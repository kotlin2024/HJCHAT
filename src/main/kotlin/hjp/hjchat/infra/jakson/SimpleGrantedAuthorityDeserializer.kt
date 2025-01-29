package hjp.hjchat.infra.jakson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class SimpleGrantedAuthorityDeserializer : JsonDeserializer<GrantedAuthority>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): GrantedAuthority {
        return SimpleGrantedAuthority(p.valueAsString)
    }
}

