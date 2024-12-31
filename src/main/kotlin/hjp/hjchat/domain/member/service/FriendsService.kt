package hjp.hjchat.domain.member.service

import com.fasterxml.jackson.databind.ObjectMapper
import hjp.hjchat.domain.member.dto.FriendNotificationDto
import hjp.hjchat.domain.member.dto.FriendShipDto
import hjp.hjchat.domain.member.dto.FriendshipStatus
import hjp.hjchat.domain.member.entity.FriendRequest
import hjp.hjchat.domain.member.entity.Friendship
import hjp.hjchat.domain.member.entity.RequestStatus
import hjp.hjchat.domain.member.entity.toResponse
import hjp.hjchat.domain.member.model.FriendRequestRepository
import hjp.hjchat.domain.member.model.FriendshipRepository
import hjp.hjchat.infra.security.jwt.UserPrincipal
import hjp.hjchat.infra.security.ouath.model.OAuthRepository
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class FriendsService(
    private val oAuthRepository: OAuthRepository,
    private val friendshipRepository: FriendshipRepository,
    private val friendRequestRepository: FriendRequestRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val messagingTemplate: SimpMessagingTemplate,
    private val memberService: MemberService,
) {


    fun getMyFriendsList(user: UserPrincipal): List<FriendShipDto> {

        val friendList = friendshipRepository.findAllByUserId(user.memberId)
            ?: throw IllegalArgumentException("해당 ${user.memberId} ID의 데이터가 존재하지 않음")
        return friendList.map { it.toResponse() }
    }

    @Transactional
    fun sendFriendRequest(userId: Long, friendId: Long): FriendShipDto {
        // 친구와 사용자 존재 확인
        val user = oAuthRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }
        val friend = oAuthRepository.findById(friendId)
            .orElseThrow { IllegalArgumentException("친구를 찾을 수 없습니다.") }

        // 이미 친구 요청이 존재하는지 확인
        if (friendshipRepository.existsByUserAndFriend(user, friend)) {
            throw IllegalArgumentException("이미 친구 요청이 존재하거나 친구 관계입니다.")
        }
        if (friendshipRepository.existsByUserAndFriend(friend, user)) {
            throw IllegalArgumentException("이미 친구 요청이 존재하거나 친구 관계입니다.")
        }

        friendRequestRepository.save(
            FriendRequest(
                sender = user,
                receiver = friend,
                status = RequestStatus.PENDING
            )
        )

        // 친구 요청 생성
        val friendship = friendshipRepository.save(
            Friendship(
                user = user,
                friend = friend,
                status = FriendshipStatus.PENDING
            )
        )

        val event = mapOf(
            "type" to "REQUEST",
            "senderId" to userId,
            "receiverId" to friendId,
            "senderName" to user.userCode,
        )
        kafkaTemplate.send("friend-events", ObjectMapper().writeValueAsString(event))

        return FriendShipDto(
            userId = friendship.user.id,
            friendId = friendship.friend.id,
            status = friendship.status.toString(),
            senderName = friendship.friend.userName,
            friendCode = friendship.friend.userCode!!,
        )
    }

    @Transactional
    fun acceptFriendRequest(userId: Long, senderId: Long): FriendShipDto {
        // 친구 요청 찾기
        val friendRequest =
            friendRequestRepository.findBySenderIdAndReceiverId(senderId = senderId, receiverId = userId)
                ?: throw IllegalArgumentException("친구 요청을 찾을 수 없습니다.")

        val user = oAuthRepository.findById(userId).getOrNull()

        val sender = oAuthRepository.findById(senderId).getOrNull()

        // 친구 관계 생성 또는 업데이트
        val friendship = friendshipRepository.findByUserIdAndFriendId(userId = senderId, friendId = userId)
            ?: throw IllegalArgumentException("친구 요청을 찾을 수 없습니다.")

        friendship.status = FriendshipStatus.ACCEPTED
        friendshipRepository.save(friendship)
        friendshipRepository.save(
            Friendship(
                user = user!!,
                friend = sender!!,
                status = FriendshipStatus.ACCEPTED
            )
        )

        // 친구 요청 삭제
        friendRequestRepository.delete(friendRequest)

        // WebSocket 알림 전송
        messagingTemplate.convertAndSend(
            "/topic/friend/${senderId}",
            FriendNotificationDto(type = "ACCEPT", senderName = user.userName)
        )

        return FriendShipDto(
            userId = friendship.user.id,
            friendId = friendship.friend.id,
            friendCode = friendship.friend.userCode!!,
            status = friendship.status.toString(),
            senderName = friendship.friend.userName
        )
    }

    @Transactional
    fun rejectFriendRequest(user: UserPrincipal, senderId: Long) {

        val userInfo = memberService.getUserInfo(user)

        val friendRequest =
            friendRequestRepository.findBySenderIdAndReceiverId(senderId = senderId, receiverId = userInfo.userId)
                ?: throw IllegalArgumentException("친구 요청을 찾을 수 없습니다.")

        val friendship = friendshipRepository.findByUserIdAndFriendId(userId = senderId, friendId = userInfo.userId)
            ?: Friendship(
                user = friendRequest.receiver,
                friend = friendRequest.sender,
                status = FriendshipStatus.REJECTED
            )

        friendRequestRepository.delete(friendRequest)
        friendshipRepository.delete(friendship)


        try {
            messagingTemplate.convertAndSend(
                "/topic/friend/$senderId", // 요청 보낸 사용자에게 알림 전송
                FriendNotificationDto(type = "REJECT", senderName = userInfo.userName)
            )
            println("DEBUG: WebSocket 알림 전송 성공: /topic/friend/$senderId")
        } catch (e: Exception) {
            println("ERROR: WebSocket 알림 전송 실패: ${e.message}")
        }

//        messagingTemplate.convertAndSend("/topic/friend/${senderId}",
//            FriendNotificationDto(type = "REJECT", senderName = userInfo.userName)
//        )
    }

    fun getSentFriendRequests(userId: Long): List<FriendShipDto> {
        val requests = friendRequestRepository.findBySenderId(userId)
            ?: throw IllegalArgumentException("해당 유저를 찾을수 없음 ${userId}.")

        return requests.map {
            FriendShipDto(
                userId = userId,
                friendId = it.receiver.id,
                friendCode = it.receiver.userCode!!,
                status = it.status.toString(),
                senderName = it.receiver.userName
            )
        }
    }

    fun getReceivedFriendRequests(userId: Long): List<FriendShipDto> {
        val requests = friendRequestRepository.findByReceiverId(userId)
            ?: throw IllegalArgumentException("해당 유저를 찾을수 없음 ${userId}.")


        return requests.map {
            FriendShipDto(
                friendId = it.sender.id,
                friendCode = it.sender.userCode!!,
                userId = userId,
                status = it.status.toString(),
                senderName = it.sender.userName
            )
        }
    }

}
