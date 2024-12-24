package hjp.hjchat.domain.chat.service

import hjp.hjchat.domain.chat.dto.MessageDto
import hjp.hjchat.domain.chat.entity.ChatRoom
import hjp.hjchat.domain.chat.entity.ChatRoomMember
import hjp.hjchat.domain.chat.entity.Message
import hjp.hjchat.domain.chat.entity.toResponse
import hjp.hjchat.domain.chat.model.ChatRoomMemberRepository
import hjp.hjchat.domain.chat.model.ChatRoomRepository
import hjp.hjchat.domain.chat.model.MessageRepository
import hjp.hjchat.infra.security.jwt.UserPrincipal
import hjp.hjchat.infra.security.ouath.model.OAuthRepository
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
    private val oAuthRepository: OAuthRepository,
    private val messageRepository: MessageRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val messagingTemplate: SimpMessagingTemplate
) {

    fun processMessage(message: MessageDto, user: UserPrincipal): Message {
        // 채팅방 확인
        val chatRoom = chatRoomRepository.findById(message.chatRoomId)
            .orElseThrow { IllegalArgumentException("Chat room not found") }

        // 사용자 확인
        val member = oAuthRepository.findById(user.memberId)
            .orElseThrow { IllegalArgumentException("Member not found") }

        // 메시지 저장
        return messageRepository.save(
            Message(
                content = message.content,
                userId = member,
                chatRoom = chatRoom
            )
        )
    }

    fun createChatRoom(memberId: Long, roomName: String, roomType: String): ChatRoom {
        val member = oAuthRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("Member not found") }

        val chatRoom = chatRoomRepository.save(
            ChatRoom(
                roomName = roomName,
                roomType = roomType.uppercase(),
                createdAt = LocalDateTime.now(),
                updatedAt = null,
                members = mutableListOf()
            )
        )

        chatRoomMemberRepository.save(
            ChatRoomMember(
                chatRoom = chatRoom,
                member = member
            )
        )
        return chatRoom
    }

    fun addUserToChatRoom(memberId: Long, chatRoomId: Long): ChatRoomMember {
        val member = oAuthRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("Member not found") }

        val chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow { IllegalArgumentException("Chat room not found") }

        if (chatRoomMemberRepository.existsByChatRoomIdAndMember(chatRoomId, member)) {
            throw IllegalArgumentException("User already in chat room")
        }

        val chatRoomMember = chatRoomMemberRepository.save(
            ChatRoomMember(
                chatRoom = chatRoom,
                member = member
            )
        )

        val joinMessage = Message(
            content = "${member.userName} 님이 채팅방에 입장하셨습니다.",
            userId = member,
            chatRoom = chatRoom
        )

        messagingTemplate.convertAndSend("/topic/chatroom/$chatRoomId", joinMessage.toResponse())

        return chatRoomMember
    }
}
