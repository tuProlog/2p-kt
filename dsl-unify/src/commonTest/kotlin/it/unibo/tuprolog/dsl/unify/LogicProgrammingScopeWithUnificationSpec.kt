@file:Suppress("ktlint:standard:property-naming")

package it.unibo.tuprolog.dsl.unify

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.dsl.LogicProgrammingScopeTester
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogicProgrammingScopeWithUnificationSpec :
    FunSpec({
        with(LogicProgrammingScopeTester<LogicProgrammingScopeWithUnification<*>> { LogicProgrammingScope.empty() }) {
            lateinit var term1: Term // [1, X,    a, Y,   f(A, fail, B, 2.3)]

            lateinit var term2: Term // [A, fail, B, 2.3, f(1, X   , a, Y  )]

            lateinit var unified: Term // [1, fail, a, 2.3, f(1, fail , a, 2.3)]

            lateinit var substitution: Substitution.Unifier

            lateinit var one: Integer

            lateinit var twoPointThree: Real

            lateinit var X: Var

            lateinit var Y: Var

            lateinit var A: Var

            lateinit var B: Var

            lateinit var a: Atom

            lateinit var f1: Struct

            lateinit var f2: Struct

            fun setUp() {
                logicProgramming {
                    one = intOf(1)
                    X = varOf("X")
                    a = atomOf("a")
                    Y = varOf("Y")
                    twoPointThree = realOf("2.3")
                    A = varOf("A")
                    B = varOf("B")
                    f1 = structOf("f", A, fail, B, twoPointThree)
                    f2 = structOf("f", one, X, a, Y)
                    term1 = logicListOf(one, X, a, Y, f1)
                    term2 = logicListOf(A, fail, B, twoPointThree, f2)
                    unified =
                        logicListOf(one, fail, a, twoPointThree, structOf("f", one, fail, a, twoPointThree))
                    substitution =
                        Substitution.unifier(
                            A to one,
                            B to a,
                            X to fail,
                            Y to twoPointThree,
                        )
                }
            }

            beforeTest { setUp() }

            // --8<-- [start:unification-operators]
            test("matches") {
                logicProgramming {
                    assertTrue(term1 matches term2)
                }
            }

            test("match") {
                logicProgramming {
                    assertTrue(match(term1, term2))
                }
            }

            test("unify") {
                logicProgramming {
                    assertEquals(unified, unify(term1, term2))
                }
            }
            // --8<-- [end:unification-operators]

            test("unify-with") {
                logicProgramming {
                    assertEquals(unified, term1 unifyWith term2)
                }
            }

            test("mgu") {
                logicProgramming {
                    assertEquals(substitution, mgu(term1, term2))
                }
            }

            test("mgu-with") {
                logicProgramming {
                    assertEquals(substitution, term1 mguWith term2)
                }
            }
        }
    })
