package com.yetanotherdevblog

import com.yetanotherdevblog.config.MongoClientConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(MongoClientConfig::class)
class ReactiveApplication

fun main(args: Array<String>) {
    SpringApplication.run(ReactiveApplication::class.java, *args)
}
