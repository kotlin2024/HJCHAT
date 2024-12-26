package hjp.hjchat.domain.member.model

import hjp.hjchat.domain.member.entity.Friendship
import hjp.hjchat.domain.member.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FriendshipRepository : JpaRepository<Friendship, Long> {
    fun existsByUserAndFriend(user: MemberEntity, friend: MemberEntity): Boolean

    fun findByUserIdAndFriendId(userId: Long, friendId: Long): Friendship?
}
