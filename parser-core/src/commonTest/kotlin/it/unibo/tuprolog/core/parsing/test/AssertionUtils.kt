package it.unibo.tuprolog.core.parsing.test

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.parsing.TermParser

/** Asserts terms are equal taking into account differences in complete variables names */
fun assertTermsAreEqual(
    expected: Term,
    actual: Term,
) {
    fun message(mistake: String) =
        """Comparing:
        |   actual: $actual
        |     type: ${actual::class}
        | expected: $expected
        |     type: ${expected::class}
        | Problem: $mistake
        """.trimMargin()

    actual.isGround shouldBe expected.isGround
    if (expected.isGround) {
        withClue(message("one is ground the other is not")) { actual shouldBe expected }
    } else {
        when {
            expected is Var && actual is Var -> {
                withClue(message("different var name")) { actual.name shouldBe expected.name }
            }
            expected is Constant && actual is Constant -> {
                withClue(message("different constant name")) { actual shouldBe expected }
            }
            expected is Struct && actual is Struct -> {
                withClue(message("different functor")) { actual.functor shouldBe expected.functor }
                withClue(message("different arity")) { actual.arity shouldBe expected.arity }
                for (i in 0 until expected.arity) {
                    assertTermsAreEqual(expected[i], actual[i])
                }
            }
        }
        withClue(message("different amount of variables")) {
            actual.variables.toSet().size shouldBe
                expected.variables.toSet().size
        }
    }
}

fun TermParser.assertTermIsCorrectlyParsed(
    stringToBeParsed: String,
    expected: Term,
    loggingOn: Boolean = false,
) {
    if (loggingOn) println("Parsing:\n\t$stringToBeParsed")

    try {
        val actual = parseTerm(stringToBeParsed)
        if (loggingOn) println("Result:\n\t$actual")
        if (loggingOn) println("Expected:\n\t$expected")

        assertTermsAreEqual(actual, expected)

        if (loggingOn) println("".padEnd(80, '-'))
    } catch (e: Exception) {
        throw Exception("Parsing failed for term:\n\t$stringToBeParsed\n", e)
    }
}
