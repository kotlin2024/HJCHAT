package hjp.hjchat.domain.member.controller

import hjp.hjchat.domain.member.dto.FriendRequestDto
import hjp.hjchat.domain.member.dto.FriendShipDto
import hjp.hjchat.domain.member.service.FriendsService
import hjp.hjchat.infra.security.jwt.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/friends")
class FriendsController(
    private val friendsService: FriendsService,
) {

    @GetMapping("/get_list")
    fun getMyFriendsList(
        @AuthenticationPrincipal user: UserPrincipal
    ): ResponseEntity<List<FriendShipDto>>{
        return ResponseEntity.status(HttpStatus.OK).body(friendsService.getMyFriendsList(user))
    }

    @GetMapping("my_request")
    fun getSentFriendRequests(
        @AuthenticationPrincipal user: UserPrincipal,
    ):ResponseEntity<List<FriendShipDto>>{
        return ResponseEntity.ok(friendsService.getSentFriendRequests(user.memberId))
    }

    @GetMapping("/request")
    fun getReceivedFriendRequests(
        @AuthenticationPrincipal user: UserPrincipal
    ): ResponseEntity<List<FriendShipDto>> {
        val receivedRequests = friendsService.getReceivedFriendRequests(user.memberId)
        return ResponseEntity.ok(receivedRequests)
    }


    @PostMapping("/request")
    fun sendFriendRequest(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: FriendRequestDto
    ): ResponseEntity<FriendShipDto> {
        val friendship = friendsService.sendFriendRequest(user.memberId, request.friendId)
        return ResponseEntity.ok(friendship)
    }

    @PostMapping("/accept")
    fun acceptFriendRequest(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: FriendRequestDto
    ): ResponseEntity<FriendShipDto> {

        val friendship = friendsService.acceptFriendRequest(userId = user.memberId, senderId = request.friendId)

        return ResponseEntity.ok(friendship)
    }

    @PostMapping("/reject")
    fun rejectFriendRequest(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: FriendRequestDto
    ): ResponseEntity<Unit>{

        return ResponseEntity.ok(friendsService.rejectFriendRequest(user = user, senderId = request.friendId))
    }

}
