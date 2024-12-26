package hjp.hjchat.domain.member.dto

data class FriendShipDto(
    val userId: Long,
    val friendId: Long,
    val senderName: String,
    val status: String // 요청 상태: "REQUESTED", "ACCEPTED", "REJECTED"
)
