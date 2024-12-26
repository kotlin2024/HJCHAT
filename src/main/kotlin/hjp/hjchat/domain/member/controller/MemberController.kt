package hjp.hjchat.domain.member.controller

import hjp.hjchat.domain.member.service.MemberService
import hjp.hjchat.infra.security.jwt.UserPrincipal
import hjp.hjchat.infra.security.ouath.dto.UserInfo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member")
class MemberController(
    private val memberService: MemberService,
) {

    @GetMapping("/get_user")
    fun getCurrentUser(
        @AuthenticationPrincipal user: UserPrincipal
    ): ResponseEntity<UserInfo> {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getUserInfo(user))
    }
}