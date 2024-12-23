package hjp.hjchat.domain.chat.controller

import graphql.kickstart.tools.GraphQLMutationResolver
import graphql.kickstart.tools.GraphQLQueryResolver
import hjp.hjchat.domain.chat.dto.MessageDto
import hjp.hjchat.domain.chat.entity.ChatRoom
import hjp.hjchat.domain.chat.entity.ChatRoomMember
import hjp.hjchat.domain.chat.entity.Message
import hjp.hjchat.domain.chat.entity.toResponse
import hjp.hjchat.domain.chat.model.ChatRoomMemberRepository
import hjp.hjchat.domain.chat.model.ChatRoomRepository
import hjp.hjchat.domain.chat.model.MessageRepository
import hjp.hjchat.domain.member.entity.MemberEntity
import hjp.hjchat.infra.security.jwt.UserPrincipal
import hjp.hjchat.infra.security.ouath.model.OAuthRepository
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrElse

@CrossOrigin(origins = ["http://localhost:63342"])
@Controller
class ChatController(
    private val messageRepository: MessageRepository,
    private val oAuthRepository: OAuthRepository,
    private val charRoomRepository: ChatRoomRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val messagingTemplate: SimpMessagingTemplate,
) : GraphQLQueryResolver, GraphQLMutationResolver {

    @QueryMapping
    fun getMessages(): List<MessageDto> {
        return messageRepository.findAll().map { it.toResponse() }
    }


    @MessageMapping("/send")
    fun sendMessage(
        @Payload message: MessageDto,
        @AuthenticationPrincipal user: UserPrincipal
    ) {
        val chatRoom = chatRoomRepository.findById(message.chatRoomId)
            .orElseThrow { IllegalArgumentException("Chat room not found") }

        val memberId = user.memberId
        val member = oAuthRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("Member not found") }
        val savedMessage = messageRepository.save(
            Message(
                content = message.content,
                userId = member,
                chatRoom = chatRoom
            )
        )

        messagingTemplate.convertAndSend("/topic/chatroom/${message.chatRoomId}", savedMessage.toResponse())
    }

    @MutationMapping
    fun createChatRoom(
        @AuthenticationPrincipal user: UserPrincipal,
        @Argument roomName: String,
        @Argument roomType: String,
    ): ChatRoom {

        val member = oAuthRepository.findById(user.memberId)
            .orElseThrow { IllegalArgumentException("Member not found") }

        val chatRoom = chatRoomRepository.save(
            ChatRoom(
                roomName = roomName,
                roomType = roomType.toUpperCase(),
                createdAt = LocalDateTime.now(),
                updatedAt = null,
                members = mutableListOf(),
            )
        )

        chatRoomMemberRepository.save(
            ChatRoomMember(
                chatRoom = chatRoom,
                member = member,
            )
        )

        return chatRoom
    }

    @MutationMapping
    fun addUser(
        @AuthenticationPrincipal user: UserPrincipal,
        @Argument chatRoomId: Long,
    ): ChatRoomMember {
        // 사용자와 채팅방 검증
        val member = oAuthRepository.findById(user.memberId)
            .orElseThrow { IllegalArgumentException("Member not found") }

        val chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow { IllegalArgumentException("Chat room not found") }

        // 이미 사용자가 채팅방에 추가되어 있는지 확인
        if (chatRoomMemberRepository.existsByChatRoomIdAndMember(chatRoomId, member)) {
            throw IllegalArgumentException("User already in chat room")
        }

        // 사용자 추가
        val chatRoomMember = chatRoomMemberRepository.save(
            ChatRoomMember(
                chatRoom = chatRoom,
                member = member,
            )
        )

        // 사용자 입장 알림 메시지 브로드캐스트
        val joinMessage = Message(
            content = "${member.userName} 님이 채팅방에 입장하셨습니다.",
            userId = member, // 시스템 메시지로 설정
            chatRoom = chatRoom
        )

        messagingTemplate.convertAndSend("/topic/chatroom/$chatRoomId", joinMessage.toResponse())

        return chatRoomMember
    }
}