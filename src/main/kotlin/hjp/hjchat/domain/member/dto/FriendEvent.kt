package hjp.hjchat.domain.member.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class FriendEvent @JsonCreator constructor(
    @JsonProperty("type") val type: String,
    @JsonProperty("senderId") val senderId: Long,
    @JsonProperty("receiverId") val receiverId: Long,
    @JsonProperty("senderName") val senderName: String,
)