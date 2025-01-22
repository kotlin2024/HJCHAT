package hjp.hjchat.infra.security.jwt.websocket


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
                println("✅ WebSocket 연결 세션 저장 - sessionId: ${session.id}")
                session.attributes[session.id] = session  // 세션 저장

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
            override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
                println("❌ WebSocket 세션 종료 - sessionId: ${session.id}")
                session.attributes.remove(session.id)  // 세션 제거
                super.afterConnectionClosed(session, closeStatus)
            }
        }
    }
}