package hjp.hjchat.infra.security.ouath.dto

import hjp.hjchat.domain.member.dto.MemberRole

data class UserInfo(
    val userId: Long,
    val memberRole: String,
    val userName: String,
)
