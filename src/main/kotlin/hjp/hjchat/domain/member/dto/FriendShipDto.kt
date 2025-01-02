package hjp.hjchat.domain.member.dto

data class FriendShipDto(
    val userId: Long,
    val friendId: Long,
    val friendCode: String,
    val senderName: String,
    val status: String
)
