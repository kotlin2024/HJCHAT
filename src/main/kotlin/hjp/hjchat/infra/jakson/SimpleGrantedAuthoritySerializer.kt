package hjp.hjchat.infra.jakson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.springframework.security.core.authority.SimpleGrantedAuthority

class SimpleGrantedAuthoritySerializer : JsonSerializer<SimpleGrantedAuthority>() {
    override fun serialize(value: SimpleGrantedAuthority, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.authority) // authority 문자열만 JSON으로 직렬화
    }
}
