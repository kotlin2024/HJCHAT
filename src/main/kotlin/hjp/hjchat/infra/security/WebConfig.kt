package hjp.hjchat.infra.security

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        // 모든 경로에 대해 CORS 허용
        registry.addMapping("/**")
            .allowedOriginPatterns("http://localhost:63342", "http://localhost:3000")  // 허용할 클라이언트 URL
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 허용할 HTTP 메서드들
            .allowedHeaders("*")  // 모든 헤더를 허용
            .exposedHeaders("Authorization")
            .allowCredentials(true)  // 쿠키를 허용하려면 true로 설정
    }

}
