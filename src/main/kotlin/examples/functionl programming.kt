package examples

// Pure function
fun abs(value: Int) = if (value < 0) -value else value
fun add(a: Int, b: Int) = a + b

// Referential transparency
fun example1() {
    println(3 * add(1, 2) == 3 * (1 + 2))
}

// Recursion instead of loops
fun factorial(number: Int): Int {
    tailrec fun loop(n: Int, result: Int = 1): Int = if (n <= 0) result else loop(n -1 , n * result)
    return loop(number)
}

// Higher-order functions
fun getResult(value: Int, fn: (Int) -> Int) = "The result for %d is %d".format(value, fn(value))

fun example2() {
    println(getResult(5, ::factorial))
    println(getResult(-5, ::abs))
}

// Polymorphic functions
typealias Predicate<T> = (T) -> Boolean

fun <E> findFirst(xs: Array<E>, predicate: Predicate<E>): Int {
    tailrec fun loop(index: Int): Int = when {  // simple pattern matching
        index == xs.size -> -1
        predicate(xs[index]) -> index
        else -> loop(index + 1)
    }
    return loop(0)
}

fun example3() {
    val numbers = arrayOf(1, 2, 3, 4)
    val result = findFirst(numbers) { it > 5 }
    println(result)
}

// Partial application
fun <A, B, C> partial(a: A, fn: (A, B) -> C): (B) -> C = { b -> fn(a, b) }

fun example4() {
    val add3 = partial(3, ::add)
    println(add3(4))
    val findInNumbers = partial(arrayOf(1, 2, 3, 4), ::findFirst)
    println(findInNumbers { it == 2 })
    println(findInNumbers { it == 3 })
}

// Curring and composition
fun <A, B, C> curry(fn: (A, B) -> C): (A) -> (B) -> C = { a: A -> { b: B -> fn(a, b) } }
fun <A, B, C> uncurry(fn: (A) -> (B) -> C): (A, B) -> C = { a: A, b: B -> fn(a)(b) }
fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C = { a: A -> f(g(a)) }

fun example5() {
    val curriedAdd = curry(::add)
    val add3 = curriedAdd(3)
    println(add3(4))
    val standardAdd = uncurry(curriedAdd)
    println(standardAdd(3, 4))
    val absAdd3 = compose(add3, ::abs)
    println(absAdd3(-5))
}

// Functional data structures
// Option
sealed class Option<out A>
data class Some<out A>(val value: A) : Option<A>()
object None : Option<Nothing>()

fun <A, B> Option<A>.map(f: (A) -> B) = when (this) {
    is None -> None
    is Some -> Some(f(value))
}

fun <A, B> Option<A>.flatMap(f: (A) -> Option<B>): Option<B> = map(f).getOrElse { None }

fun <A> Option<A>.getOrElse(default: () -> A): A = when (this) {
    is None -> default()
    is Some -> value
}

fun <A> Option<A>.orElse(ob: () -> Option<A>): Option<A> = map { Some(it) }.getOrElse { ob() }

fun <A> Option<A>.filter(f: (A) -> Boolean): Option<A> = flatMap { a -> if (f(a)) Some(a) else None }

fun <A, B> lift(f: (A) -> B): (Option<A>) -> Option<B> = { oa -> oa.map(f) }

data class Employee(val name: String, val department: String, val manager: Option<String>)
fun createEmployee(name: String) = Some(Employee(name, "IT", None))

fun example6() {
    val department = createEmployee("Tom")
        .map { it.department }
        .getOrElse { "Unknown" }
    println(department)
    val manager = createEmployee("Jan")
        .flatMap { it.manager }
        .getOrElse { "Unknown" }
    println(manager)
    val safeAbs: (Option<Double>) -> Option<Double> = lift { kotlin.math.abs(it) }
    println(safeAbs(Some(-1.0)))
    println(safeAbs(None))
}

// Either
sealed class Either<out E, out A>
data class Left<out E>(val value: E) : Either<E, Nothing>()
data class Right<out A>(val value: A) : Either<Nothing, A>()

fun <E, A, B> Either<E, A>.map(f: (A) -> B): Either<E, B> = when (this) {
    is Left -> this
    is Right -> Right(f(value))
}

fun <E, A> Either<E, A>.orElse(f: () -> Either<E, A>): Either<E, A> = when (this) {
    is Left -> f()
    is Right -> this
}

fun safeDiv(x: Int, y: Int): Either<String, Int> =
    try {
        Right(x / y)
    } catch (e: Exception) {
        Left(e.message ?: "error")
    }

fun example7() {
    println(safeDiv(6, 2))
    println(safeDiv(6, 0))
}

// List

sealed class List<out A> {

    companion object {

        fun <A> of(vararg xs: A): List<A> {
            val tail = xs.sliceArray(1 until xs.size)
            return if (xs.isEmpty()) Nil else Cons(xs[0], of(*tail))
        }

    }

}
object Nil : List<Nothing>()
data class Cons<out A>(val head: A, val tail: List<A>) : List<A>()

fun example8() {
    val emptyList = Nil
    val listOfStrings = Cons("java", Cons("Kotlin", Nil))
    val numbers = List.of(1, 2, 3, 4)
}

fun sum(xs: List<Int>): Int = when (xs) {
    is Nil -> 0
    is Cons -> xs.head + sum(xs.tail)
}

fun product(xs: List<Double>): Double = when (xs) {
    is Nil -> 1.0
    is Cons -> if (xs.head == 0.0) 0.0 else xs.head * product(xs.tail)
}

fun main() {
    println(sum(List.of(1, 2, 3, 4)))
    println(product(List.of(1.0, 2.0, 3.0, 4.0)))
}
















