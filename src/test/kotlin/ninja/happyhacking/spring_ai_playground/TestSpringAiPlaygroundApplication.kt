package ninja.happyhacking.spring_ai_playground

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<SpringAiPlaygroundApplication>().with(TestcontainersConfiguration::class).run(*args)
}
