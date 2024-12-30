package com.yetanotherdevblog.config

import com.mongodb.MongoClientSettings
import com.mongodb.ServerAddress
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

/**
 * Enables fine settings of [com.mongodb.MongoClientSettings] from Spring Boot.
 *
 * See in [common application properties, section data](https://docs.spring.io/spring-boot/appendix/application-properties/index.html#appendix.application-properties.data)
 * for common `spring.data.mongodb` properties.
 */
@Configuration
class MongoClientConfig {

    @Bean
    fun mongoClient(): MongoClient {
        val settings = MongoClientSettings.builder()
            .applyToClusterSettings { builder ->
                builder.hosts(listOf(ServerAddress("localhost", 27017)))
            }
            .applyToSocketSettings { builder ->
                builder
                    .connectTimeout(1000, TimeUnit.MILLISECONDS) // Connection timeout
                    .readTimeout(3000, TimeUnit.MILLISECONDS)   // Read timeout
            }
            .build()

        return MongoClients.create(settings)
    }
}