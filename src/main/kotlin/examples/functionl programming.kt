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

fun <A> tail(xs: List<A>) = when (xs) {
    is Cons -> xs.tail
    else -> Nil
}

fun <A> prepend(xs: List<A>, x: A) = when (xs) {
    is Cons -> Cons(x, xs)
    else -> Nil
}

tailrec fun <A> drop(xs: List<A>, n: Int): List<A> =
    if (n <= 0) xs else when (xs) {
        is Cons -> drop(xs.tail, n - 1)
        else -> Nil
    }

tailrec fun <A> dropWhile(xs: List<A>, predicate: (A) -> Boolean): List<A> = when (xs) {
    is Cons -> if (predicate(xs.head)) dropWhile(xs.tail, predicate) else xs
    else -> xs
}

fun <A> append(xs1: List<A>, xs2: List<A>): List<A> = when (xs1) {
    is Nil -> xs2
    is Cons -> Cons(xs1.head, append(xs1.tail, xs2))
}

fun <A, B> foldRight(xs: List<A>, value: B, f: (A, B) -> B): B = when (xs) {
    is Nil -> value
    is Cons -> f(xs.head, foldRight(xs.tail, value, f))
}

fun sumFr(xs: List<Int>) = foldRight(xs, 0) { a, b -> a + b }
fun productFr(xs: List<Int>) = foldRight(xs, 1.0) { a, b -> a * b }
fun lengthFr(xs: List<Int>) = foldRight(xs, 0) { _, len -> 1 + len }

tailrec fun <A, B> foldLeft(xs: List<A>, value: B, f: (B, A) -> B): B = when (xs) {
    is Nil -> value
    is Cons -> foldLeft(xs.tail, f(value, xs.head), f)
}

fun sumFl(xs: List<Int>) = foldLeft(xs, 0) { a, b -> a + b }
fun productFl(xs: List<Int>) = foldLeft(xs, 1.0) { a, b -> a * b }
fun lengthFl(xs: List<Int>) = foldLeft(xs, 0) { len, _ -> 1 + len }

fun example9() {
    val numbers = List.of(1, 2, 3, 4)
    println(sum(numbers))
    println(product(List.of(1.0, 2.0, 3.0, 4.0)))
    println(tail(numbers))
    println(prepend(numbers, 0))
    println(drop(numbers, 2))
    println(dropWhile(numbers) { it < 3 })
    println(append(numbers, List.of(5, 6, 7)))
    println(sumFr(numbers))
    println(productFr(numbers))
    println(lengthFr(numbers))
    println(sumFl(numbers))
    println(productFl(numbers))
    println(lengthFl(numbers))
}

// Tree
sealed class Tree<out A>
data class Leaf<A>(val value: A) : Tree<A>()
data class Branch<A>(val left: Tree<A>, val right: Tree<A>) : Tree<A>()

fun <A> numberOfNodes(tree: Tree<A>): Int = when (tree) {
    is Leaf -> 1
    is Branch -> 1 + numberOfNodes(tree.left) + numberOfNodes(tree.right)
}

fun <A> maxDepth(tree: Tree<A>): Int = when (tree) {
    is Leaf -> 0
    is Branch -> 1 + maxOf(maxDepth(tree.left), maxDepth(tree.right))
}

fun <A, B> map(tree: Tree<A>, f: (A) -> B): Tree<B> = when (tree) {
    is Leaf -> Leaf(f(tree.value))
    is Branch -> Branch(map(tree.left, f), map(tree.right, f))
}

fun <A, B> fold(tree: Tree<A>, f: (A) -> B, b: (B, B) -> B): B = when (tree) {
    is Leaf -> f(tree.value)
    is Branch -> b(fold(tree.left, f, b), fold(tree.right, f, b))
}

fun <A> numberOfNodeF(tree: Tree<A>) = fold(tree, { 1 }, { b1, b2 -> 1 + b1 + b2 })

fun <A> maxDepthF(tree: Tree<A>) = fold(tree, { 0 }, { b1, b2 -> 1 + maxOf(b1, b2)})

fun <A, B> mapF(tree: Tree<A>, f: (A) -> B) = fold(tree, { a: A -> Leaf(f(a)) }, { b1: Tree<B>, b2: Tree<B> -> Branch(b1, b2)})

// Separation of side effects
fun fahrenheitToCelsius(value: Double): Double = (value - 32) * 5.0 / 9.0
fun temperatureToString(temperature: Double) = "Temperatur is equal ${String.format("%.2f", temperature)}°"
// fun write(text: String): Unit = println(text)

/*
fun write(text: String) = object : IO {

    override fun run() = println(text)

}

interface IO {

    fun run()

}
*/

interface IO<A> {

    fun run(): A

    fun <B> map(f: (A) -> B): IO<B> = object : IO<B> {

        override fun run(): B = f(this@IO.run())

    }

    fun <B> flatMap(f: (A) -> IO<B>): IO<B> = object : IO<B> {

        override fun run(): B = f(this@IO.run()).run()

    }

    infix fun <B> combine(io: IO<B>): IO<Pair<A, B>> = object : IO<Pair<A, B>> {

        override fun run(): Pair<A, B> = this@IO.run() to io.run()

    }

    companion object {

        fun <A> unit(a: () -> A) = object : IO<A> {

            override fun run(): A = a()

        }

        operator fun <A> invoke(a: () -> A) = unit(a)

    }

}

// fun read(): String = readLine().orEmpty()
fun read(): IO<String> = IO { readLine().orEmpty() }
fun write(text: String): IO<Unit> = IO { println(text) }
fun toFixed(value: Double) = String.format("%.2f", value)
val temperature = compose(::toFixed, ::fahrenheitToCelsius) // temperatureToString(fahrenheitToCelsius(x))

val echo: IO<Unit> = read().flatMap(::write)

fun main() {
    // write(temperatureToString(fahrenheitToCelsius(70.0)))
    // write(temperatureToString(fahrenheitToCelsius(70.0))).run()
    // echo.run()
    write("Enter a temperature in degrees Fahrenheit: ")
        .flatMap { read().map { it.toDouble() } }
        .map { temperature(it) }
        .flatMap { write("Degrees Celsius: $it°") }
        .run()
}

/*
Napisz kalkulator podatkowy, który pozwoli na określenie całkowitej kwoty podatku do zapłacenia.
Założenia:
-podatków może być kilka - trzeba je wszystkie uwzględnić
-sposób naliczania podatku może się w przyszłości zmienić (zmiana zasad, nowe podatki)
-występują ulgi/odpisy od podatku

Zadanie wykonaj obiektowo lub funkcyjnie
*/