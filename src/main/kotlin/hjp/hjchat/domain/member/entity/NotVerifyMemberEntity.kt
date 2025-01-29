package hjp.hjchat.domain.member.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "not_verify_members")
class NotVerifyMemberEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name="userName", unique = true, nullable = false)
    val userName: String,

    @Column(name="password", nullable = false)
    val password: String,

    @Column(name="email", unique = true, nullable = false)
    val email: String,

    @Column(name="생성_날짜", nullable = false)
    val createdAt: LocalDate = LocalDate.now(),

    @Column(name="인증_상태")
    val verify: Boolean =false,

)