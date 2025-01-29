package hjp.hjchat.infra.security.smtp

import java.util.Random

fun generateVerificationCode(): String {
    val random = Random()
    return String.format("%06d", random.nextInt(999999))
}