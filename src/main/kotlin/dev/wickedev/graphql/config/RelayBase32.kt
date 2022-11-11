package dev.wickedev.graphql.config

import graphql.relay.Relay.ResolvedGlobalId
import org.apache.commons.codec.binary.Base16
import org.apache.commons.codec.binary.Base32
import java.nio.charset.StandardCharsets

class RelayBase32 {
    private val base32 = Base16()

    fun toGlobalId(type: String, id: String): String {
        val bs = "$type:$id".toByteArray(StandardCharsets.UTF_8)
        return base32.encodeToString(bs)
            .lowercase()
            .replace("=+\$".toRegex(), "")
    }

    fun fromGlobalId(globalId: String?): ResolvedGlobalId {
        val split = String(base32.decode(globalId?.uppercase()), StandardCharsets.UTF_8)
            .split(":".toRegex(), limit = 2)
            .toTypedArray()
        require(split.size == 2) { String.format("expecting a valid global id, got %s", globalId) }
        return ResolvedGlobalId(split[0], split[1])
    }
}