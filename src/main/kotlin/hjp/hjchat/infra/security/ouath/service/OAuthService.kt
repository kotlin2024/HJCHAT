package hjp.hjchat.infra.security.ouath.service

import hjp.hjchat.domain.member.dto.MemberRole
import hjp.hjchat.domain.member.entity.MemberEntity
import hjp.hjchat.domain.member.entity.NotVerifyMemberEntity
import hjp.hjchat.infra.redis.TokenBlacklistRepository
import hjp.hjchat.infra.security.exception.DuplicateEmailException
import hjp.hjchat.infra.security.exception.DuplicateUsernameException
import hjp.hjchat.infra.security.exception.PasswordMismatchException
import hjp.hjchat.infra.security.jwt.JwtTokenManager
import hjp.hjchat.infra.security.jwt.TokenResponse
import hjp.hjchat.infra.security.ouath.dto.LoginRequest
import hjp.hjchat.infra.security.ouath.dto.SignUpRequest
import hjp.hjchat.infra.security.ouath.model.EmailVerifyRepository
import hjp.hjchat.infra.security.ouath.model.OAuthRepository
import hjp.hjchat.infra.security.smtp.EmailService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.security.crypto.password.PasswordEncoder

@Service
class OAuthService(
    private val oAuthRepository: OAuthRepository,
    private val emailVerifyRepository: EmailVerifyRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenManager: JwtTokenManager,
    private val emailService: EmailService,
    private val tokenBlacklistRepository: TokenBlacklistRepository,
) {

    @Transactional
    fun signUp(request: SignUpRequest): String {

        if (oAuthRepository.existsByUserName(request.username))
            throw DuplicateUsernameException("이미 존재하는 닉네임입니다.")

        if (oAuthRepository.existsByEmail(request.email))
            throw DuplicateEmailException("이미 존재하는 이메일입니다.")

        if (request.password != request.confirmPassword)
            throw PasswordMismatchException("비밀번호와 확인 비밀번호가 일치하지 않습니다.")


        val imsiMember = emailVerifyRepository.save(
            NotVerifyMemberEntity(
                userName = request.username,
                email = request.email,
                password = request.password,
            )
        )
        val imsiToken =jwtTokenManager.generateTokenResponse(imsiMember.id, MemberRole.USER.name).accessToken

        emailService.sendVerificationEmail(request.email, imsiToken)
        return "이메일로 인증코드를 발송하였습니다."
    }
    fun verifyEmail(token: String): String {
        // 이메일 인증을 위해 받은 token을 검증
        val parsedToken = jwtTokenManager.validateToken(token).getOrElse {
            throw IllegalArgumentException("유효하지 않은 토큰입니다. 다시 확인해주세요.")
        }
        val memberId = parsedToken.body.subject.toLong()

        val member = emailVerifyRepository.findById(memberId).orElseThrow{
            IllegalArgumentException("해당 ID의 인증 대기 유저를 찾을 수 없습니다.")
        }

        oAuthRepository.save(
            MemberEntity(
                userName = member.userName,
                email = member.email,
                password = passwordEncoder.encode(member.password),
                status = "ACTIVE",
                memberRole = "USER",
                chatRoomMembers = mutableListOf()
            )
        )
        emailVerifyRepository.delete(member)
        return "이메일 인증이 완료되었습니다."
    }


    fun login(request: LoginRequest): TokenResponse {
        val loginMember = oAuthRepository.findByUserName(request.username) ?: throw IllegalStateException("존재하지 않는 유저")
        check(
            passwordEncoder.matches(
                request.password,
                loginMember.password
            )
        ) { "비밀번호가 맞지 않음" }
        val tokens = jwtTokenManager.generateTokenResponse(loginMember.id, loginMember.memberRole)

        return tokens
    }

    fun logout(refreshToken: String): String {

        jwtTokenManager.validateRefreshToken(refreshToken)

        val expirationTime = jwtTokenManager.getExpiration(refreshToken)
        val now =  System.currentTimeMillis()
        val ttl = (expirationTime - now)
        tokenBlacklistRepository.addToBlacklist(refreshToken, ttl)
        return "로그아웃 성공"
    }
}