package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsEmpty
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsEmptyAndOrdered
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsNonEmpty
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
import it.unibo.tuprolog.core.Fact
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

        assertIsNonEmpty(tree)
        assertEquals(clauses.size, tree.size)
    }

    @Test
    fun anOrderedTreePreservesTheTotalOrderOfClauses1() {
        val tree = reteTreeOf(clauses)

        assertIsNonEmpty(tree)
        assertEquals(clauses.size, tree.size)
        assertItemsAreEquals(clauses.asSequence(), tree.clauses)
    }

    @Test
    fun anOrderedTreePreservesTheTotalOrderOfClauses2() {
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)

        clauses.forEach {
            tree.assertZ(it)
            assertIsNonEmpty(tree)
        }

        assertEquals(clauses.size, tree.size)
        assertItemsAreEquals(clauses.asSequence(), tree.clauses)
    }

    @Test
    fun anOrderedTreePreservesTheTotalOrderOfClauses3() {
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)

        clauses.forEach {
            tree.assertA(it)
            assertIsNonEmpty(tree)
        }

        assertEquals(clauses.size, tree.size)
        assertItemsAreEquals(clauses.asReversed().asSequence(), tree.clauses)
    }

    @Test
    fun aClauseCanBeRetractedInSeveralWays1() {
        clauses.forEach { aClause ->

            var tree = reteTreeOf()
            assertIsEmpty(tree)

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemsAreEquals(sequenceOf(aClause), tree.retractFirst(aClause))
            assertIsEmpty(tree)
            assertItemsAreEquals(sequenceOf(), tree.retractFirst(aClause))

            // reset

            tree = reteTreeOf()
            assertIsEmpty(tree)

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemsAreEquals(sequenceOf(aClause), tree.retractOnly(aClause, 2))
            assertIsEmpty(tree)
            assertItemsAreEquals(sequenceOf(), tree.retractOnly(aClause, 2))

            // reset

            tree = reteTreeOf()
            assertIsEmpty(tree)

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemsAreEquals(sequenceOf(aClause), tree.retractAll(aClause))
            assertIsEmpty(tree)
            assertItemsAreEquals(sequenceOf(), tree.retractAll(aClause))
        }
    }

    @Test
    fun aClauseCanBeRetractedInSeveralWays2() {
        clauses.forEach { aClause ->

            var tree = reteTreeOf()
            assertIsEmpty(tree)

            tree.assertZ(aClause)
            assertItemsAreEquals(sequenceOf(aClause), tree.retractFirst(aClause))
            assertItemsAreEquals(sequenceOf(), tree.retractFirst(aClause))

            // reset

            tree = reteTreeOf()
            assertIsEmpty(tree)

            tree.assertZ(aClause)
            assertIsNonEmpty(tree)
            assertItemsAreEquals(sequenceOf(aClause), tree.retractOnly(aClause, 2))
            assertIsEmpty(tree)
            assertItemsAreEquals(sequenceOf(), tree.retractOnly(aClause, 2))

            // reset

            tree = reteTreeOf()
            assertIsEmpty(tree)

            tree.assertZ(aClause)
            assertIsNonEmpty(tree)
            assertItemsAreEquals(sequenceOf(aClause), tree.retractAll(aClause))
            assertIsEmpty(tree)
            assertItemsAreEquals(sequenceOf(), tree.retractAll(aClause))
        }
    }

    @Test
    fun aClauseCanBeRetractedInSeveralTimes1() {
        clauses.forEach { aClause ->

            val tree = reteTreeOf()
            assertIsEmptyAndOrdered(tree)

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemsAreEquals(sequenceOf(aClause), tree.retractFirst(aClause))
            assertIsEmpty(tree)
            assertItemsAreEquals(sequenceOf(), tree.retractFirst(aClause))

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemsAreEquals(sequenceOf(aClause), tree.retractOnly(aClause, 2))
            assertIsEmpty(tree)
            assertItemsAreEquals(sequenceOf(), tree.retractOnly(aClause, 2))

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemsAreEquals(sequenceOf(aClause), tree.retractAll(aClause))
            assertIsEmpty(tree)
            assertItemsAreEquals(sequenceOf(), tree.retractAll(aClause))
        }
    }

    @Test
    fun aClauseCanBeRetractedInSeveralTimes2() {
        clauses.forEach { aClause ->

            val tree = reteTreeOf()
            assertIsEmptyAndOrdered(tree)

            tree.assertZ(aClause)
            assertIsNonEmpty(tree)
            assertItemsAreEquals(sequenceOf(aClause), tree.retractFirst(aClause))
            assertIsEmpty(tree)
            assertItemsAreEquals(sequenceOf(), tree.retractFirst(aClause))

            tree.assertZ(aClause)
            assertIsNonEmpty(tree)
            assertItemsAreEquals(sequenceOf(aClause), tree.retractOnly(aClause, 2))
            assertIsEmpty(tree)
            assertItemsAreEquals(sequenceOf(), tree.retractOnly(aClause, 2))

<<<<<<< HEAD
            val getResult = tree.get(aClause)
            assertItemsAreEquals(sequenceOf(aClause), getResult)
=======
            tree.assertZ(aClause)
            assertIsNonEmpty(tree)
            assertItemsAreEquals(sequenceOf(aClause), tree.retractAll(aClause))
            assertIsEmpty(tree)
            assertItemsAreEquals(sequenceOf(), tree.retractAll(aClause))
        }
    }

    @Test
    fun aTreeIsMutable1() {
        clauses.forEach { aClause ->

            val tree = reteTreeOf()
            assertIsEmptyAndOrdered(tree)

            assertItemsAreEquals(sequenceOf(), tree.get(aClause))
            assertItemsAreEquals(sequenceOf(), tree.retractFirst(aClause))
            assertItemsAreEquals(sequenceOf(), tree.retractAll(aClause))
>>>>>>> e30e1258cecb7c3fe508ab5cb67dc0751402df11

            tree.assertZ(aClause)
            assertIsNonEmpty(tree)
            assertItemsAreEquals(sequenceOf(aClause), tree.get(aClause))
            assertItemsAreEquals(sequenceOf(aClause), tree.get(aClause))
            assertItemsAreEquals(sequenceOf(aClause), tree.retractFirst(aClause))
            assertIsEmpty(tree)
            assertItemsAreEquals(sequenceOf(), tree.get(aClause))
            assertItemsAreEquals(sequenceOf(), tree.retractFirst(aClause))
            assertItemsAreEquals(sequenceOf(), tree.retractAll(aClause))

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemsAreEquals(sequenceOf(aClause), tree.get(aClause))
            assertItemsAreEquals(sequenceOf(aClause), tree.get(aClause))
            assertItemsAreEquals(sequenceOf(aClause), tree.retractAll(aClause))
            assertIsEmpty(tree)
            assertItemsAreEquals(sequenceOf(), tree.get(aClause))
            assertItemsAreEquals(sequenceOf(), tree.retractFirst(aClause))
            assertItemsAreEquals(sequenceOf(), tree.retractAll(aClause))
        }
    }

    @Test
    fun aTreeIsMutable2() {
        clauses.forEach { aClause ->

            val tree = reteTreeOf()
            assertIsEmptyAndOrdered(tree)

            assertItemsAreEquals(sequenceOf(), tree.get(aClause))
            assertItemsAreEquals(sequenceOf(), tree.retractFirst(aClause))
            assertItemsAreEquals(sequenceOf(), tree.retractAll(aClause))

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemsAreEquals(sequenceOf(aClause), tree.get(aClause))
            assertItemsAreEquals(sequenceOf(aClause), tree.get(aClause))
            assertItemsAreEquals(sequenceOf(aClause), tree.retractFirst(aClause))
            assertIsEmpty(tree)
            assertItemsAreEquals(sequenceOf(), tree.get(aClause))
            assertItemsAreEquals(sequenceOf(), tree.retractFirst(aClause))
            assertItemsAreEquals(sequenceOf(), tree.retractAll(aClause))

            tree.assertZ(aClause)
            assertIsNonEmpty(tree)
            assertItemsAreEquals(sequenceOf(aClause), tree.get(aClause))
            assertItemsAreEquals(sequenceOf(aClause), tree.get(aClause))
            assertItemsAreEquals(sequenceOf(aClause), tree.retractAll(aClause))
            assertIsEmpty(tree)
            assertItemsAreEquals(sequenceOf(), tree.get(aClause))
            assertItemsAreEquals(sequenceOf(), tree.retractFirst(aClause))
            assertItemsAreEquals(sequenceOf(), tree.retractAll(aClause))
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
        assertItemsAreEquals(f1Facts.asSequence(), tree.get(Fact.template("f", 1)))
        assertPartialOrderIsTheSame(g1Facts.asSequence(), tree.clauses)
        assertItemsAreEquals(g1Facts.asSequence(), tree.get(Fact.template("g", 1)))
        assertPartialOrderIsTheSame(h1Facts.asSequence(), tree.clauses)
        assertItemsAreEquals(h1Facts.asSequence(), tree.get(Fact.template("h", 1)))
        assertPartialOrderIsTheSame(i1Facts.asSequence(), tree.clauses)
        assertItemsAreEquals(i1Facts.asSequence(), tree.get(Fact.template("i", 1)))
        assertPartialOrderIsTheSame(j1Facts.asSequence(), tree.clauses)
        assertItemsAreEquals(j1Facts.asSequence(), tree.get(Fact.template("j", 1)))
        assertPartialOrderIsTheSame(l1Facts.asSequence(), tree.clauses)
        assertItemsAreEquals(l1Facts.asSequence(), tree.get(Fact.template("l", 1)))
        assertPartialOrderIsTheSame(m1Facts.asSequence(), tree.clauses)
        assertItemsAreEquals(m1Facts.asSequence(), tree.get(Fact.template("m", 1)))
        assertPartialOrderIsTheSame(n1Facts.asSequence(), tree.clauses)
        assertItemsAreEquals(n1Facts.asSequence(), tree.get(Fact.template("n", 1)))
        assertPartialOrderIsTheSame(o1Facts.asSequence(), tree.clauses)
        assertItemsAreEquals(o1Facts.asSequence(), tree.get(Fact.template("o", 1)))
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
        assertItemsAreEquals(f1Facts.asReversed().asSequence(), tree.get(Fact.template("f", 1)))
        assertPartialOrderIsTheSame(g1Facts.asReversed().asSequence(), tree.clauses)
        assertItemsAreEquals(g1Facts.asReversed().asSequence(), tree.get(Fact.template("g", 1)))
        assertPartialOrderIsTheSame(h1Facts.asReversed().asSequence(), tree.clauses)
        assertItemsAreEquals(h1Facts.asReversed().asSequence(), tree.get(Fact.template("h", 1)))
        assertPartialOrderIsTheSame(i1Facts.asReversed().asSequence(), tree.clauses)
        assertItemsAreEquals(i1Facts.asReversed().asSequence(), tree.get(Fact.template("i", 1)))
        assertPartialOrderIsTheSame(j1Facts.asReversed().asSequence(), tree.clauses)
        assertItemsAreEquals(j1Facts.asReversed().asSequence(), tree.get(Fact.template("j", 1)))
        assertPartialOrderIsTheSame(l1Facts.asReversed().asSequence(), tree.clauses)
        assertItemsAreEquals(l1Facts.asReversed().asSequence(), tree.get(Fact.template("l", 1)))
        assertPartialOrderIsTheSame(m1Facts.asReversed().asSequence(), tree.clauses)
        assertItemsAreEquals(m1Facts.asReversed().asSequence(), tree.get(Fact.template("m", 1)))
        assertPartialOrderIsTheSame(n1Facts.asReversed().asSequence(), tree.clauses)
        assertItemsAreEquals(n1Facts.asReversed().asSequence(), tree.get(Fact.template("n", 1)))
        assertPartialOrderIsTheSame(o1Facts.asReversed().asSequence(), tree.clauses)
        assertItemsAreEquals(o1Facts.asReversed().asSequence(), tree.get(Fact.template("o", 1)))
    }
}