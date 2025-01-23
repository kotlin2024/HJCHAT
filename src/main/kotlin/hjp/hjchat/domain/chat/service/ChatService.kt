package hjp.hjchat.domain.chat.service

import hjp.hjchat.domain.chat.dto.ChatRoomMemberList
import hjp.hjchat.domain.chat.dto.HasAccessDto
import hjp.hjchat.domain.chat.dto.MessageDto
import hjp.hjchat.domain.chat.entity.ChatRoom
import hjp.hjchat.domain.chat.entity.ChatRoomMember
import hjp.hjchat.domain.chat.entity.Message
import hjp.hjchat.domain.chat.entity.toResponse
import hjp.hjchat.domain.chat.model.ChatRoomMemberRepository
import hjp.hjchat.domain.chat.model.ChatRoomRepository
import hjp.hjchat.domain.chat.model.MessageRepository
import hjp.hjchat.infra.s3.S3Service
import hjp.hjchat.infra.security.jwt.JwtTokenManager
import hjp.hjchat.infra.security.jwt.UserPrincipal
import hjp.hjchat.infra.security.ouath.model.OAuthRepository
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import java.time.LocalDateTime

@Service
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
    private val messageRepository: MessageRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val oAuthRepository: OAuthRepository,
    private val messagingTemplate: SimpMessagingTemplate,
    private val kafkaProducerService: KafkaProducerService,
    private val s3Service: S3Service,
    private val jwtTokenManager: JwtTokenManager,
) {

    fun getChatRoom(): List<ChatRoom>{
        return chatRoomRepository.findAll()
    }

    fun getAccessPrivateChatRoom(user: UserPrincipal): List<ChatRoom>{
        val chatMember =chatRoomMemberRepository.findByMemberId(user.memberId) ?: return listOf()

        return chatMember
            .filter{ it.chatRoom.roomType == "PRIVATE"}
            .map{ it.chatRoom }
    }

    fun getChatRoomMessages(chatRoomId: Long): List<MessageDto> {
        val chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow { IllegalArgumentException("Chat room not found") }

        val allMessages = messageRepository.findAllByChatRoom(chatRoom)
            ?: throw IllegalArgumentException("해당 채팅방에 어떤 메세지도 없음")

        return allMessages.map { it.toResponse() }
    }

    fun processMessage(message: MessageDto, user: UserPrincipal): Message {
        val chatRoom = chatRoomRepository.findById(message.chatRoomId)
            .orElseThrow { IllegalArgumentException("Chat room not found") }

        val member = oAuthRepository.findById(user.memberId)
            .orElseThrow { IllegalArgumentException("Member not found") }

       // Kafka 메시지 전송
        kafkaProducerService.sendMessage(
            "chat-messages", mapOf(
                "chatRoomId" to message.chatRoomId.toString(),
                "senderName" to member.userName,
                "senderId" to member.id,
                "content" to message.content,
                "profileImageUrl" to member.profileImageUrl
            )
        )

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

    fun addUserToChatRoom(chatRoomId: Long, userCode: String, inviter: UserPrincipal): ChatRoomMember {
        val chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow { IllegalArgumentException("Chat room not found") }

        val invitedMember = oAuthRepository.findByUserCode(userCode)
            ?: throw IllegalArgumentException("$userCode does not found")

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
            chatRoom = chatRoom,
        ) // TODO() 해당 부분 추후에 수정할것
        messageRepository.save(inviteMessage)
        messagingTemplate.convertAndSend("/topic/chatroom/$chatRoomId", inviteMessage.toResponse())

        return chatRoomMember
    }

    fun checkRoomAccess(chatRoomId: Long, userId: Long): HasAccessDto {
        val chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow { IllegalArgumentException("Chat room not found") }

        return if(chatRoom.roomType.uppercase() == "PUBLIC" || chatRoomMemberRepository.existsByChatRoomIdAndMemberId(chatRoomId, userId))
            HasAccessDto(hasAccess = true)
        else
            HasAccessDto(hasAccess = false)

    }

    fun handlePing(
        message: Map<String, String>,
        headerAccessor: SimpMessageHeaderAccessor) {

        val token = message["token"] ?: throw IllegalArgumentException("Token is missing")

        jwtTokenManager.validateToken(token).onFailure { exception ->
            if (exception is ExpiredJwtException) {
                println("🔒 AccessToken 만료 - 4001 코드로 연결 종료")
                val sessionId = headerAccessor.sessionId
                val session = headerAccessor.sessionAttributes?.get(sessionId) as? WebSocketSession
                    ?: throw IllegalStateException("WebSocketSession not found for sessionId: $sessionId")

                session.close(CloseStatus(4001, "Expired JWT Token"))
                return
            } else {
                throw IllegalArgumentException("Invalid Token")
            }
        }

        println("✅ Ping 처리 완료")
    }

    fun getChatRoomMembers(chatRoomId: Long, user: UserPrincipal): List<ChatRoomMemberList>? {

        val roomMembers = chatRoomMemberRepository.findAllByChatRoomId(chatRoomId)
            ?: throw IllegalArgumentException("해당 채팅방에 아무도 존재하지않음")
        return roomMembers.map{
            ChatRoomMemberList(
                roomMemberId = it.member.id,
                roomMemberCode = it.member.userCode!!,
            )
        }
    }
}