package it.unibo.tuprolog.dsl.theory

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertIsNot

class LogicProgrammingScopeSpec :
    FunSpec({
        with(LogicProgrammingScopeWithTheoriesTester<LogicProgrammingScope> { LogicProgrammingScope.empty() }) {
            test("theory-creation") {
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
            }

            test("mutable-theory-creation") {
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
        }
    })
