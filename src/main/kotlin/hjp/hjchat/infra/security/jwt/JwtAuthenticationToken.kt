package hjp.hjchat.infra.security.jwt

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.web.authentication.WebAuthenticationDetails

class JwtAuthenticationToken(
    private val userPrincipal: UserPrincipal, details: WebAuthenticationDetails
) : AbstractAuthenticationToken(userPrincipal.authorities) {

    init {
        super.setAuthenticated(true)
        super.setDetails(details)
    }

    override fun getCredentials() = null

    override fun getPrincipal() = userPrincipal

    override fun isAuthenticated(): Boolean = true
}