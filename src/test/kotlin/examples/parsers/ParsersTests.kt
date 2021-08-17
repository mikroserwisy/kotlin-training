package examples.parsers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ParsersTests : StringSpec({

    "should parse key value pair" {
        keyValuePair("firstName:jan") shouldBe mapOf("firstName" to "jan")
    }

    "should parse many key value pairs" {
        keyValuePair("firstName:jan\nlastName:Nowak") shouldBe mapOf("firstName" to "jan", "lastName" to "Nowak")
    }

})
