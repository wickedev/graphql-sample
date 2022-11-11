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
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Configuration
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.graphql.data.query.QuerydslDataFetcher
import org.springframework.graphql.execution.RuntimeWiringConfigurer


@Configuration
class GraphQLConfig(
    private val repositories: ObjectProvider<QuerydslPredicateExecutor<*>>
) : RuntimeWiringConfigurer {

    private val relay = Relay()

    private fun DataFetchingEnvironment.toGlobalId(): String {
        val id = getSource<Node>().id.toString()
        val parentType = this.parentType
        if (parentType is GraphQLNamedSchemaElement) {
            val type = parentType.name.lowercase()
            return relay.toGlobalId(type, id)
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
                    .coercing(GraphqlRelayIDCoercing(relay, GraphqlIDCoercing())).build()
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