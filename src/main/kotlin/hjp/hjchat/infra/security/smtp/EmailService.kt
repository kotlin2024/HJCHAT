package hjp.hjchat.infra.security.smtp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(
    @Autowired private val mailSender: JavaMailSender
) {
    fun sendVerificationEmail(email: String, token: String) {
        val verificationUrl = "http://localhost:8080/api/oauth/verify-email?token=$token" // 실제 서비스 도메인으로 변경
        val subject = "회원가입 이메일 인증"
        val message = """
        안녕하세요!
        아래 링크를 클릭하여 이메일 인증을 완료해주세요:
        $verificationUrl
        
        링크는 24시간 동안 유효합니다.
    """.trimIndent()

        // 이메일 발송 로직
        mailSender.send { mimeMessage ->
            val helper = MimeMessageHelper(mimeMessage, "UTF-8")
            helper.setTo(email)
            helper.setSubject(subject)
            helper.setText(message, false) // `true`로 변경 시 HTML로 작성 가능
        }
    }
}