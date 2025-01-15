package hjp.hjchat.infra.security.jwt

import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenManager: JwtTokenManager,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        var pureToken: String? = null

        if (request.getHeader(AUTHORIZATION) != null && request.getHeader(AUTHORIZATION).startsWith("Bearer ")) {
            pureToken = request.getHeader("Authorization").substring(7)
        }

        var refreshToken: String? = null
        refreshToken = getRefreshTokenFromCookies(request)

        if (pureToken != null) {
            jwtTokenManager.validateToken(pureToken).onSuccess {

                val tokenType = it.payload.get(JwtTokenManager.TOKEN_TYPE_KEY, String::class.java)
                val memberRole = it.payload.get(JwtTokenManager.MEMBER_ROLE_KEY, String::class.java)
                val memberId: Long = it.payload.subject.toLong()

                if (tokenType == TokenType.REFRESH_TOKEN_TYPE.name) {
                    return@onSuccess
                }

                val userPrincipal = UserPrincipal(memberId = memberId, memberRole = setOf(memberRole))
                val authentication = JwtAuthenticationToken(
                    userPrincipal = userPrincipal, details = WebAuthenticationDetailsSource().buildDetails(request)
                )

                SecurityContextHolder.getContext().authentication = authentication
            }.onFailure {
                logger.debug("Token validation failed", it)
                if (it is ExpiredJwtException) {

                    if(refreshToken != null) {
                        jwtTokenManager.validateRefreshToken(refreshToken)
                        val newAccessToken = jwtTokenManager.reissueAccessToken(refreshToken)
                        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer $newAccessToken")
                        val tokenInfo = jwtTokenManager.getInfoToken(newAccessToken)
                        val userPrincipal = UserPrincipal(memberId = tokenInfo!!.memberId, memberRole = setOf(tokenInfo.memberRole))
                        val authentication = JwtAuthenticationToken(
                            userPrincipal = userPrincipal, details = WebAuthenticationDetailsSource().buildDetails(request)
                        )
                        SecurityContextHolder.getContext().authentication = authentication
                    }

                    return
                }
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증 실패")

            }
        }

        filterChain.doFilter(request, response)
    }
}

private fun getRefreshTokenFromCookies(request: HttpServletRequest): String? {
    return request.cookies
        ?.firstOrNull { it.name == "refreshToken" }
        ?.value
}
