package hjp.hjchat.infra.security.ouath.model

import hjp.hjchat.domain.member.entity.MemberEntity
import hjp.hjchat.domain.member.entity.NotVerifyMemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.lang.reflect.Member

interface OAuthRepository: JpaRepository<MemberEntity,Long>{
    fun findByUserName(username: String): MemberEntity?
    fun existsByEmail(email: String): Boolean
    fun existsByUserName(username: String): Boolean
}

interface EmailVerifyRepository: JpaRepository<NotVerifyMemberEntity,Long>{

}