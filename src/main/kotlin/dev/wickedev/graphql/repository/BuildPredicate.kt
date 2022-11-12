package dev.wickedev.graphql.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Predicate
import dev.wickedev.graphql.entity.QUser
import graphql.schema.DataFetchingEnvironment
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.data.querydsl.SimpleEntityPathResolver
import org.springframework.data.querydsl.binding.QuerydslBindings
import org.springframework.data.querydsl.binding.QuerydslPredicateBuilder
import org.springframework.data.util.TypeInformation
import org.springframework.util.Assert
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.util.ObjectUtils


private val BUILDER = QuerydslPredicateBuilder(
    DefaultConversionService.getSharedInstance(), SimpleEntityPathResolver.INSTANCE
)

private fun getPredicate(builder: BooleanBuilder): Predicate {
    val predicate = builder.value
    return predicate ?: BooleanBuilder()
}

private fun isSingleElementCollectionWithEmptyItem(source: List<*>): Boolean {
    return source.size == 1 && ObjectUtils.isEmpty(source[0])
}

fun getPredicate(type: TypeInformation<*>, values: MultiValueMap<String, *>, bindings: QuerydslBindings): Predicate {
    Assert.notNull(bindings, "Context must not be null")
    val builder = BooleanBuilder()


    if (values.isEmpty()) {
        return getPredicate(builder)
    }

    for ((path, value) in values) {
        println(path)
        println(value)
        println(bindings)
    }

    return getPredicate(builder)
}

fun <T> buildPredicate(domainType: Class<T>, env: DataFetchingEnvironment): Predicate {
    val parameters: MultiValueMap<String, Any> = LinkedMultiValueMap()
    val bindings = QuerydslBindings()
    val path: EntityPath<*> = SimpleEntityPathResolver.INSTANCE.createPath(domainType)

    for ((key, value) in env.arguments.entries) {
        val values = if (value is List<*>) value else listOf(value)
        parameters.put(key, values)
    }

    return getPredicate(TypeInformation.of(domainType), parameters, bindings)
}
