package hjp.hjchat.infra.security.jwt.websocket

import hjp.hjchat.infra.security.jwt.JwtAuthenticationToken
import hjp.hjchat.infra.security.jwt.JwtTokenManager
import hjp.hjchat.infra.security.jwt.UserPrincipal
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import org.springframework.stereotype.Component
import org.springframework.security.core.context.SecurityContextHolder
import java.nio.file.AccessDeniedException

@Component
class JwtWebSocketHandshakeInterceptor(
    private val jwtTokenManager: JwtTokenManager,
) : HandshakeInterceptor {

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        val queryParams = request.uri.query // Query Parameter에서 JWT 토큰 추출
        val token = queryParams?.substringAfter("token=")?.substringBefore("&")
            ?: throw AccessDeniedException("JWT Token is missing in Query Parameter")

        jwtTokenManager.validateToken(token).onSuccess { claims ->
            val memberId = claims.body.subject.toLong()
            val memberRole = claims.body.get(JwtTokenManager.MEMBER_ROLE_KEY, String::class.java)

            val userPrincipal = UserPrincipal(memberId = memberId, memberRole = setOf(memberRole))
            attributes["userPrincipal"] = userPrincipal
        }.onFailure {
            throw AccessDeniedException("Invalid JWT Token")
        }

        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
        // 필요 시 추가 처리
    }
}
