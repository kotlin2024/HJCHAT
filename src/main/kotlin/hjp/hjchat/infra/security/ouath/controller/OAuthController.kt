package hjp.hjchat.infra.security.ouath.controller

import hjp.hjchat.infra.security.jwt.TokenResponse
import hjp.hjchat.infra.security.ouath.dto.LoginRequest
import hjp.hjchat.infra.security.ouath.dto.SignUpRequest
import hjp.hjchat.infra.security.ouath.dto.UserInfo
import hjp.hjchat.infra.security.ouath.service.OAuthService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/oauth")
class OAuthController(
    private val oAuthService: OAuthService,
) {


    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUpRequest): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.OK).body(oAuthService.signUp(request))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, response: HttpServletResponse): ResponseEntity<
            Map<String, String>> {

        val tokenResponse = oAuthService.login(request)

        // ACCESS TOKEN을 헤더에 담아서 반환
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer ${tokenResponse.accessToken}")

        // REFRESH TOKEN을 쿠키에 저장
        val refreshTokenCookie = Cookie("refreshToken", tokenResponse.refreshToken).apply {
            isHttpOnly = true
            secure = true
            path = "/"
        }
        response.addCookie(refreshTokenCookie)

        val messageJson = mapOf(
            "message" to "토큰 발급 성공"
        )

        return ResponseEntity.ok()
            .headers(headers)
            .body(messageJson)
    }

    @PostMapping("/logout")
    fun logOut(
        @CookieValue("refreshToken") refreshToken: String
    ): ResponseEntity<String>{
        return ResponseEntity.status(HttpStatus.OK).body(oAuthService.logout(refreshToken))
    }

    @GetMapping("/verify-email")
    fun verifyEmail(@RequestParam token: String): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.OK).body(oAuthService.verifyEmail(token))
    }

    @PostMapping("/social-login")
    fun socialLogin() {
        //TODO()
    }
}