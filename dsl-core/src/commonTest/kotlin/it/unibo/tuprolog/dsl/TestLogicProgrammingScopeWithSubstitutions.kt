package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Substitution
import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertIs
import kotlin.test.assertTrue

class TestLogicProgrammingScopeWithSubstitutions :
    AbstractLogicProgrammingScopeTest<LogicProgrammingScopeWithSubstitutions<*>>() {
    override fun createLogicProgrammingScope(): LogicProgrammingScopeWithSubstitutions<*> =
        LogicProgrammingScope.empty()

    @Test
    fun testVarToSubstitution() =
        logicProgramming {
            assertExpressionIsCorrect(Substitution.of(varOf("X"), atomOf("a"))) {
                varOf("X") to "a"
            }
        }

    @Test
    fun testStringToSubstitution() =
        logicProgramming {
            assertExpressionIsCorrect(Substitution.of(varOf("X"), atomOf("a"))) {
                "X" to "a"
            }
        }

    @Test
    fun testGetVariableValueInSubstitution() =
        logicProgramming {
            assertExpressionIsCorrect(atomOf("a")) {
                ("X" to "a")["X"]
            }
        }

    @Test
    fun testGetWrongValueInSubstitution() =
        logicProgramming {
            val error = assertFails { ("X" to "a")[1] }
            assertIs<IllegalArgumentException>(error)
            assertTrue(error.message?.startsWith("Cannot convert") ?: false)
        }

    @Test
    fun testSubstitutionContains() =
        logicProgramming {
            assertExpressionIsCorrect(true) {
                "X" in ("X" to "a")
            }
        }

    @Test
    fun testSubstitutionContainsFail() =
        logicProgramming {
            val error = assertFails { 1 in ("X" to "a") }
            assertIs<IllegalArgumentException>(error)
            assertTrue(error.message?.startsWith("Cannot convert") ?: false)
        }

    @Test
    fun testSubstitutionContainsValue() =
        logicProgramming {
            assertExpressionIsCorrect(true) {
                ("X" to "a").containsValue("a")
            }
        }
}
