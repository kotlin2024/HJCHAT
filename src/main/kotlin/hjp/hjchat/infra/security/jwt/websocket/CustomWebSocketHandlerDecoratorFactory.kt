package hjp.hjchat.infra.security.jwt.websocket

import hjp.hjchat.infra.security.jwt.JwtTokenManager
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.WebSocketHandlerDecorator
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketHandler

@Component
class CustomWebSocketHandlerDecoratorFactory : WebSocketHandlerDecoratorFactory {

    override fun decorate(handler: WebSocketHandler): WebSocketHandler {
        return object : WebSocketHandlerDecorator(handler) {
            override fun afterConnectionEstablished(session: WebSocketSession) {
                val tokenValid = session.attributes["tokenValid"] as? Boolean ?: false

                println(" ------------------tokenValid값:$tokenValid ----------------------")
                if (!tokenValid) {
                    println("🔒 유효하지 않은 토큰 - WebSocket 연결 종료")
                    session.close(CloseStatus(4001, "Invalid JWT Token"))
                    // 🔒 연결 종료
                    return
                }

                println("✅ WebSocket 연결 성공")
                super.afterConnectionEstablished(session)
            }
        }
    }
}