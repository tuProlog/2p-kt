package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertIsNot

class TestLogicProgrammingScope : AbstractLogicProgrammingScopeTestWithTheories<LogicProgrammingScope>() {
    override fun createLogicProgrammingScope(): LogicProgrammingScope = LogicProgrammingScope.empty()

    @Test
    fun testTheoryCreation() =
        logicProgramming {
            val theory =
                theoryOf(
                    fact { "f"("X") },
                    rule { "f"("X") `if` "a" },
                    directive { "f"("X") },
                )
            assertIs<Theory>(theory)
            assertIsNot<MutableTheory>(theory)
            theoryAssertions(theory)
        }

    @Test
    fun testMutableTheoryCreation() =
        logicProgramming {
            val theory =
                mutableTheoryOf(
                    fact { "f"("X") },
                    rule { "f"("X") `if` "a" },
                    directive { "f"("X") },
                )
            assertIs<Theory>(theory)
            assertIs<MutableTheory>(theory)
            theoryAssertions(theory)
            theory.assertZ(ruleOf(structOf("f", varOf("X")), atomOf("b")))
            assertEquals(4, theory.size)
        }
}
