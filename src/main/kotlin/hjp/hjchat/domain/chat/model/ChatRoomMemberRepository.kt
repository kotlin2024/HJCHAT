package hjp.hjchat.domain.chat.model

import hjp.hjchat.domain.chat.entity.ChatRoomMember
import hjp.hjchat.domain.member.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomMemberRepository : JpaRepository<ChatRoomMember, Long> {

    fun existsByChatRoomIdAndMember(chatRoomId: Long, member: MemberEntity): Boolean

    fun existsByChatRoomIdAndMemberId(chatRoomId: Long, userId: Long): Boolean

    fun findByMemberId(memberId: Long): List<ChatRoomMember>?

    fun findAllByChatRoomId(chatRoomId: Long): List<ChatRoomMember>?
}