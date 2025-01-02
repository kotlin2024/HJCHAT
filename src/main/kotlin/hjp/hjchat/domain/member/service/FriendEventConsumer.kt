package hjp.hjchat.domain.member.service

import com.fasterxml.jackson.databind.ObjectMapper
import hjp.hjchat.domain.member.dto.FriendEvent
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
        try {
            println("Received Kafka message: ${record.value()}")
            val event = ObjectMapper().readValue(record.value(), FriendEvent::class.java)

            println("Parsed Kafka event: $event")

            // WebSocket 전송
            messagingTemplate.convertAndSend("/topic/friend/${event.receiverId}", event)
            println("WebSocket message sent to /topic/friend/${event.receiverId}")
        } catch (e: Exception) {
            println("Error processing Kafka message: ${record.value()} - ${e.message}")
            throw e
        }
    }

}
