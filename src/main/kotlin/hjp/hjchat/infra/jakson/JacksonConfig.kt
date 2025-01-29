package hjp.hjchat.infra.jakson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.core.GrantedAuthority


@Configuration
class JacksonConfig(builder: Jackson2ObjectMapperBuilder) {

    init {
        val module = SimpleModule()
        module.addDeserializer(GrantedAuthority::class.java, SimpleGrantedAuthorityDeserializer())

        val objectMapper = builder.createXmlMapper(false).build<ObjectMapper>()
        objectMapper.registerModule(module)
    }
}

