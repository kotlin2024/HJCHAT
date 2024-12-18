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
import hjp.hjchat.infra.security.jwt.UserPrincipal
import hjp.hjchat.infra.security.ouath.model.OAuthRepository
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import java.time.LocalDateTime


@Controller
class ChatController(
    private val messageRepository: MessageRepository,
    private val oAuthRepository: OAuthRepository,
    private val charRoomRepository: ChatRoomRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
) : GraphQLQueryResolver, GraphQLMutationResolver {

    @QueryMapping
    fun getMessages(): List<MessageDto> {
        return messageRepository.findAll().map { it.toResponse() }
    }

    @MutationMapping
    fun sendMessage(
        @AuthenticationPrincipal user: UserPrincipal,
        @Argument content: String,
        @Argument chatRoomId: Long
    ): MessageDto {

        val member = oAuthRepository.findById(user.memberId)
            .orElseThrow { IllegalArgumentException("Member not found") }

        val chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow { IllegalArgumentException("Chat room not found") }

        val savedMessage = messageRepository.save(
            Message(
                content = content,
                userId = member,
                chatRoom = chatRoom
            )
        )
        return savedMessage.toResponse()
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
    fun inviteUserToChatRoom(
        @AuthenticationPrincipal user: UserPrincipal,
        @Argument chatRoomId: Long,
        @Argument userId: Long,
    ): ChatRoomMember {

        val member = oAuthRepository.findById(user.memberId)
            .orElseThrow { IllegalArgumentException("Member not found") }

        val chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow { IllegalArgumentException("Chat room not found") }

        val invitedMember = oAuthRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Invited member not found") }

        return chatRoomMemberRepository.save(
            ChatRoomMember(
                chatRoom = chatRoom,
                member = invitedMember,
            )
        )
    }
}