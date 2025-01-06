package hjp.hjchat.domain.chat.entity

import hjp.hjchat.domain.chat.dto.MessageDto
import hjp.hjchat.domain.member.entity.MemberEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "messages")
class Message (

    @Id @GeneratedValue
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val userId : MemberEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    val chatRoom: ChatRoom,

    @Column(name="메세지_내용")
    val content: String,

    @Column(name="보낸_시간")
    val sentAt: LocalDateTime? = null, //createdAt이랑 겹치나?

    @Column(name="읽음_여부")
    var isRead: Boolean = false,

    @Column(name="전달_시간")
    var deliveredAt: LocalDateTime? = null,

    @Column(name="전달_여부")
    var isDelivered: Boolean = false,

    @Column(name="메세지_생성_날짜")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    )
fun Message.toResponse(): MessageDto {
    return MessageDto(
        id = id!!,
        content = content,
        sender =  userId.id,
        timestamp = createdAt.toString(),
        chatRoomId = chatRoom.id!!,
        senderName = userId.userName,
        profileImageUrl = userId.profileImageUrl
    )
}