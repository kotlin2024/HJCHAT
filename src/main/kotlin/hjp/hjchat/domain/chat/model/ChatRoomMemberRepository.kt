package hjp.hjchat.domain.chat.model

import hjp.hjchat.domain.chat.entity.ChatRoomMember
import hjp.hjchat.domain.member.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomMemberRepository : JpaRepository<ChatRoomMember, Long> {

    fun existsByChatRoomIdAndMember(chatRoomId: Long, member: MemberEntity): Boolean
}