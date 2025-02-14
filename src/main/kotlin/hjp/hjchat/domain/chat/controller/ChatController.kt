package hjp.hjchat.domain.chat.controller

import graphql.kickstart.tools.GraphQLMutationResolver
import graphql.kickstart.tools.GraphQLQueryResolver
import hjp.hjchat.domain.chat.dto.ChatRoomMemberList
import hjp.hjchat.domain.chat.dto.HasAccessDto
import hjp.hjchat.domain.chat.dto.MessageDto
import hjp.hjchat.domain.chat.entity.ChatRoom
import hjp.hjchat.domain.chat.entity.ChatRoomMember
import hjp.hjchat.domain.chat.service.ChatService
import hjp.hjchat.infra.security.jwt.UserPrincipal
import jakarta.transaction.Transactional
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class ChatController(
    private val chatService: ChatService,
) : GraphQLQueryResolver, GraphQLMutationResolver {

    @QueryMapping
    fun getChatRooms(): List<ChatRoom> {
        return chatService.getChatRoom()
    }

    @QueryMapping
    fun getAccessPrivateChatRoom(
        @AuthenticationPrincipal user: UserPrincipal
    ): List<ChatRoom>{
        return chatService.getAccessPrivateChatRoom(user)
    }

    @GetMapping("/chatroom/{chatRoomId}/messages")
    fun getChatRoomMessages(@PathVariable chatRoomId: Long): ResponseEntity<List<MessageDto>> {
        val messages = chatService.getChatRoomMessages(chatRoomId)
        return ResponseEntity.ok(messages)
    }


    @MessageMapping("/send")
    @Transactional
    fun sendMessage(
        @Payload message: MessageDto,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val userPrincipal = headerAccessor.sessionAttributes?.get("userPrincipal") as? UserPrincipal
            ?: throw IllegalArgumentException("UserPrincipal not found in session attributes")

        chatService.processMessage(message, userPrincipal)

    }

    @MutationMapping
    @Transactional
    fun createChatRoom(
        @AuthenticationPrincipal user: UserPrincipal,
        @Argument roomName: String,
        @Argument roomType: String,
    ): ChatRoom {
        return chatService.createChatRoom(user.memberId, roomName, roomType)
    }

    @MutationMapping
    fun addUser(
        @Argument chatRoomId: Long,
        @Argument userCode: String,
        @AuthenticationPrincipal user: UserPrincipal
    ): ChatRoomMember {
        return chatService.addUserToChatRoom(chatRoomId, userCode, user)
    }

    @PostMapping("chatRoom/{chatRoomId}/join")
    fun joinChatRoom(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable chatRoomId: Long,
    ): ResponseEntity<HasAccessDto> {
        return ResponseEntity.ok(chatService.checkRoomAccess(chatRoomId, user.memberId))
    }

    @GetMapping("chatRoom/{chatRoomId}/members")
    fun getChatRoomMembers(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable chatRoomId: Long,
    ):ResponseEntity<List<ChatRoomMemberList>>{
        return ResponseEntity.ok(chatService.getChatRoomMembers(chatRoomId, user))
    }

    @MessageMapping("/ping")
    fun handlePing(
        @Payload message: Map<String, String>, headerAccessor: SimpMessageHeaderAccessor) {
        chatService.handlePing(message, headerAccessor)
    }


}
