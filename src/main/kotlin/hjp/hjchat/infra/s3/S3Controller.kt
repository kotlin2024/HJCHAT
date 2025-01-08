package hjp.hjchat.infra.s3

import hjp.hjchat.infra.security.jwt.UserPrincipal
import hjp.hjchat.infra.security.ouath.model.OAuthRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/s3")
class S3Controller(
    private val s3Service: S3Service,
    private val oAuthRepository: OAuthRepository,
) {

    @PostMapping("/upload/url")
    fun getUploadUrl(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestParam("fileName") fileName: String
    ): ResponseEntity<String> {
        val userId = user.memberId
        val bucketName = "hjchat-s3-bucket1"
        val key = "uploads/profile/$userId/profile"

        val member = oAuthRepository.findById(user.memberId).getOrNull()
        member!!.profileImageUrl = s3Service.getProfileImageUrl(user.memberId)
        oAuthRepository.save(member)
        val presignedUrl = s3Service.generateUploadPresignedUrl(bucketName, key)
        return ResponseEntity.ok(presignedUrl)
    }

    @GetMapping("/photo")
    fun getProfilePhoto(@AuthenticationPrincipal user: UserPrincipal): ResponseEntity<String> {

        val member = oAuthRepository.findById(user.memberId).getOrNull()
        return ResponseEntity.ok(member!!.profileImageUrl)
    }
}