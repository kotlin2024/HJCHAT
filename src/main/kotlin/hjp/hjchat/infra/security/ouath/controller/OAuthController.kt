package hjp.hjchat.infra.security.ouath.controller

import hjp.hjchat.infra.security.ouath.service.OAuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/api/oauth")
class OAuthController(
    private val oAuthService: OAuthService,
) {

    @PostMapping("/signup")
    fun signUp(){

    }

    @PostMapping("/login")
    fun login(){

    }

    @PostMapping("/social-login")
    fun socialLogin(){

    }
}