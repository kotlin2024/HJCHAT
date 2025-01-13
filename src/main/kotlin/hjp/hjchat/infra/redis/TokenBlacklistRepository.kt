package hjp.hjchat.infra.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class TokenBlacklistRepository(
    private val redisTemplate: RedisTemplate<String, String>
) {
    fun addToBlacklist(token: String, expirationTime: Long) {
        redisTemplate.opsForValue().set(token, "blacklisted", expirationTime, TimeUnit.MILLISECONDS)
    }

    fun isBlacklisted(token: String): Boolean {
        return redisTemplate.hasKey(token)
    }
}
