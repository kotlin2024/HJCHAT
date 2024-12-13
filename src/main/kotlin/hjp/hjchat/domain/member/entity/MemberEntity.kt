package hjp.hjchat.domain.member.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "members")
class MemberEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name="userName", unique = true, nullable = false)
    val userName: String,

    @Column(name="password", nullable = false)
    val password: String,

    @Column(name="email", unique = true, nullable = false)
    val email: String,

    @Column(name="탈퇴_유무")
    val isDeleted: Boolean = false,

    @Column(name="생성_날짜", nullable = false)
    val createdAt: LocalDate = LocalDate.now(),

    @Column(name="탈퇴_시간")
    val deletedAt: LocalDate? = null,

    @Column(name="회원_상태")
    val status: String?,
)