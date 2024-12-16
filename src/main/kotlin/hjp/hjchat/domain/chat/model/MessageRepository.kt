package hjp.hjchat.domain.chat.model

import hjp.hjchat.domain.chat.entity.Message
import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository: JpaRepository<Message, Long> {
}