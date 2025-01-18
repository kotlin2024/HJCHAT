package hjp.hjchat.infra.security.jwt.websocket

import hjp.hjchat.infra.security.jwt.JwtAuthenticationToken
import hjp.hjchat.infra.security.jwt.JwtTokenManager
import hjp.hjchat.infra.security.jwt.UserPrincipal
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import org.springframework.stereotype.Component
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
        val queryParams = request.uri.query
        val token = queryParams?.substringAfter("token=")?.substringBefore("&")

        if (token.isNullOrBlank()) {
            throw AccessDeniedException("JWT Token is missing in query parameters")
        }

        jwtTokenManager.validateToken(token).onSuccess { claims ->
            val memberId = claims.body.subject.toLong()
            val memberRole = claims.body.get("memberRole", String::class.java)

            val userPrincipal = UserPrincipal(
                memberId = memberId,
                memberRole = setOf(memberRole)
            )


            val authentication = JwtAuthenticationToken(
                userPrincipal = userPrincipal,
                details = null
            )

            SecurityContextHolder.getContext().authentication = authentication
            attributes["userPrincipal"] = userPrincipal
        }.onFailure {
            if(it is ExpiredJwtException) {
                attributes["tokenValid"] = false  // ❗ 유효하지 않은 토큰
            }
            else{
                throw AccessDeniedException("요효하지 않는 토큰")
            }
        }

        return true
    }


    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {

    }
}
