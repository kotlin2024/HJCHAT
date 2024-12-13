package hjp.hjchat.infra.security.ouath.dto

data class SignUpRequest(
    val username: String,
    val password: String,
    val confirmPassword: String,
    val email: String,
)
