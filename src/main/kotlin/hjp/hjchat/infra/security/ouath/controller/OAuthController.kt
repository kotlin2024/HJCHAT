package hjp.hjchat.infra.security.ouath.controller

import hjp.hjchat.infra.security.ouath.dto.LoginRequest
import hjp.hjchat.infra.security.ouath.dto.SignUpRequest
import hjp.hjchat.infra.security.ouath.service.OAuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/oauth")
class OAuthController(
    private val oAuthService: OAuthService,
) {

    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUpRequest):ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.OK).body(oAuthService.signUp(request))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest) {

    }

    @PostMapping("/social-login")
    fun socialLogin(){
        //TODO()
    }
}