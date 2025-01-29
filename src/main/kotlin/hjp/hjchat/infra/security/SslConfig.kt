package hjp.hjchat.infra.security

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SslConfig {

    @Bean
    fun containerCustomizer(): WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
        return WebServerFactoryCustomizer { factory ->
            factory.addAdditionalTomcatConnectors(createHttpConnector())
        }
    }

    private fun createHttpConnector() = org.apache.catalina.connector.Connector("org.apache.coyote.http11.Http11NioProtocol").apply {
        scheme = "http"
        port = 8080           // HTTP 포트
        secure = false
        redirectPort = 443  // HTTPS 포트로 리디렉션
    }
}
