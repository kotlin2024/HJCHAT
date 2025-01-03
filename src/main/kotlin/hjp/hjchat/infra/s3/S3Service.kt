package hjp.hjchat.infra.s3

import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration

@Service
class S3Service(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner
) {

    fun generateUploadPresignedUrl(bucketName: String, key: String): String {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType("image/*")
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .putObjectRequest(putObjectRequest)
            .signatureDuration(Duration.ofMinutes(15))
            .build()

        val presignedUrl = s3Presigner.presignPutObject(presignRequest)
        return presignedUrl.url().toString()
    }

    // 파일 다운로드용 Presigned URL 생성
    fun generateDownloadPresignedUrl(bucketName: String, key: String): String {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .getObjectRequest(getObjectRequest)
            .signatureDuration(Duration.ofMinutes(15))
            .build()

        val presignedUrl = s3Presigner.presignGetObject(presignRequest)
        return presignedUrl.url().toString()
    }
}