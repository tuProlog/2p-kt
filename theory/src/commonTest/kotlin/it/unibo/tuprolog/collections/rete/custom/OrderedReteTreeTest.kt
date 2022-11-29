package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.allChunksOfSize
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsEmpty
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsEmptyAndOrdered
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsNonEmpty
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertItemsAreEquals
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertPartialOrderIsTheSame
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.defaultClauses
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.factsAndRules
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.factsAndRulesFamilies
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedReteTreeTest {

    companion object {
        private fun reteTreeOf(vararg clauses: Clause): ReteTree =
            ReteTree.ordered(Unificator.default, *clauses)

        private fun reteTreeOf(clauses: Iterable<Clause>): ReteTree =
            ReteTree.ordered(Unificator.default, clauses)
    }

    @Test
    fun aTreeIsInitiallyEmptyAndOrdered() {
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)
    }

    @Test
    fun aTreeCanContainAllSortsOfClauses() {
        val tree = reteTreeOf(defaultClauses)

        assertIsNonEmpty(tree)
        assertEquals(defaultClauses.size, tree.size)
    }

    @Test
    fun anOrderedTreePreservesTheTotalOrderOfClauses1() {
        val tree = reteTreeOf(defaultClauses)

        assertIsNonEmpty(tree)
        assertEquals(defaultClauses.size, tree.size)
        assertItemsAreEquals(defaultClauses.asSequence(), tree.clauses)
    }

    @Test
    fun anOrderedTreePreservesTheTotalOrderOfClauses2() {
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)

        defaultClauses.forEach {
            tree.assertZ(it)
            assertIsNonEmpty(tree)
        }

        assertEquals(defaultClauses.size, tree.size)
        assertItemsAreEquals(defaultClauses.asSequence(), tree.clauses)
    }

    @Test
    fun anOrderedTreePreservesTheTotalOrderOfClauses3() {
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)

        defaultClauses.forEach {
            tree.assertA(it)
            assertIsNonEmpty(tree)
        }

        assertEquals(defaultClauses.size, tree.size)
        assertItemsAreEquals(defaultClauses.asReversed().asSequence(), tree.clauses)
    }

    @Test
    fun aClauseCanBeRetractedInSeveralWays1() {
        defaultClauses.forEach { aClause ->

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
        defaultClauses.forEach { aClause ->

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
        defaultClauses.forEach { aClause ->

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
        defaultClauses.forEach { aClause ->

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
        defaultClauses.forEach { aClause ->

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
        defaultClauses.forEach { aClause ->

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
        defaultClauses.forEach { aClause ->
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
        defaultClauses.forEach { aClause ->
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

        for (fact in defaultClauses) {
            tree.assertZ(fact)
        }

        assertEquals(defaultClauses.size, tree.size)

        for ((template, factsAndRules) in factsAndRulesFamilies) {
            assertPartialOrderIsTheSame(factsAndRules.asSequence(), tree.clauses)
            assertItemsAreEquals(factsAndRules.asSequence(), tree.get(template))
        }
    }

    @Test
    fun anOrderedTreePreservesTheInsertionOrderOfSimilarClauses2() {
        val allFactsAndRules = factsAndRules
        val tree = reteTreeOf()

        assertIsEmptyAndOrdered(tree)

        for (clause in allFactsAndRules) {
            tree.assertA(clause)
        }

        assertEquals(allFactsAndRules.size, tree.size)

        for ((template, factsAndRules) in factsAndRulesFamilies) {
            assertPartialOrderIsTheSame(factsAndRules.asReversed().asSequence(), tree.clauses)
            assertItemsAreEquals(factsAndRules.asReversed().asSequence(), tree.get(template))
        }
    }

    @Test
    fun anOrderedTreeRetractsClausesInAOrderSensitiveWay1() {
        val allFactsAndRules = factsAndRules

        for ((template, factsAndRules) in factsAndRulesFamilies) {
            var tree = reteTreeOf(allFactsAndRules)
            assertIsNonEmpty(tree)
            assertPartialOrderIsTheSame(factsAndRules.asSequence(), tree.clauses)
            assertItemsAreEquals(factsAndRules.asSequence(), tree.get(template))

            factsAndRules.forEachIndexed { i, f ->
                val a = tree.retractFirst(template)
                assertItemsAreEquals(sequenceOf(f), a)
                assertItemsAreEquals(factsAndRules.asSequence().drop(i + 1), tree.get(template))
            }

            // reset

            tree = reteTreeOf(allFactsAndRules)
            assertIsNonEmpty(tree)
            assertPartialOrderIsTheSame(factsAndRules.asSequence(), tree.clauses)
            assertItemsAreEquals(factsAndRules.asSequence(), tree.get(template))

            assertItemsAreEquals(factsAndRules.subList(0, 2).asSequence(), tree.retractOnly(template, 2))
            assertItemsAreEquals(factsAndRules.asSequence().drop(2), tree.get(template))
            assertItemsAreEquals(factsAndRules.subList(2, 4).asSequence(), tree.retractOnly(template, 2))
            assertItemsAreEquals(factsAndRules.asSequence().drop(4), tree.get(template))

            // reset

            tree = reteTreeOf(allFactsAndRules)
            assertIsNonEmpty(tree)
            assertPartialOrderIsTheSame(factsAndRules.asSequence(), tree.clauses)
            assertItemsAreEquals(factsAndRules.asSequence(), tree.get(template))

            assertItemsAreEquals(factsAndRules.asSequence(), tree.retractAll(template))
            assertItemsAreEquals(emptySequence(), tree.get(template))
        }
    }

    @Test
    fun aTreeCanAlwaysRetrieveTheClausesItContains() {
        val tree = reteTreeOf(defaultClauses)

        assertIsNonEmpty(tree)
        assertEquals(defaultClauses.size, tree.size)
        assertItemsAreEquals(defaultClauses.asSequence(), tree.clauses)

        for (clause in defaultClauses) {
            assertPartialOrderIsTheSame(sequenceOf(clause), tree.get(clause))
        }
    }

    @Test
    fun anOrderedTreeRetractsClausesInAOrderSensitiveWay2() {
        val tree = reteTreeOf(factsAndRules)

        assertIsNonEmpty(tree)
        assertEquals(factsAndRules.size, tree.size)

        for ((template, factsAndRules) in factsAndRulesFamilies) {
            assertPartialOrderIsTheSame(factsAndRules.asSequence(), tree.clauses)
            assertItemsAreEquals(factsAndRules.asSequence(), tree.get(template))

            assertItemsAreEquals(sequenceOf(factsAndRules[0]), tree.retractFirst(template))
            assertItemsAreEquals(factsAndRules.asSequence().drop(1), tree.get(template))

            tree.assertA(factsAndRules[0])
            assertItemsAreEquals(factsAndRules.asSequence(), tree.get(template))

            assertItemsAreEquals(factsAndRules.subList(0, 2).asSequence(), tree.retractOnly(template, 2))
            assertItemsAreEquals(factsAndRules.asSequence().drop(2), tree.get(template))

            tree.assertA(factsAndRules[1])
            tree.assertA(factsAndRules[0])
            assertItemsAreEquals(factsAndRules.asSequence(), tree.get(template))

            assertItemsAreEquals(factsAndRules.asSequence(), tree.retractAll(template))
            assertItemsAreEquals(emptySequence(), tree.get(template))
        }
    }

    @Test
    fun anOrderedTreeFiltersClausesByBodyAsWell() {
        val groundTerms = defaultClauses.asSequence()
            .filterIsInstance<Rule>()
            .flatMap { sequenceOf(it.head) + it.head.argsSequence }
            .filter { it.isGround }
            .toSet()
        val families = defaultClauses.filterIsInstance<Rule>().map { it.head.functor to it.head.arity }.toSet()
        for ((functor, arity) in families) {
            for (args in groundTerms.allChunksOfSize(arity)) {
                val head = Struct.of(functor, args)
                val template = Rule.of(head, Var.anonymous())

                val clauses = (0..4).map { Rule.of(head, Integer.of(it)) }

                val tree = reteTreeOf(clauses)
                assertIsNonEmpty(tree)

                assertItemsAreEquals(clauses.asSequence(), tree.clauses)
                assertItemsAreEquals(clauses.asSequence(), tree.get(template))

                for (c in clauses) {
                    assertItemsAreEquals(sequenceOf(c), tree.get(c))
                }

                for (element in clauses) {
                    assertItemsAreEquals(sequenceOf(element), tree.retractFirst(template))
                }

                assertIsEmpty(tree)
                assertItemsAreEquals(emptySequence(), tree.get(template))
                assertItemsAreEquals(emptySequence(), tree.clauses)

                for (i in clauses.size - 1 downTo 0) {
                    tree.assertA(clauses[i])
                    assertIsNonEmpty(tree)

                    assertItemsAreEquals(clauses.subList(i, clauses.size).asSequence(), tree.get(template))
                }

                assertIsNonEmpty(tree)
                assertItemsAreEquals(clauses.asSequence(), tree.get(template))
                assertItemsAreEquals(clauses.asSequence(), tree.clauses)

                for (element in clauses) {
                    assertItemsAreEquals(sequenceOf(element), tree.retractOnly(template, 1))
                }

                assertIsEmpty(tree)
                assertItemsAreEquals(emptySequence(), tree.get(template))
                assertItemsAreEquals(emptySequence(), tree.clauses)

                for (element in clauses) {
                    tree.assertZ(element)
                }

                assertIsNonEmpty(tree)
                assertItemsAreEquals(clauses.asSequence(), tree.get(template))
                assertItemsAreEquals(clauses.asSequence(), tree.clauses)

                assertItemsAreEquals(clauses.asSequence(), tree.retractAll(template))
                assertIsEmpty(tree)
                assertItemsAreEquals(emptySequence(), tree.get(template))
                assertItemsAreEquals(emptySequence(), tree.clauses)
            }
        }
    }

    @Test
    fun nonGroundClausesAreMatchedByGroundQueries() {
        val groundTerms = defaultClauses.asSequence()
            .filterIsInstance<Rule>()
            .flatMap { sequenceOf(it.head) + it.head.argsSequence }
            .filter { it.isGround }
            .toSet()
        val families = defaultClauses.filterIsInstance<Rule>()
            .map { it.head.functor to it.head.arity }
            .filter { (_, arity) -> arity > 0 }
            .toSet()

        for ((functor, arity) in families) {
            for (args in groundTerms.allChunksOfSize(arity)) {
                for (i in 0 until arity) {
                    val clauseArgs = args.toMutableList()
                    clauseArgs[i] = Var.anonymous()

                    val query = Rule.of(Struct.of(functor, args), Var.anonymous())

//                    println("handle query: $query")

                    val clauses = (0..4).map { Rule.of(Struct.of(functor, clauseArgs), Integer.of(it)) }

                    val tree = reteTreeOf(clauses)

                    assertIsNonEmpty(tree)
                    assertItemsAreEquals(clauses.asSequence(), tree.clauses)
                    assertItemsAreEquals(clauses.asSequence(), tree.get(query))

                    for (element in clauses) {
                        assertItemsAreEquals(sequenceOf(element), tree.retractFirst(query))
                    }

                    assertIsEmpty(tree)
                    assertItemsAreEquals(emptySequence(), tree.get(query))
                    assertItemsAreEquals(emptySequence(), tree.clauses)

                    for (j in clauses.size - 1 downTo 0) {
                        tree.assertA(clauses[j])
                        assertIsNonEmpty(tree)

                        assertItemsAreEquals(clauses.subList(j, clauses.size).asSequence(), tree.get(query))
                    }

                    assertIsNonEmpty(tree)
                    assertItemsAreEquals(clauses.asSequence(), tree.clauses)
                    assertItemsAreEquals(clauses.asSequence(), tree.get(query))

                    for (element in clauses) {
                        assertItemsAreEquals(sequenceOf(element), tree.retractOnly(query, 1))
                    }

                    assertIsEmpty(tree)
                    assertItemsAreEquals(emptySequence(), tree.get(query))
                    assertItemsAreEquals(emptySequence(), tree.clauses)

                    for (element in clauses) {
                        tree.assertZ(element)
                    }

                    assertIsNonEmpty(tree)
                    assertItemsAreEquals(clauses.asSequence(), tree.clauses)
                    assertItemsAreEquals(clauses.asSequence(), tree.get(query))

                    assertItemsAreEquals(clauses.asSequence(), tree.retractAll(query))
                    assertIsEmpty(tree)
                    assertItemsAreEquals(emptySequence(), tree.get(query))
                    assertItemsAreEquals(emptySequence(), tree.clauses)
                }
            }
        }
    }
}
