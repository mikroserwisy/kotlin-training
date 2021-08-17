class Person(userName: String) {

    val name = userName

    val isEmpty: Boolean
        get() = name.isEmpty()

    var description = ""
        get() = "Person with name $field"
        set(value) {
            field = value.uppercase()
        }

    private var subscriber = false

    var isSubscriber: String
        get() = "Is subscriber: $subscriber"
        set(value) {
            subscriber = !subscriber
        }

    lateinit var jobDescription: String

    init {
        if (::jobDescription.isInitialized) {
            println(jobDescription)
        }
    }

    val subject = "Tester"

    companion object {

        fun sayHello() {
            println("Hello")
        }

    }

}

class Student {

    val subject = "Programming"

    fun Person.learn() {
        println("My score is: $score")
        println("My subject id: $this@Student.subject")
    }

    fun show(person: Person) {
        person.learn()
    }

}


fun main() {
    val person = Person("Jan")
    println(person.name)
    println(person.isEmpty)
    println(person.description)
    person.description = "Marek"
    println(person.description)
    //person.name = ""
    person.jobDescription = "Tester"
    person.showInfo()
    println(person.score)
    Person.sayHello()
    Person.sayGoodbye()
    val student = Student()
    student.show(person)
    val intContainer = Container(1)
    println(intContainer.value)
    val stringContainer = Container("Text")
    println(stringContainer.value)

    convert(2.0)
    convert("Text")
}

fun Person.showInfo() {
    println("Person info: $name")
}

val Person.score: Int
    get() = name.length * 10

fun Person.Companion.sayGoodbye() = println("Goodbye")

class IntContainer(val value: Int)

class DoubleContainer(val value: Double)

fun convert(value: Int): String = value.toString()

fun convert(value: String): Double = value.toDouble()

class Container<V>(val value: V)

fun <S> convert(value: S) =  value.toString()