package dev.wickedev.graphql.repository

import dev.wickedev.graphql.entity.Post
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.graphql.data.GraphQlRepository

@GraphQlRepository
interface PostRepository : CrudRepository<Post, Long>, QuerydslPredicateExecutor<Post>