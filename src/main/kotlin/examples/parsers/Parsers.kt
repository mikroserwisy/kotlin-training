package examples.parsers

import examples.parsers.Result.Failure
import examples.parsers.Result.Success

/*fun keyValuePair(input: String): Map<String, String> {
    val (key, value) = input.split(":")
    return mapOf(key to value)
}*/

/*fun keyValuePair(input: String): Map<String, String> {
    return input.split("\n").map {
        val (key, value) = it.split(":")
        (key to value)
    }.toMap()
}*/

sealed class Result<out T> {
    data class Success<T>(val match: T, var remainder: String) : Result<T>()
    data class Failure(val expected: String, var remainder: String) : Result<Nothing>()

    fun <U> map(f: (T) -> U): Result<U> = when (this) {
        is Failure -> this
        is Success -> Success(f(match), remainder)
    }

    fun <U> flatMap(f: (T, String) -> Result<U>): Result<U> = when (this) {
        is Failure -> this
        is Success -> f(match, remainder)
    }

    fun mapExpected(f: (String) -> String): Result<T> = when (this) {
        is Success -> this
        is Failure -> Failure(f(expected), remainder)
    }

}

typealias Parser<T> = (String) -> Result<T>

fun prefix(value: String): Parser<String> = { input ->
    if (input.startsWith(value)) {
        Success(value, input.substring(value.length))
    } else {
        Failure(value, input)
    }
}

fun integer(input: String): Result<Int> {
    val match = input.takeWhile { it.isDigit() }
    return if (match.isNotEmpty()) {
        Success(match.toInt(), input.substring(match.length))
    } else {
        Failure("integer", input)
    }
}

fun whitespace(input: String): Result<String> {
    val match = input.takeWhile { it.isWhitespace() }
    return if (match.isNotEmpty()) {
        Success(match, input.substring(match.length))
    } else {
        Failure("whitespace", input)
    }
}

/*fun <T1, T2> sequence(firstParser: Parser<T1>, secondParser: Parser<T2>): Parser<Pair<T1, T2>> = { input ->
    when(val firstResult = firstParser(input)) {
        is Failure -> firstResult
        is Success -> when (val secondResult = secondParser(firstResult.remainder)) {
            is Failure -> secondResult
            is Success -> Success(Pair(firstResult.match, secondResult.match), secondResult.remainder)
        }
    }
}*/


/*fun <T1, T2> sequence(firstParser: Parser<T1>, secondParser: Parser<T2>): Parser<Pair<T1, T2>> = { input ->
    when(val firstResult = firstParser(input)) {
        is Failure -> firstResult
        is Success -> secondParser(firstResult.remainder).map { Pair(firstResult.match, it) }
    }
}*/

fun <T1, T2> sequence(firstParser: Parser<T1>, secondParser: Parser<T2>): Parser<Pair<T1, T2>> = { input ->
    firstParser(input)
        .flatMap { firstResult, remainder -> secondParser(remainder).map { Pair(firstResult, it) } }
}

infix fun <T1, T2> Parser<T1>.then(otherParser: Parser<T2>): Parser<Pair<T1, T2>> = sequence(this, otherParser)

fun <T> oneOf(firstParser: Parser<T>, secondParser: Parser<T>): Parser<T> = { input ->
    when (val firstResult = firstParser(input)) {
        is Success -> firstResult
        is Failure -> secondParser(input).mapExpected { "${firstResult.expected} or $it" }
    }
}

infix fun <T> Parser<T>.or(otherParser: Parser<T>): Parser<T> = oneOf(this, otherParser)

fun <T, U> Parser<T>.map(f: (T) -> U): Parser<U> = { this(it).map(f) }

fun <X, T> Parser<X>.before(p: Parser<T>): Parser<T> = sequence(this, p).map { it.second }

fun <T, Y> Parser<T>.followedBy(y: Parser<Y>): Parser<T> = sequence(this, y).map { it.first  }