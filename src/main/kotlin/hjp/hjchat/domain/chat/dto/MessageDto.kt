package hjp.hjchat.domain.chat.dto

data class MessageDto(
    val id: Long,
    val content: String,
    val sender: String,
    val timestamp: String
)
