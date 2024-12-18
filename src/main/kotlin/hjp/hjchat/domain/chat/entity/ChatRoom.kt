package hjp.hjchat.domain.chat.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_room")
class ChatRoom(

    @Id @GeneratedValue
    val id: Long? = null,

    @OneToMany(mappedBy = "chatRoom", cascade = [CascadeType.ALL], orphanRemoval = true)
    val messages: List<Message> = mutableListOf(),

    @OneToMany(mappedBy = "chatRoom")
    val members : List<ChatRoomMember>,

    @Column(name = "room_name")
    var roomName: String,

    @Column(name = "room_type")
    var roomType: String,

    @Column(name = "생성_날짜", updatable = false)
    val createdAt: LocalDateTime,

    @Column(name = "수정_날짜")
    var updatedAt: LocalDateTime? = null,


)