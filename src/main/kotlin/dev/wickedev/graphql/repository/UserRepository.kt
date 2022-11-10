package dev.wickedev.graphql.repository

import dev.wickedev.graphql.entity.Post
import dev.wickedev.graphql.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.graphql.data.GraphQlRepository

@GraphQlRepository
interface UserRepository : CrudRepository<User, Long>, JpaRepository<User, Long>, QuerydslPredicateExecutor<User>
