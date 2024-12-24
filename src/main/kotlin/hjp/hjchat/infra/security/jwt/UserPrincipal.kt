package hjp.hjchat.infra.security.jwt

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

data class UserPrincipal @JsonCreator constructor(
    @JsonProperty("memberId")
    val memberId: Long,

    @JsonProperty("authorities")
    val authorities: Collection<GrantedAuthority> = emptyList() // 기본값 추가
) {

    constructor(memberId: Long, memberRole: Set<String>) : this(
        memberId,
        memberRole.map { SimpleGrantedAuthority("ROLE_$it") }
    )

    val role: String = authorities.firstOrNull()?.authority ?: "ROLE_UNKNOWN"
}
