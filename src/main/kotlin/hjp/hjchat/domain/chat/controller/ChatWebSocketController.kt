package hjp.hjchat.domain.chat.controller


import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class ChatWebSocketController(private val messagingTemplate: SimpMessagingTemplate) {

    // 클라이언트가 메시지를 보냈을 때 호출되는 메소드
    @MessageMapping("/chat.sendMessage")  // /app/chat.sendMessage로 들어오는 메시지 처리
    fun sendMessage(message: String) {
        // 받은 메시지를 /topic/messages로 브로드캐스팅
        messagingTemplate.convertAndSend("/topic/messages", message)
    }
}