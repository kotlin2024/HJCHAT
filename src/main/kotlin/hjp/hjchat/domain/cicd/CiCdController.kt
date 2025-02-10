package hjp.hjchat.domain.cicd

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/ci_cd")
class CiCdController(
    private val ciCdService: CiCdService,
) {

    @GetMapping("/get/test/")
    fun ciCdForTest(): ResponseEntity<CiCdDto> {
        return ResponseEntity.ok(ciCdService.ciCdForTest())
    }
}