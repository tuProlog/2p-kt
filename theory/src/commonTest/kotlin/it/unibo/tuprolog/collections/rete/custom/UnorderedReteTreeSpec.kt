package it.unibo.tuprolog.collections.rete.custom

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.allChunksOfSize
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsEmpty
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsEmptyAndUnordered
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertIsNonEmpty
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertItemMultisetsAreEqual
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertNotContainedIn
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertPartialOrderIsTheSame
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.assertSubMultisetOf
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.defaultClauses
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.factsAndRules
import it.unibo.tuprolog.collections.rete.custom.ReteTreeAssertionUtils.factsAndRulesFamilies
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UnorderedReteTreeSpec :
    FunSpec({
        test("a-tree-is-initially-empty-and-unordered") {
            val tree = reteTreeOf()

            assertIsEmptyAndUnordered(tree)
        }

        test("a-tree-can-contain-all-sorts-of-clauses") {
            val tree = reteTreeOf(defaultClauses)

            assertIsNonEmpty(tree)
            assertEquals(defaultClauses.size, tree.size)
        }

        test("an-unordered-tree-does-not-necessarily-preserves-the-total-order-of-clauses1") {
            val tree = reteTreeOf(defaultClauses)

            assertIsNonEmpty(tree)
            assertEquals(defaultClauses.size, tree.size)
            assertItemMultisetsAreEqual(defaultClauses.asSequence(), tree.clauses)
        }

        test("an-unordered-tree-does-not-necessarily-preserves-the-total-order-of-clauses2") {
            val tree = reteTreeOf()

            assertIsEmptyAndUnordered(tree)

            defaultClauses.forEach {
                tree.assertZ(it)
                assertIsNonEmpty(tree)
            }

            assertEquals(defaultClauses.size, tree.size)
            assertItemMultisetsAreEqual(defaultClauses.asSequence(), tree.clauses)
        }

        test("an-unordered-tree-does-not-necessarily-preserves-the-total-order-of-clauses3") {
            val tree = reteTreeOf()

            assertIsEmptyAndUnordered(tree)

            defaultClauses.forEach {
                tree.assertZ(it)
                assertIsNonEmpty(tree)
            }

            assertEquals(defaultClauses.size, tree.size)
            assertItemMultisetsAreEqual(defaultClauses.asSequence(), tree.clauses)
        }

        test("a-clause-can-be-retracted-in-several-ways1") {
            defaultClauses.forEach { aClause ->

                var tree = reteTreeOf()
                assertIsEmpty(tree)

                tree.assertZ(aClause)
                assertIsNonEmpty(tree)
                assertItemMultisetsAreEqual(sequenceOf(aClause), tree.retractFirst(aClause))
                assertIsEmpty(tree)
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

        test("a-clause-can-be-retracted-in-several-ways2") {
            defaultClauses.forEach { aClause ->

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

        test("a-clause-can-be-retracted-several-times1") {
            defaultClauses.forEach { aClause ->

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

        test("a-clause-can-be-retracted-several-times2") {
            defaultClauses.forEach { aClause ->

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

        test("a-tree-is-mutable1") {
            defaultClauses.forEach { aClause ->

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

        test("a-tree-is-mutable2") {
            defaultClauses.forEach { aClause ->

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

        test("a-tree-may-contain-several-copies-of-a-clause1") {
            defaultClauses.forEach { aClause ->
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

        test("a-tree-may-contain-several-copies-of-a-clause2") {
            defaultClauses.forEach { aClause ->
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

        withTests(
            nameFn = {
                "an-unordered-tree-does-not-necessarily-preserve-the-insertion-order-of-similar-clauses1/${it.id}"
            },
            factsAndRulesFamilies,
        ) { (_, template, family) ->
            val tree = reteTreeOf()

            assertIsEmptyAndUnordered(tree)

            for (fact in defaultClauses) {
                tree.assertZ(fact)
            }

            assertEquals(defaultClauses.size, tree.size)

            assertPartialOrderIsTheSame(family.asSequence(), tree.clauses)
            assertItemMultisetsAreEqual(family.asSequence(), tree.get(template))
        }
        withTests(
            nameFn = {
                "an-unordered-tree-does-not-necessarily-preserve-the-insertion-order-of-similar-clauses2/${it.id}"
            },
            factsAndRulesFamilies,
        ) { (_, template, family) ->
            val tree = reteTreeOf()

            assertIsEmptyAndUnordered(tree)

            for (clause in factsAndRules) {
                tree.assertZ(clause)
            }

            assertEquals(factsAndRules.size, tree.size)

            assertSubMultisetOf(family.asSequence(), tree.clauses)
            assertItemMultisetsAreEqual(family.asSequence(), tree.get(template))
        }
        withTests(
            nameFn = { "an-unordered-tree-retracts-clauses-in-a-order-insensitive-way1/${it.id}" },
            factsAndRulesFamilies,
        ) { (_, template, family) ->
            var tree = reteTreeOf(factsAndRules)
            assertIsNonEmpty(tree)
            assertSubMultisetOf(family.asSequence(), tree.clauses)
            assertItemMultisetsAreEqual(family.asSequence(), tree.get(template))

            family.distinct().forEachIndexed { i, _ ->
                val retracted = tree.retractFirst(template).toList()
                assertSubMultisetOf(retracted, family)
                assertEquals(family.size - 1 - i, tree.get(template).count())
            }

            // reset

            tree = reteTreeOf(factsAndRules)
            assertIsNonEmpty(tree)
            assertSubMultisetOf(family.asSequence(), tree.clauses)
            assertItemMultisetsAreEqual(family.asSequence(), tree.get(template))

            var retracted2 = tree.retractOnly(template, 2).toList()
            assertSubMultisetOf(retracted2, family)
            assertEquals(family.size - 2, tree.get(template).count())

            retracted2 = tree.retractOnly(template, 2).toList()
            assertSubMultisetOf(retracted2, family)
            assertEquals(family.size - 4, tree.get(template).count())

            // reset

            tree = reteTreeOf(factsAndRules)
            assertIsNonEmpty(tree)
            assertSubMultisetOf(family.asSequence(), tree.clauses)
            assertItemMultisetsAreEqual(family.asSequence(), tree.get(template))

            assertItemMultisetsAreEqual(family.asSequence(), tree.retractAll(template))
            assertItemMultisetsAreEqual(emptySequence(), tree.get(template))
        }
        test("a-tree-can-always-retrieve-the-clauses-it-contains") {
            val tree = reteTreeOf(defaultClauses)

            assertIsNonEmpty(tree)
            assertEquals(defaultClauses.size, tree.size)
            assertItemMultisetsAreEqual(defaultClauses.asSequence(), tree.clauses)

            for (clause in defaultClauses) {
                assertSubMultisetOf(sequenceOf(clause), tree.get(clause))
            }
        }

        withTests(
            nameFn = { "an-unordered-tree-retracts-clauses-in-a-order-insensitive-way2/${it.id}" },
            factsAndRulesFamilies,
        ) { (_, template, family) ->
            val tree = reteTreeOf(factsAndRules)

            assertIsNonEmpty(tree)
            assertEquals(factsAndRules.size, tree.size)

            assertSubMultisetOf(family.asSequence(), tree.clauses)
            assertItemMultisetsAreEqual(family.asSequence(), tree.get(template))

            val firstRetracted = tree.retractFirst(template).toList()
            assertSubMultisetOf(firstRetracted, family)
            assertNotContainedIn(firstRetracted.asSequence(), tree.get(template))

            tree.assertZ(firstRetracted[0])
            assertSubMultisetOf(firstRetracted.asSequence(), tree.get(template))

            val numberOfClausesBeforeRetracting = tree.get(template).toList().count()
            val twoRetracted = tree.retractOnly(template, 2).toList()
            assertSubMultisetOf(twoRetracted, family)
            assertTrue((numberOfClausesBeforeRetracting - tree.get(template).count()) in (0..2))

            twoRetracted.forEach { tree.assertZ(it) }
            assertSubMultisetOf(twoRetracted.asSequence(), tree.get(template))

            val allRetracted = tree.retractAll(template).toList()
            assertItemMultisetsAreEqual(allRetracted, family)
            assertNotContainedIn(allRetracted.asSequence(), tree.get(template))
            assertItemMultisetsAreEqual(emptySequence(), tree.get(template))
        }
        test("an-unordered-tree-filters-clauses-by-body-as-well") {
            val groundTerms =
                defaultClauses
                    .asSequence()
                    .filterIsInstance<Rule>()
                    .flatMap { sequenceOf(it.head) + it.head.argsSequence }
                    .filter { it.isGround }
                    .toSet()
            val families = defaultClauses.filterIsInstance<Rule>().map { it.head.functor to it.head.arity }.toSet()

            for ((functor, arity) in families) {
                for (args in groundTerms.allChunksOfSize(arity)) {
                    val head = Struct.of(functor, args)
                    val template = Rule.of(head, Var.anonymous())

                    val clauses = (0..4).map { Rule.of(Struct.of(functor, args), Integer.of(it)) }

                    val tree = reteTreeOf(clauses)
                    assertIsNonEmpty(tree)
                    assertItemMultisetsAreEqual(clauses.asSequence(), tree.get(template))
                    assertItemMultisetsAreEqual(clauses.asSequence(), tree.clauses)

                    for (c in clauses) {
                        assertItemMultisetsAreEqual(sequenceOf(c), tree.get(c))
                    }

                    for (c in clauses) {
                        assertItemMultisetsAreEqual(sequenceOf(c), tree.retractFirst(template))
                    }

                    assertIsEmpty(tree)
                    assertItemMultisetsAreEqual(emptySequence(), tree.get(template))
                    assertItemMultisetsAreEqual(emptySequence(), tree.clauses)

                    for (i in clauses.size - 1 downTo 0) {
                        tree.assertZ(clauses[i])
                        assertIsNonEmpty(tree)

                        assertItemMultisetsAreEqual(clauses.subList(i, clauses.size).asSequence(), tree.get(template))
                    }

                    assertIsNonEmpty(tree)
                    assertItemMultisetsAreEqual(clauses.asSequence(), tree.get(template))
                    assertItemMultisetsAreEqual(clauses.asSequence(), tree.clauses)

                    for (i in clauses.indices) {
                        tree.retractOnly(template, 1)
                        assertEquals(clauses.size - i - 1, tree.size)
                    }

                    assertIsEmpty(tree)
                    assertItemMultisetsAreEqual(emptySequence(), tree.get(template))
                    assertItemMultisetsAreEqual(emptySequence(), tree.clauses)

                    for (element in clauses) {
                        tree.assertZ(element)
                    }

                    assertIsNonEmpty(tree)
                    assertItemMultisetsAreEqual(clauses.asSequence(), tree.get(template))
                    assertItemMultisetsAreEqual(clauses.asSequence(), tree.clauses)

                    assertItemMultisetsAreEqual(clauses.asSequence(), tree.retractAll(template))
                    assertIsEmpty(tree)
                    assertItemMultisetsAreEqual(emptySequence(), tree.get(template))
                    assertItemMultisetsAreEqual(emptySequence(), tree.clauses)
                }
            }
        }

        test("non-ground-clauses-are-matched-by-ground-queries") {
            val groundTerms =
                defaultClauses
                    .asSequence()
                    .filterIsInstance<Rule>()
                    .flatMap { sequenceOf(it.head) + it.head.argsSequence }
                    .filter { it.isGround }
                    .toSet()
            val families =
                defaultClauses
                    .filterIsInstance<Rule>()
                    .map { it.head.functor to it.head.arity }
                    .filter { (_, arity) -> arity > 0 }
                    .toSet()

            for ((functor, arity) in families) {
                for (args in groundTerms.allChunksOfSize(arity)) {
                    for (i in 0 until arity) {
                        val clauseArgs = args.toMutableList()
                        clauseArgs[i] = Var.anonymous()

                        val query = Rule.of(Struct.of(functor, args), Var.anonymous())

                        val clauses = (0..4).map { Rule.of(Struct.of(functor, clauseArgs), Integer.of(it)) }

                        val tree = reteTreeOf(clauses)

                        assertIsNonEmpty(tree)
                        assertItemMultisetsAreEqual(clauses.asSequence(), tree.clauses)
                        assertItemMultisetsAreEqual(clauses.asSequence(), tree.get(query))

                        for (j in clauses.indices) {
                            assertSubMultisetOf(tree.retractFirst(query), clauses.asSequence())

                            assertEquals(clauses.size - j - 1, tree.size)
                        }

                        assertIsEmpty(tree)
                        assertItemMultisetsAreEqual(emptySequence(), tree.get(query))
                        assertItemMultisetsAreEqual(emptySequence(), tree.clauses)

                        for (j in clauses.size - 1 downTo 0) {
                            tree.assertZ(clauses[j])
                            assertIsNonEmpty(tree)

                            assertItemMultisetsAreEqual(clauses.subList(j, clauses.size).asSequence(), tree.get(query))
                        }

                        assertIsNonEmpty(tree)
                        assertItemMultisetsAreEqual(clauses.asSequence(), tree.clauses)
                        assertItemMultisetsAreEqual(clauses.asSequence(), tree.get(query))

                        for (j in clauses.indices) {
                            assertSubMultisetOf(tree.retractOnly(query, 1), clauses.asSequence())

                            assertEquals(clauses.size - j - 1, tree.size)
                        }

                        assertIsEmpty(tree)
                        assertItemMultisetsAreEqual(emptySequence(), tree.get(query))
                        assertItemMultisetsAreEqual(emptySequence(), tree.clauses)

                        for (c in clauses) {
                            tree.assertZ(c)
                        }

                        assertIsNonEmpty(tree)
                        assertItemMultisetsAreEqual(clauses.asSequence(), tree.clauses)
                        assertItemMultisetsAreEqual(clauses.asSequence(), tree.get(query))

                        assertItemMultisetsAreEqual(clauses.asSequence(), tree.retractAll(query))
                        assertIsEmpty(tree)
                        assertItemMultisetsAreEqual(emptySequence(), tree.get(query))
                        assertItemMultisetsAreEqual(emptySequence(), tree.clauses)
                    }
                }
            }
        }
    })

private fun reteTreeOf(vararg clauses: Clause): ReteTree = ReteTree.unordered(Unificator.default, *clauses)

private fun reteTreeOf(clauses: Iterable<Clause>): ReteTree = ReteTree.unordered(Unificator.default, clauses)
