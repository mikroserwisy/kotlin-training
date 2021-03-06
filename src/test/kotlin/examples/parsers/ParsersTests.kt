package examples.parsers

import examples.parsers.Result.Failure
import examples.parsers.Result.Success
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ParsersTests : StringSpec({

    /*"should parse key value pair" {
        keyValuePair("firstName:jan") shouldBe mapOf("firstName" to "jan")
    }*/

    /*"should parse many key value pairs" {
        keyValuePair("firstName:jan\nlastName:Nowak") shouldBe mapOf("firstName" to "jan", "lastName" to "Nowak")
    }*/

/*
  co jeśli pojawią się kolejne wymagania np:
      -pary jako wartości
      -linie z komentarzem
      -inne separatory
      -wyrażenia, które trzeba interpretować w specyficzny sposób

   Logika naszego parsera stanie się zawiła i skomplikowana.
   Jakimś pomysłem mogą wydawać się wyrażenia regularne, ale to zły pomysł :)
   Źeby rozwiązać problem w efektywny sposób wykorzystamy funkcje i możliwości ich kompozycji
   Możemy założyć (chociaż to uproszczenie), że parser to prosta funkcja
   (String) -> Result<T> gdzie Result będzie reprezentowało dwie potencjalne wartości
   sukces albo porażkę
*/

    "should parse prefix" {
        val parser = prefix("-")
        parser("-jan") shouldBe Success("-", "jan")
        parser("jan") shouldBe Failure("-", "jan")
    }

    "should parse integer" {
        integer("123jan") shouldBe Success(123, "jan")
        integer("jan") shouldBe Failure("integer", "jan")
    }

    "should parse whitespace" {
        whitespace("  jan") shouldBe Success("  ", "jan")
        whitespace("jan") shouldBe Failure("whitespace", "jan")
    }

    "should parse sequence" {
        val parser = sequence(prefix("-"), ::integer)
        parser("-123") shouldBe Success(Pair("-", 123), "")
        parser("123") shouldBe Failure("-", "123")
        val prefixAndInteger = prefix("-").then(::integer)
        prefixAndInteger("-123") shouldBe Success(Pair("-", 123), "")
    }

    "should parse one of" {
        val parser = oneOf(prefix("a"), prefix("b"))
        parser("ab") shouldBe Success("a", "b")
        parser("bc") shouldBe Success("b", "c")
        parser("cd") shouldBe Failure("a or b", "cd")
        val aOrB = prefix("a").or(prefix("b"))
        aOrB("ab") shouldBe Success("a", "b")
    }

    "should map" {
        integer("11").map { it % 2 == 0 } shouldBe Success(false, "")
    }

    "should skip first parser" {
        val parser = ::integer.before(prefix("a"))
        parser("1a") shouldBe Success("a", "")
    }

    "should skip following parser" {
        val parser = prefix("a").followedBy(::integer)
        parser("a1") shouldBe Success("a", "")
    }

    "should parse many" {
        val parser = prefix("a").many()
        parser("aaa") shouldBe Success(listOf("a", "a", "a"), "")
    }

    "should parse many with separartor" {
        val parser = ::integer.separatedBy(prefix(","))
        parser("1,2") shouldBe Success(listOf(1, 2), "")
        parser("1,a") shouldBe Failure("integer", "a")
        parser("a") shouldBe Success(emptyList(), "a")
    }

    "should parse expression with array of ints" {
        val text = "let ab = [1, 2, 3,  4]"

        val let = prefix("let").then(::whitespace.many())
        val variableName = prefixWhile { it.isLetter() }.followedBy(::whitespace.many())
        val assigment = prefix("=").followedBy(::whitespace.many())
        val values = ::integer.separatedBy(sequence(prefix(","), ::whitespace.many()))
        val array = prefix("[").before(values).followedBy(prefix("]"))
        val parser = let.before(variableName).followedBy(assigment).then(array)
        when (val result = parser(text)) {
            is Success -> println(result.match.second)
            is Failure -> println(result.expected)
        }
    }


})



