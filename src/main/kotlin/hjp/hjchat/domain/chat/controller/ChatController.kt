package hjp.hjchat.domain.chat.controller

import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class ChatController {

    @QueryMapping
    fun hello(): String {
        return "Hello, GraphQL!"
    }
}