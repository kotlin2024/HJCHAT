package hjp.hjchat.domain.member.controller

import hjp.hjchat.domain.member.dto.FriendNotificationDto
import hjp.hjchat.domain.member.dto.FriendRequestDto
import hjp.hjchat.domain.member.dto.FriendShipDto
import hjp.hjchat.domain.member.service.FriendsService
import hjp.hjchat.domain.member.service.MemberService
import hjp.hjchat.infra.security.jwt.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/friends")
class FriendsController(
    private val friendsService: FriendsService,
    private val messagingTemplate: SimpMessagingTemplate,
    private val memberService: MemberService,
) {

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

        // WebSocket 알림 전송
        messagingTemplate.convertAndSend("/topic/friend/${request.friendId}",
            FriendNotificationDto(type = "REQUEST", senderName = memberService.getUserInfo(user).userName)
        )

        return ResponseEntity.ok(friendship)
    }

    @PostMapping("/accept")
    fun acceptFriendRequest(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: FriendRequestDto
    ): ResponseEntity<FriendShipDto> {
        println(" 사용자가 수락할때의 사용자의 userId = ${user.memberId}")
        val friendship = friendsService.acceptFriendRequest(userId = user.memberId, senderId = request.friendId)

        // WebSocket 알림 전송
        messagingTemplate.convertAndSend("/topic/friend/${request.friendId}",
            FriendNotificationDto(type = "ACCEPT", senderName = memberService.getUserInfo(user).userName)
        )

        return ResponseEntity.ok(friendship)
    }



}
