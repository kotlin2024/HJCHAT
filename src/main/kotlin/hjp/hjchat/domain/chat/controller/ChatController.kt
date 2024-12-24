package hjp.hjchat.domain.chat.controller

import graphql.kickstart.tools.GraphQLMutationResolver
import graphql.kickstart.tools.GraphQLQueryResolver
import hjp.hjchat.domain.chat.dto.MessageDto
import hjp.hjchat.domain.chat.entity.ChatRoom
import hjp.hjchat.domain.chat.entity.ChatRoomMember
import hjp.hjchat.domain.chat.entity.toResponse
import hjp.hjchat.domain.chat.model.ChatRoomRepository
import hjp.hjchat.domain.chat.service.ChatService
import hjp.hjchat.infra.security.jwt.UserPrincipal
import jakarta.transaction.Transactional
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin

@CrossOrigin(origins = ["http://localhost:63342"])
@Controller
class ChatController(
    private val chatService: ChatService,
    private val chatRoomRepository: ChatRoomRepository,
    private val messagingTemplate: SimpMessagingTemplate,
) : GraphQLQueryResolver, GraphQLMutationResolver {

    @QueryMapping
    fun getChatRooms(): List<ChatRoom> {
        return chatRoomRepository.findAll()
    }

    @MessageMapping("/send")
    @Transactional
    fun sendMessage(
        @Payload message: MessageDto,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val userPrincipal = headerAccessor.sessionAttributes?.get("userPrincipal") as? UserPrincipal
            ?: throw IllegalArgumentException("UserPrincipal not found in session attributes")

        val savedMessage = chatService.processMessage(message, userPrincipal)
        messagingTemplate.convertAndSend("/topic/chatroom/${message.chatRoomId}", savedMessage.toResponse())
    }

    @MutationMapping
    @Transactional
    fun createChatRoom(
        @AuthenticationPrincipal user: UserPrincipal,
        @Argument roomName: String,
        @Argument roomType: String
    ): ChatRoom {
        return chatService.createChatRoom(user.memberId, roomName, roomType)
    }

    @MutationMapping
    fun addUser(
        @Argument chatRoomId: Long,
        @Argument userName: String,
        @AuthenticationPrincipal user: UserPrincipal
    ): ChatRoomMember {
        return chatService.addUserToChatRoom(chatRoomId, userName, user)
    }

    @MessageMapping("/join")
    fun joinChatRoom(
        @Payload roomId: Long,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val userPrincipal = headerAccessor.sessionAttributes?.get("userPrincipal") as? UserPrincipal
            ?: throw IllegalArgumentException("UserPrincipal not found in session attributes")

        if (!chatService.checkRoomAccess(roomId, userPrincipal.memberId)) {
            throw IllegalArgumentException("Access denied to the chat room.")
        }

        // Broadcast or other join-related logic can be added here.
    }
}
