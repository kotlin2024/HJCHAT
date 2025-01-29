package hjp.hjchat.infra.security.ouath.dto

data class LoginRequest(
    val username: String,
    val password: String,
)