import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

interface Showable {

    fun show(text: String)

}

class ConsoleShowable : Showable {

    override fun show(text: String) = println(text)

}

class StateChangeLogger {

    operator fun getValue(ref: Any?, property: KProperty<*>) = "$ref, property name: ${property.name}"

    operator fun setValue(ref: Any?, property: KProperty<*>, value: String) {
        println("$ref, property name: ${property.name}, value: $value")
    }

}

class Account(var balance: Long, showable: Showable): Showable by showable {

    var description: String by StateChangeLogger()

    val hash: String by lazy {
        println("Computing hash")
        UUID.randomUUID().toString()
    }

    var number: String by Delegates.observable("not assigned") { property, oldValue, newValue ->
        println("Property \"${property.name}\" was changed from $oldValue to $newValue")
    }

    override fun show(text: String) {
        println("Account show: $text")
    }

}

class Person(map: Map<String, Any?>) {

    val firstName: String by map
    val age: Int by map

}


fun main() {
    val account = Account(1, ConsoleShowable())
    val hash = account.hash
    account.hash
    account.description = "My account"
    println(account.description)
    account.show("Test")
    account.number = "10"

    val properties = mapOf("firstName" to "Jan", "age" to 10)
    val  person = Person(properties)
    println(person.firstName)

    println("Test" hash '*')
    val point = -Point(1, 2)
    val secondPoint = Point(3, 3)
    println(point)
    println(point + secondPoint)
    val (x, _) = point
    val (_, y) = getPoint()
    println(x)
}

fun getPoint() = Point(1, 2)

infix fun String.hash(char: Char) =  this.map { char }.joinToString("")

data class Point(val x: Int, val y: Int) {

    operator fun plus(point: Point): Point = Point(x + point.x, y + point.y)

}

operator fun Point.unaryMinus() = Point(-x, -y)


