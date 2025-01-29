package hjp.hjchat.domain.chat.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class KafkaConsumerService(
    private val messagingTemplate: SimpMessagingTemplate,
) {
    private val logger = LoggerFactory.getLogger(KafkaConsumerService::class.java)

    @KafkaListener(topics = ["chat-messages"], groupId = "hjchat-consumer-group")
    fun consumeMessage(record: ConsumerRecord<String, String>) {
        try{
            logger.info("Received message: ${record.value()}")
            val messageData = record.value()
            val parsedMessage = ObjectMapper().readValue(messageData, Map::class.java)
            val chatRoomId = parsedMessage["chatRoomId"] as String
            messagingTemplate.convertAndSend("/topic/chatroom/$chatRoomId", parsedMessage)
        }
        catch (e: Exception){
            logger.error("Error processing message: ${record.value()}", e)
            throw e
        }
    }
}
