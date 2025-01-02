package hjp.hjchat.infra.s3

import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.nio.file.Path
import java.time.Duration

@Service
class S3Service(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner
) {

    fun generatePresignedUrl(bucketName: String, key: String): String {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .getObjectRequest(getObjectRequest)
            .signatureDuration(Duration.ofMinutes(15)) // 15분 동안 유효
            .build()

        val presignedUrl = s3Presigner.presignGetObject(presignRequest)
        return presignedUrl.url().toString()
    }

    fun uploadFile(bucketName: String, key: String, filePath: Path): String {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build()

        val response: PutObjectResponse = s3Client.putObject(putObjectRequest, filePath)
        return "https://${bucketName}.s3.amazonaws.com/${key}"
    }

    fun downloadFile(bucketName: String, key: String, destination: Path) {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build()

        s3Client.getObject(getObjectRequest, destination)
    }
}