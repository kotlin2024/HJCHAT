package hjp.hjchat.domain.member.model

import hjp.hjchat.domain.member.entity.FriendRequest
import org.springframework.data.jpa.repository.JpaRepository

interface FriendRequestRepository: JpaRepository<FriendRequest, Long> {
    fun findByReceiverId(receiverId: Long): List<FriendRequest>
    fun findBySenderIdAndReceiverId(friendId: Long, userId: Long): FriendRequest
    fun findBySenderId(userId: Long): List<FriendRequest>
}