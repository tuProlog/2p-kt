package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsEmpty
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsEmptyAndOrdered
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsNonEmpty
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertItemsAreEquals
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertPartialOrderIsTheSame
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.clauses
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.factFamilies
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.facts
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
    fun aClauseCanBeRetractedSeveralTimes1() {
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
    fun aClauseCanBeRetractedSeveralTimes2() {
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

        for ((template, facts) in factFamilies) {
            assertPartialOrderIsTheSame(facts.asSequence(), tree.clauses)
            assertItemsAreEquals(facts.asSequence(), tree.get(template))
        }
    }

    @Test
    fun anOrderedTreePreservesTheInsertionOrderOfSimilarClauses2() {
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)

        for (fact in facts) {
            tree.assertA(fact)
        }

        assertEquals(facts.size, tree.size)

        for ((template, facts) in factFamilies) {
            assertPartialOrderIsTheSame(facts.asReversed().asSequence(), tree.clauses)
            assertItemsAreEquals(facts.asReversed().asSequence(), tree.get(template))
        }
    }

    @Test
    fun anOrderedTreeRetractsClausesInAOrderSensitiveWay1() {
        for ((template, facts) in factFamilies) {
            var tree = reteTreeOf(ReteTreeAssertionUtils.facts)
            assertIsNonEmpty(tree)
            assertPartialOrderIsTheSame(facts.asSequence(), tree.clauses)
            assertItemsAreEquals(facts.asSequence(), tree.get(template))

            facts.forEachIndexed { i, f ->
                val a = tree.retractFirst(template)
                println(a.first())
                assertItemsAreEquals(sequenceOf(f), a)
                assertItemsAreEquals(facts.asSequence().drop(i + 1), tree.get(template))
            }

            // reset

            tree = reteTreeOf(ReteTreeAssertionUtils.facts)
            assertIsNonEmpty(tree)
            assertPartialOrderIsTheSame(facts.asSequence(), tree.clauses)
            assertItemsAreEquals(facts.asSequence(), tree.get(template))

            assertItemsAreEquals(facts.subList(0, 2).asSequence(), tree.retractOnly(template, 2))
            assertItemsAreEquals(facts.asSequence().drop(2), tree.get(template))
            assertItemsAreEquals(facts.subList(2, 4).asSequence(), tree.retractOnly(template, 2))
            assertItemsAreEquals(facts.asSequence().drop(4), tree.get(template))

            // reset

            tree = reteTreeOf(ReteTreeAssertionUtils.facts)
            assertIsNonEmpty(tree)
            assertPartialOrderIsTheSame(facts.asSequence(), tree.clauses)
            assertItemsAreEquals(facts.asSequence(), tree.get(template))

            assertItemsAreEquals(facts.asSequence(), tree.retractAll(template))
            assertItemsAreEquals(emptySequence(), tree.get(template))
        }
    }

    @Test
    fun anOrderedTreeRetractsClausesInAOrderSensitiveWay2() {
        val tree = reteTreeOf(facts)

        assertIsNonEmpty(tree)
        assertEquals(facts.size, tree.size)

        for ((template, facts) in factFamilies) {
            assertPartialOrderIsTheSame(facts.asSequence(), tree.clauses)
            assertItemsAreEquals(facts.asSequence(), tree.get(template))

            assertItemsAreEquals(sequenceOf(facts[0]), tree.retractFirst(template))
            assertItemsAreEquals(facts.asSequence().drop(1), tree.get(template))

            tree.assertA(facts[0])
            assertItemsAreEquals(facts.asSequence(), tree.get(template))

            assertItemsAreEquals(facts.subList(0, 2).asSequence(), tree.retractOnly(template, 2))
            assertItemsAreEquals(facts.asSequence().drop(2), tree.get(template))

            tree.assertA(facts[1])
            tree.assertA(facts[0])
            assertItemsAreEquals(facts.asSequence(), tree.get(template))

            assertItemsAreEquals(facts.asSequence(), tree.retractAll(template))
            assertItemsAreEquals(emptySequence(), tree.get(template))
        }
    }
}