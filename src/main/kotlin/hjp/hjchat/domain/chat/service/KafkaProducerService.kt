package hjp.hjchat.domain.chat.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {

    fun sendMessage(topic: String, key: String, message: Any) {
        try {
            // 메시지를 JSON으로 변환
            val jsonMessage = objectMapper.writeValueAsString(message)
            kafkaTemplate.send(topic,key ,jsonMessage)
            println("Message sent to Kafka topic [$topic]: $jsonMessage")
        } catch (e: Exception) {
            println("Failed to send message to Kafka topic [$topic]: ${e.message}")
            throw e
        }
    }
}
