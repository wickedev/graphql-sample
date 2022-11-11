package dev.wickedev.graphql.controller

import dev.wickedev.graphql.entity.Post
import dev.wickedev.graphql.entity.User
import dev.wickedev.graphql.getDomainType
import dev.wickedev.graphql.repository.PostRepository
import dev.wickedev.graphql.repository.UserRepository
import dev.wickedev.graphql.repository.buildPredicate
import graphql.relay.*
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.QueryMapping
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
            return@findBy DefaultConnection(
                emptyList(), DefaultPageInfo(
                    DefaultConnectionCursor("1"), DefaultConnectionCursor("10"), false, false
                )
            )
        }
    }

    @QueryMapping
    fun postPages(env: DataFetchingEnvironment): Connection<Post> {
        return SimpleListConnection(postRepository.findAll().toList(), "post:").get(env)
    }
}