package it.unibo.tuprolog.dsl

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Substitution

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
                    val error = shouldThrowAny { ("X" to "a")[1] }
                    error.shouldBeInstanceOf<IllegalArgumentException>()
                    (error.message?.startsWith("Cannot convert") ?: false).shouldBeTrue()
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
                    val error = shouldThrowAny { 1 in ("X" to "a") }
                    error.shouldBeInstanceOf<IllegalArgumentException>()
                    (error.message?.startsWith("Cannot convert") ?: false).shouldBeTrue()
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
