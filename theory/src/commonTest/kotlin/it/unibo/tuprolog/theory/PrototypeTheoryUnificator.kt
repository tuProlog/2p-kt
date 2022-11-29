package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.dsl.unify.logicProgramming
import it.unibo.tuprolog.dsl.unify.lp
import it.unibo.tuprolog.theory.impl.AbstractIndexedTheory
import it.unibo.tuprolog.theory.impl.AbstractListedTheory
import it.unibo.tuprolog.unify.Unificator
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.collections.List as KtList

class PrototypeTheoryUnificator(
    private val theoryGenerator: (Unificator, Iterable<Clause>) -> Theory
) {
    private val unificator1 = Unificator.strict()

    private val unificator2 = Unificator.naive()

    private val clauses: KtList<Clause> = logicProgramming {
        ktListOf(
            fact { "f"("a") },
            fact { "f"(intOf(1)) },
            fact { "f"(realOf(1.0)) }
        )
    }

    private lateinit var theory1: Theory
    private lateinit var theory2: Theory

    fun initialize() {
        theory1 = theoryGenerator(unificator1, clauses)
        theory2 = theoryGenerator(unificator2, clauses)
    }

    fun testInizializationSetsUnificator() {
        assertEquals(unificator1, theory1.unificator)
        assertEquals(unificator2, theory2.unificator)
    }

    fun testEqualityAmongDifferentUnificators() {
        assertEquals(theory1, theory2)
        assertEquals(theory1.hashCode(), theory2.hashCode())
    }

    fun testUnificatorAffectsGet() {
        lp {
            assertEquals(clauses, theory1["f"(X)].toList())
            assertEquals(clauses, theory2["f"(X)].toList())
            assertEquals(clauses.subList(1, 2), theory1["f"(1)].toList())
            if (theory2 is AbstractIndexedTheory) {
                assertEquals(clauses.subList(1, 2), theory2["f"(1)].toList())
            } else {
                assertEquals(clauses.subList(1, 3), theory2["f"(1)].toList())
            }
        }
    }

    fun testChangeUnificator() {
        val theory3 = theory1.setUnificator(unificator2)
        if (theory1.let { it.isMutable && it is AbstractListedTheory }) {
            assertSame(theory1, theory3)
        } else {
            assertNotSame(theory1, theory3)
        }
        assertEquals(unificator2, theory3.unificator)
        assertEquals(theory1, theory2)
        assertEquals(theory3, theory2)
    }
}
