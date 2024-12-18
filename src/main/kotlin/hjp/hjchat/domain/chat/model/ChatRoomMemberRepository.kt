package hjp.hjchat.domain.chat.model

import hjp.hjchat.domain.chat.entity.ChatRoomMember
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomMemberRepository : JpaRepository<ChatRoomMember, Long>