package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.dsl.unify.LogicProgrammingScope
import kotlin.test.assertEquals

/** Asserts terms are equal taking into account differences in complete variables names */
fun assertTermsAreEqual(
    expected: Term,
    actual: Term,
) {
    assertEquals(expected.isGround, actual.isGround)
    if (expected.isGround) {
        assertEquals(
            expected,
            actual,
            message =
                """Comparing:
            |   actual: $actual
            |     type: ${actual::class}
            | expected: $expected
            |     type: ${expected::class}
                """.trimMargin(),
        )
    } else {
        when {
            expected is Var && actual is Var -> {
                assertEquals(expected.name, actual.name)
            }
            expected is Constant && actual is Constant -> {
                assertEquals(expected, actual)
            }
            expected is Struct && actual is Struct -> {
                assertEquals(expected.functor, actual.functor)
                assertEquals(expected.arity, actual.arity)
                for (i in 0 until expected.arity) {
                    assertTermsAreEqual(expected[i], actual[i])
                }
            }
        }
        assertEquals(
            expected.variables.toSet().size,
            actual.variables.toSet().size,
        )
    }
}

fun TermParser.assertTermIsCorrectlyParsed(
    stringToBeParsed: String,
    expectedGenerator: LogicProgrammingScope.() -> Term,
) {
    assertTermIsCorrectlyParsed(stringToBeParsed, LogicProgrammingScope.of().expectedGenerator())
}

fun TermParser.assertTermIsCorrectlyParsed(
    stringToBeParsed: String,
    expected: Term,
    loggingOn: Boolean = false,
) {
    if (loggingOn) println("Parsing:\n\t$stringToBeParsed")

    val actual = parseTerm(stringToBeParsed)

    if (loggingOn) println("Result:\n\t$actual")
    if (loggingOn) println("Expected:\n\t$expected")

    assertTermsAreEqual(actual, expected)

    if (loggingOn) println("".padEnd(80, '-'))
}
