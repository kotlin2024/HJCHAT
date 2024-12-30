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
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
    private val messageRepository: MessageRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val oAuthRepository: OAuthRepository,
    private val messagingTemplate: SimpMessagingTemplate,
    private val kafkaProducerService: KafkaProducerService,
) {


    fun processMessage(message: MessageDto, user: UserPrincipal): Message {
        val chatRoom = chatRoomRepository.findById(message.chatRoomId)
            .orElseThrow { IllegalArgumentException("Chat room not found") }

        val member = oAuthRepository.findById(user.memberId)
            .orElseThrow { IllegalArgumentException("Member not found") }

        // Kafka 메시지 전송
        kafkaProducerService.sendMessage("chat-messages", mapOf(
            "chatRoomId" to message.chatRoomId.toString(),
            "sender" to member.userName,
            "content" to message.content
        ))

        return messageRepository.save(
            Message(
                content = message.content,
                userId = member,
                chatRoom = chatRoom,
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
                members = mutableListOf()
            )
        )

        chatRoomMemberRepository.save(
            ChatRoomMember(
                chatRoom = chatRoom,
                member = member,
                joinedAt = LocalDateTime.now()
            )
        )

        return chatRoom
    }

    fun addUserToChatRoom(chatRoomId: Long, userName: String, inviter: UserPrincipal): ChatRoomMember {
        val chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow { IllegalArgumentException("Chat room not found") }

        val invitedMember = oAuthRepository.findByUserName(userName)
            ?: throw IllegalArgumentException("User not found")

        if (chatRoomMemberRepository.existsByChatRoomIdAndMember(chatRoomId, invitedMember)) {
            throw IllegalArgumentException("User is already in the chat room")
        }

        val chatRoomMember = chatRoomMemberRepository.save(
            ChatRoomMember(
                chatRoom = chatRoom,
                member = invitedMember,
                joinedAt = LocalDateTime.now()
            )
        )

        val inviteMessage = Message(
            content = "${invitedMember.userName} has been invited to the chat room.",
            userId = invitedMember,
            chatRoom = chatRoom
        )
        messageRepository.save(inviteMessage)
        messagingTemplate.convertAndSend("/topic/chatroom/$chatRoomId", inviteMessage.toResponse())

        return chatRoomMember
    }

    fun checkRoomAccess(chatRoomId: Long, userId: Long): Boolean {
        val chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow { IllegalArgumentException("Chat room not found") }

        return when (chatRoom.roomType.uppercase()) {
            "PUBLIC" -> true
            "PRIVATE" -> chatRoomMemberRepository.existsByChatRoomIdAndMemberId(chatRoomId, userId)
            else -> throw IllegalArgumentException("Invalid room type")
        }
    }
}
