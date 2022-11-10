package dev.wickedev.graphql.controller

import dev.wickedev.graphql.entity.Post
import dev.wickedev.graphql.entity.User
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PostController {

    @SchemaMapping
    fun author(self: Post): User {
        return self.author
    }
}