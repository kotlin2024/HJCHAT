package hjp.hjchat.domain.chat.model

import hjp.hjchat.domain.chat.entity.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomRepository: JpaRepository<ChatRoom, Long>{

    fun deleteByRoomName(roomName: String)
}