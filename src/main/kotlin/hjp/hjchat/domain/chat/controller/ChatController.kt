package hjp.hjchat.domain.chat.controller

import graphql.GraphQLContext
import graphql.kickstart.tools.GraphQLMutationResolver
import graphql.kickstart.tools.GraphQLQueryResolver
import hjp.hjchat.domain.chat.dto.MessageDto
import hjp.hjchat.domain.chat.entity.Message
import hjp.hjchat.domain.chat.entity.toResponse
import hjp.hjchat.domain.chat.model.MessageRepository
import hjp.hjchat.infra.security.jwt.JwtTokenManager
import hjp.hjchat.infra.security.ouath.model.OAuthRepository
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

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
        @Argument content: String,
        graphQlContext: GraphQLContext
    ): MessageDto {

        val authorizationHeader = graphQlContext.getOrDefault<String>("Authorization", "")
        val token = authorizationHeader.removePrefix("Bearer ").trim()
        val userId = jwtTokenManager.extractUserId(token)

        val user = oAuthRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Member not found with id: $userId") }



        val savedMessage = messageRepository.save(
            Message(
                content = content,
                userId = user,
            )
        )
        return savedMessage.toResponse()
    }
}