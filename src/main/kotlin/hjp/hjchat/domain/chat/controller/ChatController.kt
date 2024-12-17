package hjp.hjchat.domain.chat.controller

import graphql.GraphQLContext
import graphql.kickstart.tools.GraphQLMutationResolver
import graphql.kickstart.tools.GraphQLQueryResolver
import hjp.hjchat.domain.chat.dto.MessageDto
import hjp.hjchat.domain.chat.entity.Message
import hjp.hjchat.domain.chat.entity.toResponse
import hjp.hjchat.domain.chat.model.MessageRepository
import hjp.hjchat.infra.security.jwt.JwtTokenManager
import hjp.hjchat.infra.security.jwt.UserPrincipal
import hjp.hjchat.infra.security.ouath.model.OAuthRepository
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Controller
import kotlin.jvm.optionals.getOrNull

@Controller
class ChatController(
    private val messageRepository: MessageRepository,
    private val oAuthRepository: OAuthRepository,
    private val jwtTokenManager: JwtTokenManager,
) : GraphQLQueryResolver, GraphQLMutationResolver {

    @QueryMapping
    fun getMessages(): List<MessageDto> {
        return messageRepository.findAll().map { it.toResponse() }
    }

    @MutationMapping
    fun sendMessage(
        @AuthenticationPrincipal user: UserPrincipal, // 인증된 사용자 정보
        @Argument content: String
    ): MessageDto {

        val member = oAuthRepository.findById(user.memberId)
            .orElseThrow { IllegalArgumentException("Member not found") }

        val savedMessage = messageRepository.save(
            Message(
                content = content,
                userId = member
            )
        )
        return savedMessage.toResponse()
    }
}