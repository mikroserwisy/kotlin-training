package examples.parsers

/*fun keyValuePair(input: String): Map<String, String> {
    val (key, value) = input.split(":")
    return mapOf(key to value)
}*/

fun keyValuePair(input: String): Map<String, String> {
    return input.split("\n").map {
        val (key, value) = it.split(":")
        (key to value)
    }.toMap()
}