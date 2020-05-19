package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsEmptyAndOrdered
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertItemsAreEquals
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertPartialOrderIsTheSame
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.clauses
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.f1Facts
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.facts
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.g1Facts
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.h1Facts
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.i1Facts
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.j1Facts
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.l1Facts
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.m1Facts
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.n1Facts
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.o1Facts
import it.unibo.tuprolog.core.Clause
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedReteTreeTest {

    companion object {
        private fun reteTreeOf(vararg clauses: Clause): ReteTree =
            ReteTree.ordered(*clauses)

        private fun reteTreeOf(clauses: Iterable<Clause>): ReteTree =
            ReteTree.ordered(clauses)
    }

    @Test
    fun aTreeIsInitiallyEmptyAndOrdered() {
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)
    }

    @Test
    fun aTreeCanContainAllSortsOfClauses() {
        val tree = reteTreeOf(clauses)

        assertEquals(clauses.size, tree.size)
    }

    @Test
    fun anOrderedTreePreservesTheTotalOrderOfClauses() {
        val tree = reteTreeOf(clauses)

        assertEquals(clauses.size, tree.size)
        assertItemsAreEquals(clauses.asSequence(), tree.clauses)
    }

    @Test
    fun aTreeIsMutable() {
        clauses.forEach { aClause ->
            val tree = reteTreeOf()

            assertIsEmptyAndOrdered(tree)

            tree.assertZ(aClause)
            assertEquals(1, tree.size)
            assertItemsAreEquals(sequenceOf(aClause), tree.get(aClause))
        }

    }

    @Test
    fun aTreeMayContainSeveralCopiesOfAClause1() {
        clauses.forEach { aClause ->
            val tree = reteTreeOf()

            assertIsEmptyAndOrdered(tree)

            tree.assertZ(aClause)
            assertEquals(1, tree.size)
            assertItemsAreEquals(sequenceOf(aClause), tree.get(aClause))

            tree.assertZ(aClause)
            assertEquals(2, tree.size)
            assertItemsAreEquals(sequenceOf(aClause, aClause), tree.get(aClause))
        }
    }

    @Test
    fun aTreeMayContainSeveralCopiesOfAClause2() {
        clauses.forEach { aClause ->
            val tree = reteTreeOf()

            assertIsEmptyAndOrdered(tree)

            tree.assertA(aClause)
            assertEquals(1, tree.size)
            assertItemsAreEquals(sequenceOf(aClause), tree.get(aClause))

            tree.assertA(aClause)
            assertEquals(2, tree.size)
            assertItemsAreEquals(sequenceOf(aClause, aClause), tree.get(aClause))
        }
    }

    @Test
    fun anOrderedTreePreservesTheInsertionOrderOfSimilarClauses1() {
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)

        for (fact in facts) {
            tree.assertZ(fact)
        }

        assertEquals(facts.size, tree.size)
        assertPartialOrderIsTheSame(f1Facts.asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(g1Facts.asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(h1Facts.asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(i1Facts.asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(j1Facts.asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(l1Facts.asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(m1Facts.asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(n1Facts.asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(o1Facts.asSequence(), tree.clauses)
    }

    @Test
    fun anOrderedTreePreservesTheInsertionOrderOfSimilarClauses2() {
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)

        for (fact in facts) {
            tree.assertA(fact)
        }

        assertEquals(facts.size, tree.size)
        assertPartialOrderIsTheSame(f1Facts.asReversed().asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(g1Facts.asReversed().asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(h1Facts.asReversed().asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(i1Facts.asReversed().asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(j1Facts.asReversed().asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(l1Facts.asReversed().asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(m1Facts.asReversed().asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(n1Facts.asReversed().asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(o1Facts.asReversed().asSequence(), tree.clauses)
    }
}