package hjp.hjchat.domain.member.service

import hjp.hjchat.domain.member.dto.FriendShipDto
import hjp.hjchat.domain.member.dto.FriendshipStatus
import hjp.hjchat.domain.member.entity.FriendRequest
import hjp.hjchat.domain.member.entity.Friendship
import hjp.hjchat.domain.member.entity.RequestStatus
import hjp.hjchat.domain.member.model.FriendRequestRepository
import hjp.hjchat.domain.member.model.FriendshipRepository
import hjp.hjchat.infra.security.ouath.model.OAuthRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class FriendsService(
    private val oAuthRepository: OAuthRepository,
    private val friendshipRepository: FriendshipRepository,
    private val friendRequestRepository: FriendRequestRepository,
) {

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

        return FriendShipDto(
            userId = friendship.user.id,
            friendId = friendship.friend.id,
            status = friendship.status.toString(),
            senderName = friendship.friend.userName
        )
    }

    @Transactional
    fun acceptFriendRequest(userId: Long, senderId: Long): FriendShipDto {
        // 친구 요청 찾기
        val friendRequest = friendRequestRepository.findBySenderIdAndReceiverId(senderId = senderId, receiverId = userId)
            ?: throw IllegalArgumentException("친구 요청을 찾을 수 없습니다.")


        // 요청 상태 업데이트
        friendRequest.status = RequestStatus.ACCEPTED
        friendRequestRepository.save(friendRequest)

        // 친구 관계 생성 또는 업데이트
        val friendship = friendshipRepository.findByUserIdAndFriendId(userId, senderId)
            ?: Friendship(user = friendRequest.receiver, friend = friendRequest.sender, status = FriendshipStatus.ACCEPTED)

        friendship.status = FriendshipStatus.ACCEPTED
        friendshipRepository.save(friendship)

        // 친구 요청 삭제
        friendRequestRepository.delete(friendRequest)

        return FriendShipDto(
            userId = friendship.user.id,
            friendId = friendship.friend.id,
            status = friendship.status.toString(),
            senderName = friendship.friend.userName
        )
    }


    fun getSentFriendRequests(userId: Long): List<FriendShipDto> {
        val requests = friendRequestRepository.findBySenderId(userId)
            ?: throw IllegalArgumentException("해당 유저를 찾을수 없음 ${userId}.")

        return requests.map {
            FriendShipDto(
                userId = userId,
                friendId = it.receiver.id,
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
                friendId =it.sender.id,
                userId = userId,
                status = it.status.toString(),
                senderName = it.sender.userName
            )
        }
    }
}
