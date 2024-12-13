package hjp.hjchat.infra.security.ouath.service

import hjp.hjchat.domain.member.dto.MemberRole
import hjp.hjchat.domain.member.entity.MemberEntity
import hjp.hjchat.exception.DuplicateEmailException
import hjp.hjchat.exception.DuplicateUsernameException
import hjp.hjchat.exception.PasswordMismatchException
import hjp.hjchat.infra.security.jwt.JwtTokenManager
import hjp.hjchat.infra.security.jwt.TokenResponse
import hjp.hjchat.infra.security.ouath.dto.LoginRequest
import hjp.hjchat.infra.security.ouath.dto.SignUpRequest
import hjp.hjchat.infra.security.ouath.model.OAuthRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.security.crypto.password.PasswordEncoder

@Service
class OAuthService(
    private val oAuthRepository: OAuthRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenManager: JwtTokenManager
) {

    @Transactional
    fun signUp(request: SignUpRequest): String {

        if (oAuthRepository.existsByUserName(request.username))
            throw DuplicateUsernameException("이미 존재하는 닉네임")

        if (oAuthRepository.existsByEmail(request.email))
            throw DuplicateEmailException("존재하는 이메일")

        if (request.password != request.confirmPassword)
            throw PasswordMismatchException("비밀번호와 확인 비밀번호가 일치하지 않습니다.")



        oAuthRepository.save(
            MemberEntity(
                userName = request.username,
                email = request.email,
                password = passwordEncoder.encode(request.password),
                status = "ACTIVE",
                memberRole = MemberRole.USER.name
            )
        )

        return "회원가입 성공"
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
}