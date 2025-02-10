package hjp.hjchat.domain.cicd

import org.springframework.stereotype.Service

@Service
class CiCdService {
    fun ciCdForTest(): String {
        return "CI-CD가 제대로 적용되었습니다!! 2025-02-10 6: 47"
    }
}