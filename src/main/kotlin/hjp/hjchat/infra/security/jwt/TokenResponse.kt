package hjp.hjchat.infra.security.jwt

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)