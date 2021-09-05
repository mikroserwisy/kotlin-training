package examples.templates

import java.lang.IllegalArgumentException

class Template(private val textWithExpressions: String) {

    private val expression = ".*#\\{\\w+}.*".toRegex()
    private val invalidParameter = "\\W+".toRegex()

    fun evaluate(parameters: Map<String, String>): String {
        validateParameters(parameters)
        val result = substituteParameters(textWithExpressions, parameters)
        validate(result)
        return result
    }

    private fun validateParameters(parameters: Map<String, String>) {
        if (parameters.values.stream().anyMatch { it.matches(invalidParameter) }) {
            throw IllegalArgumentException()
        }
    }

    private fun substituteParameters(textWithExpressions: String, parameters: Map<String, String>): String {
        var result = textWithExpressions
        for ((name, value) in parameters) {
            result = "#\\{${name}}".toRegex().replace(result, value)
        }
        return result
    }

    private fun validate(resultText: String) {
        if (resultText.matches(expression)) {
            throw IllegalArgumentException()
        }
    }

}
