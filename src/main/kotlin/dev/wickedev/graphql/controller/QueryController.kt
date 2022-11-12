package dev.wickedev.graphql.controller

import com.querydsl.core.QueryModifiers
import dev.wickedev.graphql.entity.Post
import dev.wickedev.graphql.entity.User
import dev.wickedev.graphql.getDomainType
import dev.wickedev.graphql.repository.PostRepository
import dev.wickedev.graphql.repository.UserRepository
import dev.wickedev.graphql.repository.buildPredicate
import graphql.relay.*
import graphql.schema.DataFetchingEnvironment
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.RestController

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
            for ((key, value) in env.arguments.entries) {
                val values = if (value is List<*>) value else listOf(value)
                parameters.put(key, values)
            }

            val first: Int = parameters["first"]?.first() as Int? ?: 10
            val pageable = PageRequest.ofSize(first)
            // - WHERE id < cursor 혹은 WHERE id > cursor
            // - 그외 less(than equal), greater(than equal) 조건은 페이징에서 AND 조건으로 사용하지 말것 !!
            //     - 모든 less, greater 조건을 AND 만족시키려면 있어야 할 다음 페이지가 없어짐
            //     - 커서 기반 페이징에서는 커스텀 lt, lte, gt, gte 금지
            // - ORDER BY id DESC 혹은 ORDER BY id ASC 무조건 들어가야함 !!

            return@findBy SimpleListConnection(it.page(pageable).toList(), "user:").get(env)
        }
    }

    @QueryMapping
    fun postPages(env: DataFetchingEnvironment): Connection<Post> {
        return SimpleListConnection(postRepository.findAll().toList(), "post:").get(env)
    }
}