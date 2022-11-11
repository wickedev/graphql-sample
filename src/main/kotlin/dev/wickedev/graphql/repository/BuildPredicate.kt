package dev.wickedev.graphql.repository

import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Predicate
import graphql.schema.DataFetchingEnvironment
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.data.querydsl.SimpleEntityPathResolver
import org.springframework.data.querydsl.binding.QuerydslBindings
import org.springframework.data.querydsl.binding.QuerydslPredicateBuilder
import org.springframework.data.util.TypeInformation
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap


private val BUILDER = QuerydslPredicateBuilder(
    DefaultConversionService.getSharedInstance(), SimpleEntityPathResolver.INSTANCE
)

fun <T> buildPredicate(domainType: Class<T>, env: DataFetchingEnvironment): Predicate {
    val parameters: MultiValueMap<String, Any> = LinkedMultiValueMap()
    val bindings = QuerydslBindings()
    val path: EntityPath<*> = SimpleEntityPathResolver.INSTANCE.createPath(domainType)

    for ((key, value) in env.arguments.entries) {
        val values = if (value is List<*>) value else listOf(value)
        parameters.put(key, values)
    }

    return BUILDER.getPredicate(TypeInformation.of(domainType), parameters, bindings)
}
