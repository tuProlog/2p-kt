package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.test.Test
import kotlin.test.assertEquals

class TestLogicProgrammingScopeWithOperators :
    AbstractLogicProgrammingScopeTest<LogicProgrammingScopeWithOperators<*>>() {
    override fun createLogicProgrammingScope(): LogicProgrammingScopeWithOperators<*> = LogicProgrammingScope.empty()

    private fun <T1 : Any, T2 : Any> testBinaryExpression(
        left: T1,
        right: T2,
        operator: String,
        operation: LogicProgrammingScopeWithOperators<*>.(T1, T2) -> Term,
    ) = logicProgramming {
        val actualResult = operation(left, right)
        val expectedResult = structOf(operator, left.toTerm(), right.toTerm())
        assertEquals(expectedResult, actualResult)
    }

    private val first = Atom.of("a")
    private val second = "X"
    private val third = 1

    @Test
    fun testPlus() {
        testBinaryExpression(first, second, "+") { a, b -> a + b }
    }

    @Test
    fun testMinus() {
        testBinaryExpression(first, second, "-") { a, b -> a - b }
    }

    @Test
    fun testMultiply() {
        testBinaryExpression(first, second, "*") { a, b -> a * b }
    }

    @Test
    fun testDivide() {
        testBinaryExpression(first, second, "/") { a, b -> a / b }
    }

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
        testBinaryExpression(first, second, "rem") { a, b -> a rem b }
    }

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

    init {
        listOf(1).asIterable().plus(1)
    }

    @Test
    fun testImpliedBy() {
        testBinaryExpression(first, second, ":-") { a, b -> a impliedBy b }
    }

    @Test
    fun testImpliedByMultiple() =
        logicProgramming {
            val expected = ruleOf(first, second.toTerm(), third.toTerm())
            val actual = first.impliedBy(second, third)
            assertEquals(expected, actual)
        }

    @Test
    fun testIf() {
        testBinaryExpression(first, second, ":-") { a, b -> a `if` b }
    }
}
