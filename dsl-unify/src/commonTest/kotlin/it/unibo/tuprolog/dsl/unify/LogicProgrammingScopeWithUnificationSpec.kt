@file:Suppress("ktlint:standard:property-naming")

package it.unibo.tuprolog.dsl.unify

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.dsl.LogicProgrammingScopeTester

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
                    (term1 matches term2).shouldBeTrue()
                }
            }

            test("match") {
                logicProgramming {
                    match(term1, term2).shouldBeTrue()
                }
            }

            test("unify") {
                logicProgramming {
                    unify(term1, term2) shouldBe unified
                }
            }
            // --8<-- [end:unification-operators]

            test("unify-with") {
                logicProgramming {
                    (term1 unifyWith term2) shouldBe unified
                }
            }

            test("mgu") {
                logicProgramming {
                    mgu(term1, term2) shouldBe substitution
                }
            }

            test("mgu-with") {
                logicProgramming {
                    (term1 mguWith term2) shouldBe substitution
                }
            }
        }
    })
