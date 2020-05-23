package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.allChunksOfSize
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsEmpty
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsEmptyAndUnordered
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsNonEmpty
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertItemMultisetsAreEqual
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertNotContainedIn
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertPartialOrderIsTheSame
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertSubMultisetOf
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.clauses
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.factsAndRules
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.factsAndRulesFamilies
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import kotlin.test.Test
import kotlin.test.assertEquals

class UnorderedReteTreeTest {

    companion object {
        private fun reteTreeOf(vararg clauses: Clause): ReteTree =
            ReteTree.unordered(*clauses)

        private fun reteTreeOf(clauses: Iterable<Clause>): ReteTree =
            ReteTree.unordered(clauses)
    }

    @Test
    fun aTreeIsInitiallyEmptyAndUnordered() {
        val tree = reteTreeOf()

        assertIsEmptyAndUnordered(tree)
    }

    @Test
    fun aTreeCanContainAllSortsOfClauses() {
        val tree = reteTreeOf(clauses)

        assertIsNonEmpty(tree)
        assertEquals(clauses.size, tree.size)
    }

    @Test
    fun anUnorderedTreeDoesNotNecessarilyPreservesTheTotalOrderOfClauses1() {
        val tree = reteTreeOf(clauses)

        assertIsNonEmpty(tree)
        assertEquals(clauses.size, tree.size)
        assertItemMultisetsAreEqual(clauses.asSequence(), tree.clauses)
    }

    @Test
    fun anUnorderedTreeDoesNotNecessarilyPreservesTheTotalOrderOfClauses2() {
        val tree = reteTreeOf()

        assertIsEmptyAndUnordered(tree)

        clauses.forEach {
            tree.assertZ(it)
            assertIsNonEmpty(tree)
        }

        assertEquals(clauses.size, tree.size)
        assertItemMultisetsAreEqual(clauses.asSequence(), tree.clauses)
    }

    @Test
    fun anUnorderedTreeDoesNotNecessarilyPreservesTheTotalOrderOfClauses3() {
        val tree = reteTreeOf()

        assertIsEmptyAndUnordered(tree)

        clauses.forEach {
            tree.assertA(it)
            assertIsNonEmpty(tree)
        }

        assertEquals(clauses.size, tree.size)
        assertItemMultisetsAreEqual(clauses.asSequence(), tree.clauses)
    }

    @Test
    fun aClauseCanBeRetractedInSeveralWays1() {
        clauses.forEach { aClause ->

            var tree = reteTreeOf()
            assertIsEmpty(tree)

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractFirst(aClause))
            assertIsEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractFirst(aClause))

            // reset

            tree = reteTreeOf()
            assertIsEmpty(tree)

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractOnly(aClause, 2))
            assertIsEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractOnly(aClause, 2))

            // reset

            tree = reteTreeOf()
            assertIsEmpty(tree)

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractAll(aClause))
            assertIsEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractAll(aClause))
        }
    }

    @Test
    fun aClauseCanBeRetractedInSeveralWays2() {
        clauses.forEach { aClause ->

            var tree = reteTreeOf()
            assertIsEmpty(tree)

            tree.assertZ(aClause)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractFirst(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractFirst(aClause))

            // reset

            tree = reteTreeOf()
            assertIsEmpty(tree)

            tree.assertZ(aClause)
            assertIsNonEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractOnly(aClause, 2))
            assertIsEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractOnly(aClause, 2))

            // reset

            tree = reteTreeOf()
            assertIsEmpty(tree)

            tree.assertZ(aClause)
            assertIsNonEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractAll(aClause))
            assertIsEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractAll(aClause))
        }
    }

    @Test
    fun aClauseCanBeRetractedSeveralTimes1() {
        clauses.forEach { aClause ->

            val tree = reteTreeOf()
            assertIsEmptyAndUnordered(tree)

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractFirst(aClause))
            assertIsEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractFirst(aClause))

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractOnly(aClause, 2))
            assertIsEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractOnly(aClause, 2))

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractAll(aClause))
            assertIsEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractAll(aClause))
        }
    }

    @Test
    fun aClauseCanBeRetractedSeveralTimes2() {
        clauses.forEach { aClause ->

            val tree = reteTreeOf()
            assertIsEmptyAndUnordered(tree)

            tree.assertZ(aClause)
            assertIsNonEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractFirst(aClause))
            assertIsEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractFirst(aClause))

            tree.assertZ(aClause)
            assertIsNonEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractOnly(aClause, 2))
            assertIsEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractOnly(aClause, 2))

            tree.assertZ(aClause)
            assertIsNonEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractAll(aClause))
            assertIsEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractAll(aClause))
        }
    }

    @Test
    fun aTreeIsMutable1() {
        clauses.forEach { aClause ->

            val tree = reteTreeOf()
            assertIsEmptyAndUnordered(tree)

            assertItemMultisetsAreEqual(sequenceOf(), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractFirst(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractAll(aClause))

            tree.assertZ(aClause)
            assertIsNonEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractFirst(aClause))
            assertIsEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractFirst(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractAll(aClause))

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractAll(aClause))
            assertIsEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractFirst(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractAll(aClause))
        }
    }

    @Test
    fun aTreeIsMutable2() {
        clauses.forEach { aClause ->

            val tree = reteTreeOf()
            assertIsEmptyAndUnordered(tree)

            assertItemMultisetsAreEqual(sequenceOf(), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractFirst(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractAll(aClause))

            tree.assertA(aClause)
            assertIsNonEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractFirst(aClause))
            assertIsEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractFirst(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractAll(aClause))

            tree.assertZ(aClause)
            assertIsNonEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractAll(aClause))
            assertIsEmpty(tree)
            assertItemMultisetsAreEqual(sequenceOf(), tree.get(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractFirst(aClause))
            assertItemMultisetsAreEqual(sequenceOf(), tree.retractAll(aClause))
        }
    }

    @Test
    fun aTreeMayContainSeveralCopiesOfAClause1() {
        clauses.forEach { aClause ->
            val tree = reteTreeOf()

            assertIsEmptyAndUnordered(tree)

            tree.assertZ(aClause)
            assertEquals(1, tree.size)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.get(aClause))

            tree.assertZ(aClause)
            assertEquals(2, tree.size)
            assertItemMultisetsAreEqual(sequenceOf(aClause, aClause), tree.get(aClause))
        }
    }

    @Test
    fun aTreeMayContainSeveralCopiesOfAClause2() {
        clauses.forEach { aClause ->
            val tree = reteTreeOf()

            assertIsEmptyAndUnordered(tree)

            tree.assertA(aClause)
            assertEquals(1, tree.size)
            assertItemMultisetsAreEqual(sequenceOf(aClause), tree.get(aClause))

            tree.assertA(aClause)
            assertEquals(2, tree.size)
            assertItemMultisetsAreEqual(sequenceOf(aClause, aClause), tree.get(aClause))
        }
    }

    @Test
    fun anUnorderedTreeDoesNotNecessarilyPreserveTheInsertionOrderOfSimilarClauses1() {
        val tree = reteTreeOf()

        assertIsEmptyAndUnordered(tree)

        for (fact in clauses) {
            tree.assertZ(fact)
        }

        assertEquals(clauses.size, tree.size)

        for ((template, factsAndRules) in factsAndRulesFamilies) {
            assertPartialOrderIsTheSame(factsAndRules.asSequence(), tree.clauses)
            assertItemMultisetsAreEqual(factsAndRules.asSequence(), tree.get(template))
        }
    }

    @Test
    fun anUnorderedTreeDoesNotNecessarilyPreserveTheInsertionOrderOfSimilarClauses2() {
        val allFactsAndRules = factsAndRules
        val tree = reteTreeOf()

        assertIsEmptyAndUnordered(tree)

        for (clause in allFactsAndRules) {
            tree.assertA(clause)
        }

        assertEquals(allFactsAndRules.size, tree.size)

        for ((template, factsAndRules) in factsAndRulesFamilies) {
            assertSubMultisetOf(factsAndRules.asSequence(), tree.clauses)
            assertItemMultisetsAreEqual(factsAndRules.asSequence(), tree.get(template))
        }
    }

    @Test
    fun anUnorderedTreeRetractsClausesInAOrderInsensitiveWay1() {
        val allFactsAndRules = factsAndRules

        for ((template, factsAndRules) in factsAndRulesFamilies) {
            var tree = reteTreeOf(allFactsAndRules)
            assertIsNonEmpty(tree)
            assertSubMultisetOf(factsAndRules.asSequence(), tree.clauses)
            assertItemMultisetsAreEqual(factsAndRules.asSequence(), tree.get(template))

            factsAndRules.distinct().forEachIndexed { i, f ->
                val retracted = tree.retractFirst(template).toList()
                assertSubMultisetOf(retracted, factsAndRules)
                assertEquals(factsAndRules.size - 1 - i, tree.get(template).count())
//                assertNotContainedIn(retracted.asSequence(), tree.get(template))
            }

            // reset

            tree = reteTreeOf(allFactsAndRules)
            assertIsNonEmpty(tree)
            assertSubMultisetOf(factsAndRules.asSequence(), tree.clauses)
            assertItemMultisetsAreEqual(factsAndRules.asSequence(), tree.get(template))

            var retracted2 = tree.retractOnly(template, 2).toList()
            assertSubMultisetOf(retracted2, factsAndRules)
//            assertNotContainedIn(retracted2.asSequence(), tree.get(template))
            assertEquals(factsAndRules.size - 2, tree.get(template).count())

            retracted2 = tree.retractOnly(template, 2).toList()
            assertSubMultisetOf(retracted2, factsAndRules)
//            assertNotContainedIn(retracted2.asSequence(), tree.get(template))
            assertEquals(factsAndRules.size - 4, tree.get(template).count())

            // reset

            tree = reteTreeOf(allFactsAndRules)
            assertIsNonEmpty(tree)
            assertSubMultisetOf(factsAndRules.asSequence(), tree.clauses)
            assertItemMultisetsAreEqual(factsAndRules.asSequence(), tree.get(template))

            assertItemMultisetsAreEqual(factsAndRules.asSequence(), tree.retractAll(template))
            assertItemMultisetsAreEqual(emptySequence(), tree.get(template))
        }
    }

    @Test
    fun aTreeCanAlwaysRetrieveTheClausesItContains() {
        val tree = reteTreeOf(clauses)

        assertIsNonEmpty(tree)
        assertEquals(clauses.size, tree.size)
        assertItemMultisetsAreEqual(clauses.asSequence(), tree.clauses)

        for (clause in clauses) {
            assertSubMultisetOf(sequenceOf(clause), tree.get(clause))
        }
    }

    @Test
    fun anUnorderedTreeRetractsClausesInAOrderInsensitiveWay2() {
        val tree = reteTreeOf(factsAndRules)

        assertIsNonEmpty(tree)
        assertEquals(factsAndRules.size, tree.size)

        for ((template, factsAndRules) in factsAndRulesFamilies) {
            assertSubMultisetOf(factsAndRules.asSequence(), tree.clauses)
            assertItemMultisetsAreEqual(factsAndRules.asSequence(), tree.get(template))

            val firstRetracted = tree.retractFirst(template).toList()
            assertSubMultisetOf(firstRetracted, factsAndRules)
            assertNotContainedIn(firstRetracted.asSequence(), tree.get(template))

            tree.assertA(firstRetracted[0])
            assertSubMultisetOf(firstRetracted.asSequence(), tree.get(template))

            val twoRetracted = tree.retractOnly(template, 2).toList()
            assertSubMultisetOf(twoRetracted, factsAndRules)
            assertNotContainedIn(twoRetracted.asSequence(), tree.get(template)) {
                """
                    Item `$it` is still present in ${tree.get(template).toList()}=tree.get(`$template`), 
                """.trimIndent()
            }

            twoRetracted.forEach { tree.assertA(it) }
            assertSubMultisetOf(twoRetracted.asSequence(), tree.get(template))

            val allRetracted = tree.retractAll(template).toList()
            assertItemMultisetsAreEqual(allRetracted, factsAndRules)
            assertNotContainedIn(allRetracted.asSequence(), tree.get(template))
            assertItemMultisetsAreEqual(emptySequence(), tree.get(template))
        }
    }

    @Test
    fun anUnorderedTreeFiltersClausesByBodyAsWell() {
        val groundTerms = clauses.asSequence()
            .filterIsInstance<Rule>()
            .flatMap { sequenceOf(it.head) + it.head.argsSequence }
            .filter { it.isGround }
            .toSet()
        val families = clauses.filterIsInstance<Rule>().map { it.head.functor to it.head.arity }.toSet()

        for ((functor, arity) in families) {
            for (args in groundTerms.allChunksOfSize(arity)) {

                val head = Struct.of(functor, args)
                val template = Rule.of(head, Var.anonymous())

                val clauses = groundTerms.map { Rule.of(head, it) }

                val tree = reteTreeOf(clauses)
                assertIsNonEmpty(tree)

                assertItemMultisetsAreEqual(clauses.asSequence(), tree.get(template))

                for (i in clauses.indices) {
                    assertItemMultisetsAreEqual(sequenceOf(clauses[i]), tree.get(clauses[i]))
                }

                for (element in clauses) {
                    assertItemMultisetsAreEqual(sequenceOf(element), tree.retractFirst(template))
                }

                assertIsEmpty(tree)

                for (i in clauses.size - 1 downTo 0) {
                    tree.assertA(clauses[i])
                    assertIsNonEmpty(tree)

                    assertItemMultisetsAreEqual(clauses.subList(i, clauses.size).asSequence(), tree.get(template))
                }

                for (element in clauses) {
                    assertItemMultisetsAreEqual(sequenceOf(element), tree.retractOnly(template, 1))
                }

                assertIsEmpty(tree)

                for (element in clauses) {
                    tree.assertZ(element)
                }

                assertItemMultisetsAreEqual(clauses.asSequence(), tree.retractAll(template))
            }
        }
    }
}