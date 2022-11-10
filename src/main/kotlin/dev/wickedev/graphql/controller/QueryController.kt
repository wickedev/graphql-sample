package dev.wickedev.graphql.controller

import com.querydsl.core.types.Predicate
import dev.wickedev.graphql.entity.Post
import dev.wickedev.graphql.entity.User
import dev.wickedev.graphql.repository.PostRepository
import dev.wickedev.graphql.repository.UserRepository
import graphql.relay.*
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.web.bind.annotation.RestController

fun buildPredicate(env: DataFetchingEnvironment): Predicate = TODO()

@RestController
class QueryController(
    private val userRepository: UserRepository, private val postRepository: PostRepository
) {

    @QueryMapping
    fun health(): Boolean {
        return true
    }

    @QueryMapping
    fun userPages(env: DataFetchingEnvironment): Connection<User> {
        return userRepository.findBy<User, Connection<User>>(buildPredicate(env)) {
            return@findBy DefaultConnection(
                emptyList(), DefaultPageInfo(
                    DefaultConnectionCursor(""), DefaultConnectionCursor(""), false, false
                )
            )
        }
    }

    @QueryMapping
    fun postPages(env: DataFetchingEnvironment): Connection<Post> {
        return SimpleListConnection(postRepository.findAll().toList(), "post:").get(env)
    }
}