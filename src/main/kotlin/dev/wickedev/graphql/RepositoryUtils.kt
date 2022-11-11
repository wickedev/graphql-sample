package dev.wickedev.graphql

import org.springframework.core.ResolvableType
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.annotation.MergedAnnotations
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata
import org.springframework.graphql.data.GraphQlRepository
import org.springframework.lang.Nullable
import org.springframework.util.Assert
import org.springframework.util.StringUtils


fun <T> getDomainType(executor: QuerydslPredicateExecutor<T>): Class<T> {
    return getRepositoryMetadata(executor).domainType as Class<T>
}

fun <T> getRepositoryMetadata(executor: QuerydslPredicateExecutor<T>): RepositoryMetadata {
    Assert.isInstanceOf(Repository::class.java, executor)
    val genericInterfaces = executor.javaClass.genericInterfaces
    for (genericInterface in genericInterfaces) {
        val rawClass = ResolvableType.forType(genericInterface).rawClass
        if (rawClass == null || MergedAnnotations.from(rawClass).isPresent(
                NoRepositoryBean::class.java
            )
        ) {
            continue
        }
        if (Repository::class.java.isAssignableFrom(rawClass)) {
            return DefaultRepositoryMetadata(rawClass)
        }
    }
    throw IllegalArgumentException(String.format("Cannot resolve repository interface from %s", executor))
}

@Nullable
fun <T> getGraphQlTypeName(repository: QuerydslPredicateExecutor<T>): String? {
    val annotation = AnnotatedElementUtils.findMergedAnnotation(
        repository.javaClass,
        GraphQlRepository::class.java
    ) ?: return null
    return if (StringUtils.hasText(annotation.typeName)) annotation.typeName else getDomainType(repository).simpleName
}
