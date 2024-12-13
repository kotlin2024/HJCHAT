package hjp.hjchat.infra.security.ouath.model

import hjp.hjchat.domain.member.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OAuthRepository: JpaRepository<MemberEntity,Long>{
    fun findByUserName(username: String): MemberEntity?
    fun existsByEmail(email: String): Boolean
    fun existsByUserName(username: String): Boolean
}