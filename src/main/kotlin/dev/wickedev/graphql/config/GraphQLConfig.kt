package dev.wickedev.graphql.config

import dev.wickedev.graphql.entity.Node
import dev.wickedev.graphql.getDomainType
import graphql.relay.Relay
import graphql.scalar.GraphqlIDCoercing
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.DataFetchingEnvironmentImpl.newDataFetchingEnvironment
import graphql.schema.GraphQLNamedSchemaElement
import graphql.schema.GraphQLScalarType
import graphql.schema.idl.RuntimeWiring
import org.apache.commons.codec.binary.Base32
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Configuration
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.graphql.data.query.QuerydslDataFetcher
import org.springframework.graphql.execution.RuntimeWiringConfigurer


@Configuration
class GraphQLConfig<T : Node>(
    private val repositories: ObjectProvider<QuerydslPredicateExecutor<T>>
) : RuntimeWiringConfigurer {

    private val relayBase32 = RelayBase32()
    val base32 = Base32()

    private fun DataFetchingEnvironment.toGlobalId(): Any? {
        val source = getSource<Any>()
        if (source !is Node) {
            return source
        }

        val id = source.id.toString()

        val parentType = this.parentType
        if (parentType is GraphQLNamedSchemaElement) {
            val type = parentType.name.lowercase()
            return relayBase32.toGlobalId(type, id)
        }
        return id
    }

    private fun nodeDataFetcher(): DataFetcher<*> {
        val dataFetchers = repositories.associate {
            val type = getDomainType(it).simpleName.lowercase()
            val df = QuerydslDataFetcher.builder(it).single()
            type to df
        }

        return DataFetcher { env ->
            val id = env.getArgument("id") as Relay.ResolvedGlobalId
            val parsedEnv = newDataFetchingEnvironment(env)
                .arguments(mapOf("id" to id.id))
                .build()
            dataFetchers[id.type]?.get(parsedEnv)
        }
    }

    override fun configure(builder: RuntimeWiring.Builder) {
        val types = repositories.map { getDomainType(it).simpleName }.toList()
        builder
            .scalar(
                GraphQLScalarType.newScalar().name("ID").description("Built-in ID")
                    .coercing(GraphqlRelayIDCoercing(relayBase32, GraphqlIDCoercing())).build()
            )
            .type("Query") { t ->
                t.dataFetcher("node", nodeDataFetcher())
            }

        for (type in types) {
            builder.type(type) { t ->
                t.dataFetcher("id") { env ->
                    env.toGlobalId()
                }
            }
        }
    }
}