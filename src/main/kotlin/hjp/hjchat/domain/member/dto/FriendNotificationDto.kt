package hjp.hjchat.domain.member.dto

data class FriendNotificationDto(
    val type: String,  // "REQUEST" 또는 "ACCEPT"
    val senderName: String
)
