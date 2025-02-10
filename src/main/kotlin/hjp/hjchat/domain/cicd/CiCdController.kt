package hjp.hjchat.domain.cicd

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class CiCdController(
    private val ciCdService: CiCdService,
) {

    @GetMapping("/ci_cd/")
    fun cicdForTest(): String{
        return ciCdService.cicdForTest()
    }
}