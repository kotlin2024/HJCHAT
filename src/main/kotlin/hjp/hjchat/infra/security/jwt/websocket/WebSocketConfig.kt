package hjp.hjchat.infra.security.jwt.websocket

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.slf4j.LoggerFactory

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(private val jwtWebSocketHandshakeInterceptor: JwtWebSocketHandshakeInterceptor) : WebSocketMessageBrokerConfigurer {

    val logger = LoggerFactory.getLogger(WebSocketConfig::class.java)

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic")
        config.setApplicationDestinationPrefixes("/app")
    }


    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        logger.info("Registering WebSocket endpoint at /ws")
        registry.addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:63342", "http://localhost:8080")
            .addInterceptors(jwtWebSocketHandshakeInterceptor)  // HandshakeInterceptor 등록
            .withSockJS()
    }
}
