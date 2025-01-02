package hjp.hjchat.infra.s3

import hjp.hjchat.infra.security.jwt.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths

@RestController
@RequestMapping("/api/s3")
class S3Controller(
    private val s3Service: S3Service
) {

    @PostMapping("/upload")
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @AuthenticationPrincipal user: UserPrincipal
    ): ResponseEntity<String> {
        val userId= user.memberId
        val bucketName = "hjchat-s3-bucket1"
        val key = "uploads/profile/$userId/profile"

        val tempFile = Files.createTempFile(null, file.originalFilename)
        file.transferTo(tempFile.toFile())

        val fileUrl = s3Service.uploadFile(bucketName, key, tempFile)
        return ResponseEntity.ok(fileUrl)
    }



    @GetMapping("/photo")
    fun getProfilePhoto(@AuthenticationPrincipal user: UserPrincipal): ResponseEntity<String> {
        val userId = user.memberId
        val bucketName = "hjchat-s3-bucket1"
        val key = "uploads/$userId/profile"

        val presignedUrl = s3Service.generatePresignedUrl(bucketName, key)
        return ResponseEntity.ok(presignedUrl)
    }

    @GetMapping("/download")
    fun downloadFile(
        @RequestParam("key") key: String
    ): ResponseEntity<String> {
        val bucketName = "hjchat-s3-bucket1"
        val destination = Paths.get("downloads/${key.split("/").last()}")
        s3Service.downloadFile(bucketName, key, destination)
        return ResponseEntity.ok("File downloaded to: $destination")
    }
}
