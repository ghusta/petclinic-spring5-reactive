package com.yetanotherdevblog

import com.yetanotherdevblog.petclinic.handlers.OwnersHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.kotlin.test.test
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class OwnersHandlerTest {

    @Autowired
    lateinit var ownersHandler: OwnersHandler

    @Test
    fun `findByNameLike returns no result`() {
        ownersHandler.findByNameLike("No Result")
                .test()
                .expectNextCount(0)
                .verifyComplete()
    }

}