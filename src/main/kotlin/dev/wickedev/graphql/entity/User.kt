package dev.wickedev.graphql.entity

import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.data.jpa.repository.support.Querydsl

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    @Column(name = "phone_number")
    val phoneNumber: String,
) {
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    lateinit var posts: List<Post>
        private set

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as User

        if (id != other.id) return false
        if (name != other.name) return false
        if (phoneNumber != other.phoneNumber) return false

        return true
    }

    override fun toString(): String {
        return "User(id=$id, name='$name', phoneNumber='$phoneNumber')"
    }
}