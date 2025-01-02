package hjp.hjchat.infra.s3

import hjp.hjchat.infra.security.jwt.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/s3")
class S3Controller(
    private val s3Service: S3Service
) {

    @PostMapping("/upload/url")
    fun getUploadUrl(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestParam("fileName") fileName: String
    ): ResponseEntity<String> {
        val userId = user.memberId
        val bucketName = "hjchat-s3-bucket1"
        val key = "uploads/profile/$userId/profile"

        val presignedUrl = s3Service.generateUploadPresignedUrl(bucketName, key)
        return ResponseEntity.ok(presignedUrl)
    }

    @GetMapping("/photo")
    fun getProfilePhoto(@AuthenticationPrincipal user: UserPrincipal): ResponseEntity<String> {
        val userId = user.memberId
        val bucketName = "hjchat-s3-bucket1"
        val key = "uploads/profile/$userId/profile"

        // Presigned URL 생성
        val presignedUrl = s3Service.generateDownloadPresignedUrl(bucketName, key)
        return ResponseEntity.ok(presignedUrl)
    }
}