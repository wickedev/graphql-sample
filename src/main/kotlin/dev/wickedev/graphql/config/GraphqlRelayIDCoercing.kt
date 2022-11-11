package dev.wickedev.graphql.config

import graphql.relay.Relay
import graphql.scalar.GraphqlIDCoercing
import graphql.schema.Coercing

class GraphqlRelayIDCoercing(
    private val relay: RelayBase32,
    private val idCoercing: GraphqlIDCoercing
) : Coercing<Any, Any> by idCoercing {
    override fun serialize(input: Any): Any {
        return idCoercing.serialize(input)
    }

    override fun parseValue(input: Any): Relay.ResolvedGlobalId {
        return relay.fromGlobalId(idCoercing.parseValue(input))
    }

    override fun parseLiteral(input: Any): Relay.ResolvedGlobalId {
        return relay.fromGlobalId(idCoercing.parseLiteral(input))
    }
}