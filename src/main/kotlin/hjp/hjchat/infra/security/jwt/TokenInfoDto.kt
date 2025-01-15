package hjp.hjchat.infra.security.jwt

data class TokenInfoDto(
    val memberRole: String,
    val memberId: Long,
    val tokenExpireTime: Long,
    val issuer: String,
    val issuedAt: Long,
)
