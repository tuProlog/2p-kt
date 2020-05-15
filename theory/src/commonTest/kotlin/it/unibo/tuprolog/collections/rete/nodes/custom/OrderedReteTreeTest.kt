package it.unibo.tuprolog.collections.rete.nodes.custom

import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.collections.rete.nodes.custom.ReteTreeAssertionUtils.assertIsEmptyAndOrdered
import it.unibo.tuprolog.collections.rete.nodes.custom.ReteTreeAssertionUtils.assertItemsAreEquals
import it.unibo.tuprolog.collections.rete.nodes.custom.ReteTreeAssertionUtils.assertPartialOrderIsTheSame
import it.unibo.tuprolog.collections.rete.nodes.custom.ReteTreeAssertionUtils.dot2FunctorRules
import it.unibo.tuprolog.collections.rete.nodes.custom.ReteTreeAssertionUtils.facts
import it.unibo.tuprolog.collections.rete.nodes.custom.ReteTreeAssertionUtils.other1FunctorRules
import it.unibo.tuprolog.collections.rete.nodes.custom.ReteTreeAssertionUtils.rules
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedReteTreeTest {

    companion object {
        private fun reteTreeOf(vararg clauses: Clause): ReteTree =
            ReteTree.ordered(*clauses)
    }

    @Test
    fun aTreeIsInitiallyEmptyAndOrdered() {
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)
    }

    @Test
    fun aTreeIsMutable() {
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)

        val aFact = facts.random()

        tree.assertZ(aFact)
        assertEquals(1, tree.size)
        assertItemsAreEquals(sequenceOf(aFact), tree.get(aFact))
    }

    @Test
    fun aTreeMayContainSeveralCopiesOfAClause() {
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)

        val aFact = facts.random()

        tree.assertZ(aFact)
        assertEquals(1, tree.size)
        assertItemsAreEquals(sequenceOf(aFact), tree.get(aFact))

        tree.assertZ(aFact)
        assertEquals(2, tree.size)
        assertItemsAreEquals(sequenceOf(aFact, aFact), tree.get(aFact))
    }

    @Test
    fun anOrderedTreePreservesTheInsertionOrderOfSimilarClauses1() {
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)

        for (rule in rules) {
            tree.assertZ(rule)
        }

        assertEquals(rules.size, tree.size)
        assertPartialOrderIsTheSame(other1FunctorRules.asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(dot2FunctorRules.asSequence(), tree.clauses)
    }

    @Test
    fun anOrderedTreePreservesTheInsertionOrderOfSimilarClauses2() {
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)

        for (rule in rules) {
            tree.assertA(rule)
        }

        assertEquals(rules.size, tree.size)
        assertPartialOrderIsTheSame(other1FunctorRules.asReversed().asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(dot2FunctorRules.asReversed().asSequence(), tree.clauses)
    }

    @Test
    fun anOrderedTreePreservesTheInsertionOrderOfSimilarClauses3() {
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)

        val seq1 =

        tree.assertA(Fact.of(Struct.of("f", Integer.of(2))))
        tree.assertZ(Fact.of(Struct.of("2", Integer.of(2))))

        assertEquals(rules.size, tree.size)
        assertPartialOrderIsTheSame(other1FunctorRules.asReversed().asSequence(), tree.clauses)
        assertPartialOrderIsTheSame(dot2FunctorRules.asReversed().asSequence(), tree.clauses)
    }
}