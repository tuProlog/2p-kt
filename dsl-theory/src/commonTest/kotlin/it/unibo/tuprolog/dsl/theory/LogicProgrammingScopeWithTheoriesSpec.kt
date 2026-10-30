package it.unibo.tuprolog.dsl.theory

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertIsNot

class LogicProgrammingScopeWithTheoriesSpec :
    FunSpec({
        with(
            LogicProgrammingScopeWithTheoriesTester<LogicProgrammingScopeWithTheories<*>> {
                LogicProgrammingScope
                    .empty()
            },
        ) {
            test("theory-creation") {
                logicProgramming {
                    val theory =
                        theory(
                            { factOf(structOf("f", varOf("X"))) },
                            { ruleOf(structOf("f", varOf("X")), atomOf("a")) },
                            { directiveOf(structOf("f", varOf("X"))) },
                        )
                    assertIs<Theory>(theory)
                    assertIsNot<MutableTheory>(theory)
                    theoryAssertions(theory)
                }
            }

            test("mutable-theory-creation") {
                logicProgramming {
                    val theory =
                        mutableTheory(
                            { factOf(structOf("f", varOf("X"))) },
                            { ruleOf(structOf("f", varOf("X")), atomOf("a")) },
                            { directiveOf(structOf("f", varOf("X"))) },
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
