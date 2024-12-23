package hjp.hjchat.domain.chat.dto

import hjp.hjchat.domain.chat.entity.ChatRoom

data class MessageDto(
    val id: Long,
    val content: String,
    val sender: Long,
    val timestamp: String,
    val chatRoomId: Long,
)
