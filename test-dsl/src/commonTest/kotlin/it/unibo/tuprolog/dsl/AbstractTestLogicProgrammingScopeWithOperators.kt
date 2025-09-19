package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

abstract class AbstractTestLogicProgrammingScopeWithOperators<T1 : Any, T2 : Any>(
    private val first: T1,
    private val second: T2,
) : AbstractLogicProgrammingScopeTest<LogicProgrammingScopeWithOperators<*>>() {
    override fun createLogicProgrammingScope(): LogicProgrammingScopeWithOperators<*> = LogicProgrammingScope.empty()

    private fun testBinaryExpression(
        left: T1,
        right: T2,
        operator: String,
        operation: LogicProgrammingScopeWithOperators<*>.(T1, T2) -> Term,
    ) = logicProgramming {
        val actualResult = operation(left, right)
        val expectedResult = structOf(operator, left.toTerm(), right.toTerm())
        assertEquals(expectedResult, actualResult)
    }

    private val third = 1

    @Test
    fun testPlus() {
        testBinaryExpression(first, second, "+") { a, b -> plus(a, b) }
    }

    protected abstract fun LogicProgrammingScopeWithOperators<*>.plus(
        a: T1,
        b: T2,
    ): Term

    @Test
    fun testMinus() {
        testBinaryExpression(first, second, "-") { a, b -> minus(a, b) }
    }

    protected abstract fun LogicProgrammingScopeWithOperators<*>.minus(
        a: T1,
        b: T2,
    ): Term

    @Test
    fun testMultiply() {
        testBinaryExpression(first, second, "*") { a, b -> times(a, b) }
    }

    protected abstract fun LogicProgrammingScopeWithOperators<*>.times(
        a: T1,
        b: T2,
    ): Term

    @Test
    fun testDivide() {
        testBinaryExpression(first, second, "/") { a, b -> div(a, b) }
    }

    protected abstract fun LogicProgrammingScopeWithOperators<*>.div(
        a: T1,
        b: T2,
    ): Term

    @Test
    fun testEqualsTo() {
        testBinaryExpression(first, second, "=") { a, b -> a equalsTo b }
    }

    @Test
    fun testNotEqualsTo() {
        testBinaryExpression(first, second, "\\=") { a, b -> a notEqualsTo b }
    }

    @Test
    fun testGreaterThan() {
        testBinaryExpression(first, second, ">") { a, b -> a greaterThan b }
    }

    @Test
    fun testGreaterThanOrEqualsTo() {
        testBinaryExpression(first, second, ">=") { a, b -> a greaterThanOrEqualsTo b }
    }

    @Test
    fun testNonLowerThan() {
        testBinaryExpression(first, second, ">=") { a, b -> a nonLowerThan b }
    }

    @Test
    fun testLowerThan() {
        testBinaryExpression(first, second, "<") { a, b -> a lowerThan b }
    }

    @Test
    fun testLowerThanOrEqualsTo() {
        testBinaryExpression(first, second, "=<") { a, b -> a lowerThanOrEqualsTo b }
    }

    @Test
    fun testNonGreaterThan() {
        testBinaryExpression(first, second, "=<") { a, b -> a nonGreaterThan b }
    }

    @Test
    fun testIntDiv() {
        testBinaryExpression(first, second, "//") { a, b -> a intDiv b }
    }

    @Test
    fun testRem() {
        testBinaryExpression(first, second, "rem") { a, b -> rem(a, b) }
    }

    protected abstract fun LogicProgrammingScopeWithOperators<*>.rem(
        a: T1,
        b: T2,
    ): Term

    @Test
    fun testPow() {
        testBinaryExpression(first, second, "**") { a, b -> a pow b }
    }

    @Test
    fun testSup() {
        testBinaryExpression(first, second, "^") { a, b -> a sup b }
    }

    @Test
    fun testIs() {
        testBinaryExpression(first, second, "is") { a, b -> a `is` b }
    }

    @Test
    fun testThen() {
        testBinaryExpression(first, second, "->") { a, b -> a then b }
    }

    @Test
    fun testOr() {
        testBinaryExpression(first, second, ";") { a, b -> a or b }
    }

    @Test
    fun testAnd() {
        testBinaryExpression(first, second, ",") { a, b -> a and b }
    }

    @Test
    fun testMultipleAndsToTuple() =
        logicProgramming {
            val expected = tupleOf(sequenceOf(first, second, third).map { it.toTerm() })
            val actual = first and second and third
            assertEquals(expected, actual)
        }

    @Test
    fun testImpliedBy(): Unit =
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

    @Test
    fun testPlusOnListsIsAppend() =
        logicProgramming {
            assertEquals(listOf(1, 2, 3), listOf(1, 2) + 3)
            assertEquals(listOf(1, 2, 3), listOf(1, 2) + listOf(3))
        }

    @Test
    fun testImpliedByMultiple(): Unit =
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

    @Test
    fun testIf(): Unit =
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
