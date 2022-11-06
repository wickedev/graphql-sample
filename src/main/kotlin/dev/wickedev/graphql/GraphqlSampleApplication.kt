package dev.wickedev.graphql

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GraphqlSampleApplication

fun main(args: Array<String>) {
	runApplication<GraphqlSampleApplication>(*args)
}
