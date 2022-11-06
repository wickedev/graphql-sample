package dev.wickedev.graphql.controller

import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class QueryController {

    @QueryMapping
    fun health(): Boolean {
        return true
    }
}