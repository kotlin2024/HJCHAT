package hjp.hjchat.domain.chat.entity

import hjp.hjchat.domain.member.entity.MemberEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_room_member")
class ChatRoomMember(

    @Id @GeneratedValue
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var chatRoom: ChatRoom,

    @ManyToOne(fetch = FetchType.LAZY)
    var member: MemberEntity,

    @Column(name= "채팅방_참여_시간")
    var joinedAt: LocalDateTime = LocalDateTime.now(),
    )