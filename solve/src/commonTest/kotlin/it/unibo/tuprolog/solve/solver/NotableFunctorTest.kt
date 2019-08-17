package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Clause
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


/**
 * Test class for [NotableFunctor]
 *
 * @author Enrico
 */
internal class NotableFunctorTest {

    @Test
    fun notableFunctorsAreTheSameAsClause() {
        assertEquals(Clause.notableFunctors, NotableFunctor.values().map { it.functor }.toList())
    }

    @Test
    fun conjunctionFunctor() {
        assertEquals(",", NotableFunctor.CONJUNCTION.functor)
    }

    @Test
    fun disjunctionFunctor() {
        assertEquals(";", NotableFunctor.DISJUNCTION.functor)
    }

    @Test
    fun implicationFunctor() {
        assertEquals("->", NotableFunctor.IMPLICATION.functor)
    }

    @Test
    fun conjunctionSemantic() {
        with(NotableFunctor.CONJUNCTION) {
            assertTrue { semantic(true, true) }
            assertFalse { semantic(true, false) }
            assertFalse { semantic(false, true) }
            assertFalse { semantic(false, false) }
        }
    }

    @Test
    fun disjunctionSemantic() {
        with(NotableFunctor.DISJUNCTION) {
            assertTrue { semantic(true, true) }
            assertTrue { semantic(true, false) }
            assertTrue { semantic(false, true) }
            assertFalse { semantic(false, false) }
        }
    }

    @Test
    fun implicationSemantic() {
        with(NotableFunctor.IMPLICATION) {
            assertTrue { semantic(true, true) }
            assertFalse { semantic(true, false) }
            assertTrue { semantic(false, true) }
            assertTrue { semantic(false, false) }
        }
    }

}
