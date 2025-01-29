package hjp.hjchat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

@EntityScan(basePackages = ["hjp.hjchat.domain"])
@SpringBootApplication
class HjchatApplication

fun main(args: Array<String>) {
    runApplication<HjchatApplication>(*args)
}
