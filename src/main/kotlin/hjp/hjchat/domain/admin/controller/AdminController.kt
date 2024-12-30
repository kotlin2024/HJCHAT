package hjp.hjchat.domain.admin.controller

import hjp.hjchat.domain.admin.service.AdminService
import hjp.hjchat.infra.security.ouath.dto.SignUpRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController(
    private val adminService: AdminService,
) {

    @DeleteMapping("/friend_request")
    fun deleteAllFriendRequestEntity(): String{
        adminService.deleteAllFriendRequestEntity()
        return "삭제 성공"
    }

    @DeleteMapping("/friend_ship")
    fun deleteAllFriendshipEntity():String{
        adminService.deleteAllFriendshipEntity()
        return "삭제 성공"
    }

    @PostMapping("/signup_no_email")
    fun adminSignupExceptEmailVerification(dto: SignUpRequest): String{
        return adminService.signUp(dto)
    }
}