package it.unibo.tuprolog.dsl.theory

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldNotBeInstanceOf
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory

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
                    theory.shouldBeInstanceOf<Theory>()
                    theory.shouldNotBeInstanceOf<MutableTheory>()
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
                    theory.shouldBeInstanceOf<Theory>()
                    theory.shouldBeInstanceOf<MutableTheory>()
                    theoryAssertions(theory)
                    theory.assertZ(ruleOf(structOf("f", varOf("X")), atomOf("b")))
                    theory.size shouldBe 4
                }
            }
        }
    })
