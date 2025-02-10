package hjp.hjchat.domain.cicd

import org.springframework.stereotype.Service

@Service
class CiCdService {
    fun ciCdForTest(): CiCdDto {
        return CiCdDto(
            description = " 2025-02-10 7시 16분에 변경된 파일!",
        )
    }
}