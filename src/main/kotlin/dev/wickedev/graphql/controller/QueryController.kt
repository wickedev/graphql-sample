package dev.wickedev.graphql.controller

import com.google.common.base.CaseFormat
import dev.wickedev.graphql.entity.Post
import dev.wickedev.graphql.entity.User
import dev.wickedev.graphql.getDomainType
import dev.wickedev.graphql.repository.PostRepository
import dev.wickedev.graphql.repository.UserRepository
import dev.wickedev.graphql.repository.buildPredicate
import graphql.relay.*
import graphql.schema.DataFetchingEnvironment
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.RestController


val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
val snakeRegex = "_[a-zA-Z]".toRegex()

// String extensions
fun String.camelToSnakeCase(): String {
    return camelRegex.replace(this) {
        "_${it.value}"
    }.lowercase()
}

fun String.snakeToLowerCamelCase(): String {
    return snakeRegex.replace(this) {
        it.value.replace("_", "")
            .uppercase()
    }
}

fun String.snakeToUpperCamelCase(): String {
    return this.snakeToLowerCamelCase().uppercase()
}


@RestController
class QueryController(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) {
    @QueryMapping
    fun health(): Boolean = true

    @QueryMapping
    fun userPages(env: DataFetchingEnvironment): Connection<User> {

        val domainType = getDomainType(userRepository)
        return userRepository.findBy<User, Connection<User>>(buildPredicate(domainType, env)) {
            val parameters: MultiValueMap<String, Any> = LinkedMultiValueMap()


            val first: Int = env.arguments["first"] as Int? ?: 10

            @Suppress("UNCHECKED_CAST")
            val orderBy = (env.arguments["orderBy"] as List<Map<String, String>>?)
                ?.map { order -> order["property"]!! to order["direction"]!! }
                ?: listOf("ID" to "DESC")

            var pageable = PageRequest.ofSize(first)

            for (order in orderBy) {
                val property = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, order.first)
                val sortOrder = if (order.second == "ASC")
                    Sort.Order.asc(property)
                else
                    Sort.Order.desc(property)

                pageable = pageable.withSort(Sort.by(sortOrder))
            }

            // filter 조건은 가장 우선 순위로 할것
            // orderBy는 들어온 순서대로 WHERE (0) < cursor AND WHERE (1) < cursor ...
            // ORDER BY (0) DESC, (1) DESC, (2) DESC ...
            return@findBy SimpleListConnection(it.page(pageable).toList(), "user:").get(env)
        }
    }

    @QueryMapping
    fun postPages(env: DataFetchingEnvironment): Connection<Post> {
        return SimpleListConnection(postRepository.findAll().toList(), "post:").get(env)
    }
}