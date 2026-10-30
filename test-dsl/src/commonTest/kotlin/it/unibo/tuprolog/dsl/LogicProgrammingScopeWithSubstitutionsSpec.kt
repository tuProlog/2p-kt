package it.unibo.tuprolog.dsl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Substitution
import kotlin.test.assertFails
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LogicProgrammingScopeWithSubstitutionsSpec :
    FunSpec({
        with(LogicProgrammingScopeTester<LogicProgrammingScopeWithSubstitutions<*>> { LogicProgrammingScope.empty() }) {
            test("var-to-substitution") {
                logicProgramming {
                    assertExpressionIsCorrect(Substitution.of(varOf("X"), atomOf("a"))) {
                        varOf("X") to "a"
                    }
                }
            }

            test("string-to-substitution") {
                logicProgramming {
                    assertExpressionIsCorrect(Substitution.of(varOf("X"), atomOf("a"))) {
                        "X" to "a"
                    }
                }
            }

            test("get-variable-value-in-substitution") {
                logicProgramming {
                    assertExpressionIsCorrect(atomOf("a")) {
                        ("X" to "a")["X"]
                    }
                }
            }

            test("get-wrong-value-in-substitution") {
                logicProgramming {
                    val error = assertFails { ("X" to "a")[1] }
                    assertIs<IllegalArgumentException>(error)
                    assertTrue(error.message?.startsWith("Cannot convert") ?: false)
                }
            }

            test("substitution-contains") {
                logicProgramming {
                    assertExpressionIsCorrect(true) {
                        "X" in ("X" to "a")
                    }
                }
            }

            test("substitution-contains-fail") {
                logicProgramming {
                    val error = assertFails { 1 in ("X" to "a") }
                    assertIs<IllegalArgumentException>(error)
                    assertTrue(error.message?.startsWith("Cannot convert") ?: false)
                }
            }

            test("substitution-contains-value") {
                logicProgramming {
                    assertExpressionIsCorrect(true) {
                        ("X" to "a").containsValue("a")
                    }
                }
            }
        }
    })
