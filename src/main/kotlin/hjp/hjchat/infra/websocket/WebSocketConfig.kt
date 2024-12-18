package hjp.hjchat.infra.websocket

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // 클라이언트가 연결할 수 있는 WebSocket 엔드포인트 설정
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS() // '/ws' 엔드포인트 설정
    }

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        // 메시지 브로커를 설정하여 메시지를 구독하는 엔드포인트와 보내는 엔드포인트를 설정
        config.enableSimpleBroker("/topic", "/queue") // 메시지 구독을 위한 엔드포인트 설정
        config.setApplicationDestinationPrefixes("/app") // 메시지 전송을 위한 엔드포인트 설정
    }
}