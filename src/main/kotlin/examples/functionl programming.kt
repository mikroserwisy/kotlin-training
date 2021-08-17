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
