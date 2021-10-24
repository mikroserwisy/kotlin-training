package examples

import java.util.*

// Napisz program zliczający ilość wystąpień litery w tekście
fun ex1() {
    val letter = 'a'
    val text = "Ala ma kota"
    // var result = 0
    /*
    for (char in text) {
        if (char == letter) {
            result++
        }
    }
    */
    // text.forEach { if (it == letter) result++ }
    val result = text.filter { it == 'a' }.length
    println("Number of occurrences of letter $letter in \"$text\" is equal $result")
}

// Napisz program weryfikujący czy dany rok jest przestępny
// https://docs.microsoft.com/en-us/office/troubleshoot/excel/determine-a-leap-year
fun ex2() {
    val year = 2010
    val isLeap = when {
        year % 4 == 0 -> if (year % 100 == 0) year % 400 == 0 else true
        else -> false
    }
    println("Result: $isLeap")
}

// Napisz program sprawdzający, czy dany znak to litera (duża lub mala ASCII) z wykorzystaniem instrukcji warunkowych i range
fun ex3() {
    val char = 'a'
    if (char in 'a'..'z' || char in 'A'..'Z') {
        println("$char is in alphabet")
    } else {
        println("$char is not in alphabet")
    }
}

// Napisz program obliczający wartość silni dla zadanej liczby całkowitej, używając pętli for, a następnie while
fun ex4() {
    val value = 5
    var factorial = 1L
    /*for (number in 1..value) {
        factorial *= number
    }*/
    var number = 1
    while (number <= value) {
        factorial *= number
        number++
    }
    println("Result for $value! is equal $factorial")
}

// Napisz program działający jak prosty kalkulator konsolowy
fun ex5() {
    val reader = Scanner(System.`in`)
    var result = 0.0
    while (true) {
        printMenu()
        when (reader.next()) {
            "0" -> result = 0.0
            "1" -> result += reader.nextDouble()
            "2" -> result -= reader.nextDouble()
            "3" -> result *= reader.nextDouble()
            "4" -> result /= reader.nextDouble()
            else -> break
        }
        println("Result: $result")
    }
}

fun printMenu() {
    println("0 - Reset")
    println("1 - Plus")
    println("2 - Minus")
    println("3 - Multiply")
    println("4 - Divide")
}

/* Zaimplementuj grę tic tac toe
-gracze wykonują ruchy na przemian, zajmują kolejne pola, stawiając swój znak
-gracze mogą zajmować tylko wolne pola
-gra kończy się gdy:
    -nie można wykonać więcej ruchów
    -jeden z graczy zajmuje sekwencję wygrywającą
-istnieje możliwość odczytu/zapisu stanu gry
 */