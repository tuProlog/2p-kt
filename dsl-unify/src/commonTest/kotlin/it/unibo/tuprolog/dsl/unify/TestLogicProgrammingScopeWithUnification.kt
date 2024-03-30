package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.dsl.AbstractLogicProgrammingScopeTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("ktlint:standard:property-naming")
class TestLogicProgrammingScopeWithUnification :
    AbstractLogicProgrammingScopeTest<LogicProgrammingScopeWithUnification<*>>() {
    override fun createLogicProgrammingScope(): LogicProgrammingScopeWithUnification<*> = LogicProgrammingScope.empty()

    private lateinit var term1: Term // [1, X,    a, Y,   f(A, fail, B, 2.3)]
    private lateinit var term2: Term // [A, fail, B, 2.3, f(1, X   , a, Y  )]
    private lateinit var unified: Term // [1, fail, a, 2.3, f(1, fail , a, 2.3)]
    private lateinit var substitution: Substitution.Unifier
    private lateinit var one: Integer
    private lateinit var twoPointThree: Real
    private lateinit var X: Var
    private lateinit var Y: Var
    private lateinit var A: Var
    private lateinit var B: Var
    private lateinit var a: Atom
    private lateinit var f1: Struct
    private lateinit var f2: Struct

    @BeforeTest
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

    @Test
    fun testMatches() =
        logicProgramming {
            assertTrue(term1 matches term2)
        }

    @Test
    fun testMatch() =
        logicProgramming {
            assertTrue(match(term1, term2))
        }

    @Test
    fun testUnify() =
        logicProgramming {
            assertEquals(unified, unify(term1, term2))
        }

    @Test
    fun testUnifyWith() =
        logicProgramming {
            assertEquals(unified, term1 unifyWith term2)
        }

    @Test
    fun testMgu() =
        logicProgramming {
            assertEquals(substitution, mgu(term1, term2))
        }

    @Test
    fun testMguWith() =
        logicProgramming {
            assertEquals(substitution, term1 mguWith term2)
        }
}
