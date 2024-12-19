package hjp.hjchat.infra.security.jwt.websocket

import hjp.hjchat.infra.security.jwt.JwtAuthenticationToken
import hjp.hjchat.infra.security.jwt.JwtTokenManager
import hjp.hjchat.infra.security.jwt.UserPrincipal
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.stereotype.Component
import org.springframework.security.core.context.SecurityContextHolder
import java.nio.file.AccessDeniedException

@Component
class JwtWebSocketInterceptor(
    private val jwtTokenManager: JwtTokenManager,
) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val headers = message.headers
        val token = headers["simpSessionId"]?.toString() ?: return message

        jwtTokenManager.validateToken(token).onSuccess { claims ->
            val memberId = claims.body.subject.toLong()
            val memberRole = claims.body.get(JwtTokenManager.MEMBER_ROLE_KEY, String::class.java)

            val userPrincipal = UserPrincipal(memberId = memberId, memberRole = setOf(memberRole))
            val authentication = JwtAuthenticationToken(userPrincipal, null)
            SecurityContextHolder.getContext().authentication = authentication
        }.onFailure {
            throw AccessDeniedException("Invalid JWT Token")
        }

        return super.preSend(message, channel)
    }

}
