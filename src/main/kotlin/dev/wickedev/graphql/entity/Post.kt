package dev.wickedev.graphql.entity

import jakarta.persistence.*
import org.hibernate.Hibernate

@Entity
@Table(name = "posts")
data class Post(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
    val title: String,
    val content: String,
) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    lateinit var author: User
        private set

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + author.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as Post

        if (id != other.id) return false
        if (title != other.title) return false
        if (content != other.content) return false
        if (author != other.author) return false

        return true
    }

    override fun toString(): String {
        return this::class.simpleName + "(id = $id , title = $title , content = $content , authorId = ${author.id})"
    }
}