package com.yetanotherdevblog

import com.yetanotherdevblog.petclinic.toLocalDate
import com.yetanotherdevblog.petclinic.toStr
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
class VariousTests {

    /**
     * @Value injection need escaping of the `$` char
     * Or you can instead use ConfigurationProperties
     * Or change that special character
     */
    @Value("\${custom.property}")
    lateinit var customProperty : String

    /**
     * Method names can be free text by wrapping them around `...`
     */
    @Test
    fun `@Value properties needs escaping`() {
        assertEquals(customProperty, "Lorem Ipsum")
    }

    @Test
    fun `Test LocalDate#toStr extension method`() {
        assertEquals(LocalDate.of(1970, 1, 1).toStr(), "01/01/1970")
    }

    @Test
    fun `Test String#toLocalDate extension method`() {
        assertEquals("01/01/1970".toLocalDate(), LocalDate.of(1970, 1, 1))
    }

}
