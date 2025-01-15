package hjp.hjchat.infra.security.ouath.dto


data class UserInfo(
    val userId: Long,
    val memberRole: String,
    val userName: String,
    val profileImageUrl: String?,
    val createdAt: String,
    val userCode: String,
    val email: String,
)
