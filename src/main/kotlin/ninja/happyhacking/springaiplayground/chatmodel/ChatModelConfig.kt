package ninja.happyhacking.springaiplayground.chatmodel

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient


@Configuration
class ChatModelConfig {
    @Bean
    fun restClientBuilder(): RestClient.Builder {
        return RestClient.builder()
    }
}
