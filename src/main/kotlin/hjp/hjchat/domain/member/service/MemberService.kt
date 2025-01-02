package hjp.hjchat.domain.member.service

import hjp.hjchat.infra.security.jwt.UserPrincipal
import hjp.hjchat.infra.security.ouath.dto.UserInfo
import hjp.hjchat.infra.security.ouath.model.OAuthRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class MemberService(
    private val oAuthRepository: OAuthRepository,
) {


    fun getUserInfo(user: UserPrincipal): UserInfo {

        val member = oAuthRepository.findById(user.memberId)
            .getOrNull()
        if(member == null){
            throw IllegalStateException("존재하지 않는 유저")
        }

        return UserInfo(
            userId = user.memberId,
            userName = member.userName,
            memberRole = member.memberRole
        )
    }
}