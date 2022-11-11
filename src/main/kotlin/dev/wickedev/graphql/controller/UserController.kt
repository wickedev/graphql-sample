package dev.wickedev.graphql.controller

import dev.wickedev.graphql.entity.Post
import dev.wickedev.graphql.entity.User
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

    @SchemaMapping
    fun posts(self: User): List<Post> {
        return emptyList()
    }
}