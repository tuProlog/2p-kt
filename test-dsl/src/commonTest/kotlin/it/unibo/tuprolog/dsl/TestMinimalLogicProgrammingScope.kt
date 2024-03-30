package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.dsl.PlatformSpecificValues.MINUS_THREE
import it.unibo.tuprolog.dsl.PlatformSpecificValues.ONE_POINT_ZERO
import it.unibo.tuprolog.dsl.PlatformSpecificValues.THREE_POINT_ONE_DOUBLE
import it.unibo.tuprolog.dsl.PlatformSpecificValues.THREE_POINT_ONE_FLOAT
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TestMinimalLogicProgrammingScope : AbstractLogicProgrammingScopeTest<MinimalLogicProgrammingScope<*>>() {
    override fun createLogicProgrammingScope(): MinimalLogicProgrammingScope<*> = LogicProgrammingScope.empty()

    @Test
    fun testSubScope() {
        val scope = createLogicProgrammingScope()
        val subScope = scope.newScope()
        val var1 = scope.varOf("X")
        val var2 = subScope.varOf("X")
        assertStructurallyEquals(var1, var2)
    }

    @Test
    fun testSubScoping() {
        val scope = createLogicProgrammingScope()
        val var1 = scope.varOf("X")
        scope.scope {
            val var2 = varOf("X")
            assertStructurallyEquals(var1, var2)
        }
    }

    @Test
    fun testFunctorExpression() =
        logicProgramming {
            val expected =
                structOf(
                    "functor",
                    numOf(1),
                    numOf("2.3"),
                    truthOf(false),
                    atomOf("abc"),
                    structOf("f", varOf("X")),
                    logicListOf(varOf("X"), varOf("Y")),
                    blockOf(truthOf(true), atomOf("!")),
                )
            val actual =
                "functor"(
                    1,
                    2.3,
                    false,
                    "abc",
                    "f"("X"),
                    logicListOf("X", "Y"),
                    blockOf(true, "!"),
                )
            assertEquals(expected, actual)
        }

    private fun MinimalLogicProgrammingScope<*>.createDirectiveUnderTest(): Term =
        directiveOf(structOf("test", varOf("X")))

    @Test
    fun testDirective() =
        logicProgramming {
            val expected = createDirectiveUnderTest()
            val actual = directive { createDirectiveUnderTest() }
            assertTrue(expected.structurallyEquals(actual))
            assertNotEquals(expected, actual)
        }

    private fun MinimalLogicProgrammingScope<*>.createRuleUnderTest(): Term =
        ruleOf(
            structOf("grandparent", varOf("X"), varOf("Z")),
            structOf("parent", varOf("X"), varOf("Y")),
            structOf("parent", varOf("Y"), varOf("Z")),
        )

    @Test
    fun testRule() =
        logicProgramming {
            val expected = createRuleUnderTest()
            val actual = rule { createRuleUnderTest() }
            assertTrue(expected.structurallyEquals(actual))
            assertNotEquals(expected, actual)
        }

    @Test
    fun testRuleAsClause() =
        logicProgramming {
            val expected = createRuleUnderTest()
            val actual = clause { createRuleUnderTest() }
            assertTrue(expected.structurallyEquals(actual))
            assertNotEquals(expected, actual)
        }

    private fun MinimalLogicProgrammingScope<*>.createFactUnderTest(): Term =
        factOf(structOf("parent", varOf("X"), varOf("Y")))

    @Test
    fun testFact() =
        logicProgramming {
            val expected = createFactUnderTest()
            val actual = fact { createFactUnderTest() }
            assertTrue(expected.structurallyEquals(actual))
            assertNotEquals(expected, actual)
        }

    @Test
    fun testFactAsClause() =
        logicProgramming {
            val expected = factOf(structOf("parent", varOf("X"), varOf("Y")))
            val actual =
                clause {
                    "parent"("X", "Y")
                }
            assertTrue(expected.structurallyEquals(actual))
            assertNotEquals(expected, actual)
        }

    @Test
    fun testTailedList() =
        logicProgramming {
            val expected = logicList("a", "b", tail = "c")
            val actual = consOf("a", consOf("b", "c"))
            assertEquals(expected, actual)
        }

    @Test
    fun testNumOfInteger() {
        assertLogicProgrammingExpressionIsCorrect(Integer.of(1)) { numOf(1) }
    }

    @Test
    fun testNumOfNegativeInteger() {
        assertLogicProgrammingExpressionIsCorrect(Integer.of(-3)) { numOf(-3) }
    }

    @Test
    fun testNumOfLong() {
        assertLogicProgrammingExpressionIsCorrect(Integer.of(1)) { numOf(1L) }
    }

    @Test
    fun testNumOfShort() {
        assertLogicProgrammingExpressionIsCorrect(Integer.of(1)) { numOf(1.toShort()) }
    }

    @Test
    fun testNumOfByte() {
        assertLogicProgrammingExpressionIsCorrect(Integer.of(1)) { numOf(1.toByte()) }
    }

    @Test
    fun testNumOfStringInteger() {
        assertLogicProgrammingExpressionIsCorrect(Integer.of(1)) { numOf("1") }
    }

    @Test
    fun testNumOfDouble() {
        assertLogicProgrammingExpressionIsCorrect(Real.of("1.0")) { numOf(1.0) }
    }

    @Test
    fun testNumOfFloat() {
        assertLogicProgrammingExpressionIsCorrect(Real.of("1.0")) { numOf(1.0f) }
    }

    @Test
    fun testNumOfStringDouble() {
        assertLogicProgrammingExpressionIsCorrect(Real.of("1.0")) { numOf("1.0") }
    }

    @Test
    fun testNumOfDoubleValue() {
        assertLogicProgrammingExpressionIsCorrect(THREE_POINT_ONE_DOUBLE) { numOf(3.1) }
    }

    @Test
    fun testNumOfFloatValue() {
        assertLogicProgrammingExpressionIsCorrect(THREE_POINT_ONE_FLOAT) { numOf(3.1f) }
    }

    @Test
    fun testNumOfStringFloatValue() {
        assertLogicProgrammingExpressionIsCorrect(Real.of("3.1")) { numOf("3.1") }
    }

    @Test
    fun testUnderscoreNotEquals() =
        logicProgramming {
            val first = `_`
            val second = `_`
            assertAreDifferentUnderscores(first, second)
        }

    @Test
    fun testVarOfEquals() =
        logicProgramming {
            assertEquals(varOf("X"), varOf("X"))
        }

    @Test
    fun testVarOfUnderscoreEquals() =
        logicProgramming {
            assertEquals(varOf("_"), varOf("_"))
        }

    @Test
    fun testToTermInteger() {
        assertLogicProgrammingExpressionIsCorrect(Integer.of(1)) {
            1.toTerm()
        }
    }

    @Test
    fun testToTermNegativeInteger() {
        assertLogicProgrammingExpressionIsCorrect(MINUS_THREE) {
            (-3).toTerm()
        }
    }

    @Test
    fun testToTermDouble() {
        assertLogicProgrammingExpressionIsCorrect(ONE_POINT_ZERO) {
            1.0.toTerm()
        }
    }

    @Test
    fun testToTermDoubleValue() {
        assertLogicProgrammingExpressionIsCorrect(Real.of("3.1")) {
            3.1.toTerm()
        }
    }

    @Test
    fun testToTermStringAtom() {
        assertLogicProgrammingExpressionIsCorrect(Atom.of("a")) {
            "a".toTerm()
        }
    }

    @Test
    fun testToTermStringVar() =
        logicProgramming {
            val expected = varOf("X")
            val actual = "X".toTerm()
            assertEquals(expected, actual)
        }

    @Test
    fun testToTermAtom() =
        logicProgramming {
            atomOf("a").let {
                assertSame(it, it.toTerm())
            }
        }

    @Test
    fun testToTermList() {
        assertLogicProgrammingExpressionIsCorrect(Cons.singleton(Atom.of("a"))) {
            listOf("a").toTerm()
        }
    }

    @Test
    fun testToTermSet() {
        assertLogicProgrammingExpressionIsCorrect(Block.of(Atom.of("a"))) {
            setOf("a").toTerm()
        }
    }

    @Test
    fun testToTermArray() {
        assertLogicProgrammingExpressionIsCorrect(List.of(Atom.of("a"))) {
            arrayOf("a").toTerm()
        }
    }

    @Test
    fun testToTermSequence() {
        assertLogicProgrammingExpressionIsCorrect(List.of(Atom.of("a"))) {
            sequenceOf("a").toTerm()
        }
    }

    @Test
    fun testToTermBlock() {
        assertLogicProgrammingExpressionIsCorrect(Block.of(Atom.of("a"))) {
            blockOf("a")
        }
    }

    @Test
    fun testToTermTuple() {
        assertLogicProgrammingExpressionIsCorrect(Tuple.of(Atom.of("a"), Atom.of("b"))) {
            tupleOf("a", "b")
        }
    }
}
