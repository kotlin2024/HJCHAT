package hjp.hjchat.infra.security.jwt

import hjp.hjchat.infra.redis.TokenBlacklistRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date

@Component
class JwtTokenManager(
    @Value("\${auth.jwt.issuer}") private var issuer: String,
    @Value("\${auth.jwt.secret}") private var secret: String,
    private val tokenBlacklistRepository: TokenBlacklistRepository,
) {

    companion object {
        const val TOKEN_TYPE_KEY = "tokenType"
        const val MEMBER_ROLE_KEY = "memberRole"
    }

    private val accessTokenValidity = 60 * 1000
    private val refreshTokenValidity = 7 * 24 * 3600 * 1000

    private val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun generateTokenResponse(memberId: Long, memberRole: String): TokenResponse {
        return TokenResponse(
            accessToken = generateToken(
                memberId.toString(),
                memberRole,
                TokenType.ACCESS_TOKEN_TYPE.name,
                accessTokenValidity
            ),
            refreshToken = generateToken(
                memberId.toString(),
                memberRole,
                TokenType.REFRESH_TOKEN_TYPE.name,
                refreshTokenValidity
            )
        )
    }

    private fun generateToken(subject: String, memberRole: String, tokenType: String, expirationTime: Int): String {
        val claims: Claims =
            Jwts.claims().add(mapOf(MEMBER_ROLE_KEY to memberRole, TOKEN_TYPE_KEY to tokenType)).build()

        val now = Instant.now()
        val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

        return Jwts.builder()
            .subject(subject)
            .issuer(issuer)
            .issuedAt(Date.from(now))
            .expiration(Date(System.currentTimeMillis() + expirationTime))
            .claims(claims)
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Result<Jws<Claims>> {
        return kotlin.runCatching {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
        }
    }

    fun validateRefreshToken(refreshToken: String){
        validateToken(refreshToken)
            .onSuccess {
                try{
                    if(tokenBlacklistRepository.isBlacklisted(refreshToken)){
                        throw IllegalStateException("토큰이 블랙리스트에 등록됨 " )
                    }
                } catch(e: Exception){
                    throw IllegalStateException("Redis 연결 실패: ${e.message}")
                }
            if(it.payload[TOKEN_TYPE_KEY] == TokenType.ACCESS_TOKEN_TYPE){
                throw IllegalStateException(" refreshToken이 아닌 AccessToken입니다")
            }
        }
            .onFailure {
                throw IllegalStateException("refreshToken 올바르지 않음 ${it.message}")
        }
    }

    fun reissueAccessToken(refreshToken: String): String{
        val tokenInfo = validateToken(refreshToken).getOrNull()
        val memberId = tokenInfo!!.payload.subject.toLong()
        val memberRole = tokenInfo.payload[MEMBER_ROLE_KEY] as String
        return generateTokenResponse(memberId = memberId, memberRole = memberRole).accessToken
    }

    fun getInfoToken(token: String): TokenInfoDto?{
        validateToken(token).onSuccess {
            return TokenInfoDto(
                memberRole = it.payload[MEMBER_ROLE_KEY] as String,
                memberId = it.payload.subject.toLong(),
                tokenExpireTime = it.payload.expiration.time,
                issuer = it.payload.issuer,
                issuedAt = it.payload.issuedAt.time
            )
        }
        return null
    }


    fun extractUserId(token: String): Long {
        val claims = validateToken(token).getOrThrow().payload
        return claims.subject.toLong()
    }
    fun getExpiration(refreshToken: String): Long{
        val token = validateToken(refreshToken).getOrNull()
        return token!!.payload.expiration.time
    }
}