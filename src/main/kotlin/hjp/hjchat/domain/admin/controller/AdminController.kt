package hjp.hjchat.domain.admin.controller

import hjp.hjchat.domain.admin.service.AdminService
import hjp.hjchat.infra.security.ouath.dto.SignUpRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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

    @DeleteMapping("/all_chat_rooms")
    fun deleteAllChatRooms(): String{
        adminService.deleteAllChatRooms()
        return "모든 채팅방 삭제 성공"
    }

    @DeleteMapping("chat_rooms/{roomName}")
    fun deleteChatRooms(@PathVariable roomName: String): String{
        adminService.deleteChatRooms(roomName)
        return " 삭제한 채팅방 : $roomName "
    }

    @GetMapping("/check")
    fun checkingUpdateServer(): String{
        return "1월28일 4시에 업데이트한 서버파일입니다."
    }
}