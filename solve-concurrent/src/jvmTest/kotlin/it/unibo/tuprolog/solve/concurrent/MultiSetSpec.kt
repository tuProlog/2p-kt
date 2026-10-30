@file:OptIn(ExperimentalCoroutinesApi::class)

package it.unibo.tuprolog.solve.concurrent

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.dsl.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.assertNotEquals

class MultiSetSpec :
    FunSpec({
        test("empty-multi-set") {
            multiRunConcurrentTest {
                val multiSet1 = MultiSet()
                val multiSet2 = MultiSet()
                multiSet1.assertingEquals(multiSet2)
                val empty = MultiSet(sequenceOf())
                multiSet1.assertingEquals(empty)
            }
        }

        test("multi-set-creation-and-equality") {
            multiRunConcurrentTest {
                logicProgramming {
                    val query = "X" equalsTo 1 and `var`("X")
                    val solutions = sequenceOf(query.no())
                    val multiSet1 = MultiSet(solutions)
                    val multiSet2 = MultiSet(solutions)
                    multiSet1.assertingEquals(multiSet2)

                    val query2 = `var`("X") and ("X" equalsTo 1)
                    val solutions2 =
                        sequenceOf(
                            query2.yes("X" to 1),
                            query2.yes("X" to 1),
                            query.no(),
                            query2.yes("X" to 1),
                        )
                    MultiSet(solutions2).assertingEquals(MultiSet(solutions2))

                    val solutionHalt =
                        sequenceOf(
                            query.halt(
                                ExistenceError.forProcedure(
                                    DummyInstances.executionContext,
                                    Signature("nofoo", 1),
                                ),
                            ),
                        )
                    MultiSet(solutionHalt).assertingEquals(MultiSet(solutionHalt))
                }
            }
        }

        test("different-multi-set") {
            multiRunConcurrentTest {
                logicProgramming {
                    val query = "X" equalsTo 1 and `var`("X")
                    val query2 = `var`("X") and ("X" equalsTo 1)
                    val solutions = sequenceOf(query.no())
                    val solutions2 = sequenceOf(query2.yes("X" to 1))
                    val solutions3 = sequenceOf(query2.no())
                    val multiSet1 = MultiSet(solutions)
                    val multiSet2 = MultiSet(solutions2)
                    val multiSet3 = MultiSet(solutions3)
                    assertNotEquals(multiSet1, multiSet2)
                    assertNotEquals(multiSet1, multiSet3)
                    assertNotEquals(multiSet2, multiSet3)
                }
            }
        }
    })
