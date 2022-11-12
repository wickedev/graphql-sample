package dev.wickedev.graphql.repository

import dev.wickedev.graphql.entity.Post
import dev.wickedev.graphql.entity.QUser
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.graphql.data.GraphQlRepository

@GraphQlRepository
interface PostRepository : CrudRepository<Post, Long>, QuerydslPredicateExecutor<Post> {
    fun findAllByAuthorId(id: Long): Iterable<Post>
}

@NoRepositoryBean
abstract class PostRepositoryImpl : PostRepository, QuerydslRepositorySupport(Post::class.java) {
    override fun findAllByAuthorId(id: Long): Iterable<Post> {

        from(QUser.user).limit(1).fetch()

        return emptyList()
    }
}