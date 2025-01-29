package hjp.hjchat.domain.member.entity


import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "friend_requests")
class FriendRequest(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    val sender: MemberEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    val receiver: MemberEntity,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: RequestStatus = RequestStatus.PENDING,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class RequestStatus {
    PENDING, // 요청 중
    ACCEPTED, // 요청 수락됨
    REJECTED // 요청 거절됨
}
