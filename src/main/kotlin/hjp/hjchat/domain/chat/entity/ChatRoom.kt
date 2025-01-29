package hjp.hjchat.domain.chat.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_room")
class ChatRoom(

    @Id @GeneratedValue
    val id: Long? = null,

    @OneToMany(mappedBy = "chatRoom", cascade = [CascadeType.ALL], orphanRemoval = true)
    val messages: MutableList<Message> = mutableListOf(),

    @OneToMany(mappedBy = "chatRoom", cascade = [CascadeType.ALL], orphanRemoval = true)
    val members: MutableList<ChatRoomMember> = mutableListOf(),

    @Column(name = "room_name")
    var roomName: String,

    @Column(name = "room_type")
    var roomType: String,

    @Column(name="room_password")
    var roomPassword: String? = null,

    @Column(name = "생성_날짜", updatable = false)
    val createdAt: LocalDateTime,

    @Column(name = "수정_날짜")
    var updatedAt: LocalDateTime? = null,


)