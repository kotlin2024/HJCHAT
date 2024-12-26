package hjp.hjchat.domain.member.entity

import hjp.hjchat.domain.member.dto.FriendshipStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "friendships")
class Friendship(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: MemberEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    val friend: MemberEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: FriendshipStatus = FriendshipStatus.PENDING,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
