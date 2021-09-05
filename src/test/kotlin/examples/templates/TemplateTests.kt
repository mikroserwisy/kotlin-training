package examples.templates

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class TemplateTests {

    private val sut = Template("My name is #{firstName} #{lastName}")

    @Test
    fun should_evaluate_text_with_expressions() {
        val parameters = mapOf("firstName" to "Jan", "lastName" to "Kowalski")
        assertEquals("My name is Jan Kowalski", sut.evaluate(parameters))
    }

    @Test
    fun should_throw_exception_when_parameter_is_missing() {
        assertThrows(IllegalArgumentException::class.java) { sut.evaluate(emptyMap()) }
    }

    @Test
    fun should_accept_only_alphanumeric_parameters() {
        val parameters = mapOf("firstName" to "**", "lastName" to "**")
        assertThrows(IllegalArgumentException::class.java) { sut.evaluate(parameters) }
    }

}