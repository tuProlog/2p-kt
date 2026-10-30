package it.unibo.tuprolog.dsl

import io.kotest.core.spec.style.funSpec
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import kotlin.test.assertEquals
import kotlin.test.assertFails

fun <T1 : Any, T2 : Any> operatorsTests(
    first: T1,
    second: T2,
    operators: ArithmeticOperators<T1, T2>,
) = funSpec {
    with(LogicProgrammingScopeTester<LogicProgrammingScopeWithOperators<*>> { LogicProgrammingScope.empty() }) {
        with(operators) {
            fun testBinaryExpression(
                left: T1,
                right: T2,
                operator: String,
                operation: LogicProgrammingScopeWithOperators<*>.(T1, T2) -> Term,
            ) = logicProgramming {
                val actualResult = operation(left, right)
                val expectedResult = structOf(operator, left.toTerm(), right.toTerm())
                assertEquals(expectedResult, actualResult)
            }

            val third = 1

            test("plus") {
                testBinaryExpression(first, second, "+") { a, b -> plus(a, b) }
            }

            test("minus") {
                testBinaryExpression(first, second, "-") { a, b -> minus(a, b) }
            }

            test("multiply") {
                testBinaryExpression(first, second, "*") { a, b -> times(a, b) }
            }

            test("divide") {
                testBinaryExpression(first, second, "/") { a, b -> div(a, b) }
            }

            test("equals-to") {
                testBinaryExpression(first, second, "=") { a, b -> a equalsTo b }
            }

            test("not-equals-to") {
                testBinaryExpression(first, second, "\\=") { a, b -> a notEqualsTo b }
            }

            test("greater-than") {
                testBinaryExpression(first, second, ">") { a, b -> a greaterThan b }
            }

            test("greater-than-or-equals-to") {
                testBinaryExpression(first, second, ">=") { a, b -> a greaterThanOrEqualsTo b }
            }

            test("non-lower-than") {
                testBinaryExpression(first, second, ">=") { a, b -> a nonLowerThan b }
            }

            test("lower-than") {
                testBinaryExpression(first, second, "<") { a, b -> a lowerThan b }
            }

            test("lower-than-or-equals-to") {
                testBinaryExpression(first, second, "=<") { a, b -> a lowerThanOrEqualsTo b }
            }

            test("non-greater-than") {
                testBinaryExpression(first, second, "=<") { a, b -> a nonGreaterThan b }
            }

            test("int-div") {
                testBinaryExpression(first, second, "//") { a, b -> a intDiv b }
            }

            test("rem") {
                testBinaryExpression(first, second, "rem") { a, b -> rem(a, b) }
            }

            test("pow") {
                testBinaryExpression(first, second, "**") { a, b -> a pow b }
            }

            test("sup") {
                testBinaryExpression(first, second, "^") { a, b -> a sup b }
            }

            test("is") {
                testBinaryExpression(first, second, "is") { a, b -> a `is` b }
            }

            test("then") {
                testBinaryExpression(first, second, "->") { a, b -> a then b }
            }

            test("or") {
                testBinaryExpression(first, second, ";") { a, b -> a or b }
            }

            test("and") {
                testBinaryExpression(first, second, ",") { a, b -> a and b }
            }

            test("multiple-ands-to-tuple") {
                logicProgramming {
                    val expected = tupleOf(sequenceOf(first, second, third).map { it.toTerm() })
                    val actual = first and second and third
                    assertEquals(expected, actual)
                }
            }

            test("implied-by") {
                logicProgramming {
                    val firstTerm = first.toTerm()
                    if (firstTerm is Struct) {
                        testBinaryExpression(first, second, ":-") { a, b -> a impliedBy b }
                    } else {
                        assertFails {
                            first impliedBy second
                        }
                    }
                }
            }

            test("plus-on-lists-is-append") {
                logicProgramming {
                    assertEquals(listOf(1, 2, 3), listOf(1, 2) + 3)
                    assertEquals(listOf(1, 2, 3), listOf(1, 2) + listOf(3))
                }
            }

            test("implied-by-multiple") {
                logicProgramming {
                    val firstTerm = first.toTerm()
                    if (firstTerm is Struct) {
                        val expected = ruleOf(firstTerm, second.toTerm(), third.toTerm())
                        val actual = firstTerm.impliedBy(second, third)
                        assertEquals(expected, actual)
                    } else {
                        assertFails {
                            first.impliedBy(second, third)
                        }
                    }
                }
            }

            test("if") {
                logicProgramming {
                    val firstTerm = first.toTerm()
                    if (firstTerm is Struct) {
                        testBinaryExpression(first, second, ":-") { a, b -> a `if` b }
                    } else {
                        assertFails {
                            first `if` second
                        }
                    }
                }
            }
        }
    }
}

/** The operators whose overload, picked statically for [T1] and [T2], is under test. */
interface ArithmeticOperators<T1, T2> {
    fun LogicProgrammingScopeWithOperators<*>.plus(
        a: T1,
        b: T2,
    ): Term

    fun LogicProgrammingScopeWithOperators<*>.minus(
        a: T1,
        b: T2,
    ): Term

    fun LogicProgrammingScopeWithOperators<*>.times(
        a: T1,
        b: T2,
    ): Term

    fun LogicProgrammingScopeWithOperators<*>.div(
        a: T1,
        b: T2,
    ): Term

    fun LogicProgrammingScopeWithOperators<*>.rem(
        a: T1,
        b: T2,
    ): Term
}
