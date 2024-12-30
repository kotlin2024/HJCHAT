package hjp.hjchat.domain.member.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class FriendEventConsumer(
    private val messagingTemplate: SimpMessagingTemplate
) {

    @KafkaListener(topics = ["friend-events"], groupId = "hjchat-consumer-group")
    fun consumeFriendEvent(record: ConsumerRecord<String, String>) {
        val eventData = record.value()
        val parsedEvent = ObjectMapper().readValue(eventData, Map::class.java)
        val receiverId = parsedEvent["receiverId"] as String

        // WebSocket을 통해 친구 요청 알림 전송
        messagingTemplate.convertAndSend("/topic/friend/$receiverId", parsedEvent)
    }
}
