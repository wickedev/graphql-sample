package dev.wickedev.graphql.controller

import dev.wickedev.graphql.entity.Post
import dev.wickedev.graphql.entity.User
import dev.wickedev.graphql.repository.PostRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(val postRepository: PostRepository) {

    @SchemaMapping
    fun posts(self: User): Iterable<Post> {
        return postRepository.findAllByAuthorId(self.id)
    }
}