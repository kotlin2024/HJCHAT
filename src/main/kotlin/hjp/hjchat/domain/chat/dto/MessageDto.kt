package hjp.hjchat.domain.chat.dto


data class MessageDto(
    val id: Long,
    val content: String,
    val senderId: Long,
    val senderName: String,
    val timestamp: String?,
    val chatRoomId: Long,
    val profileImageUrl: String?,
)
