package it.unibo.tuprolog.collections.rete.custom

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
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
import kotlin.test.assertEquals

class OrderedReteTreeSpec :
    FunSpec({
        test("a-tree-is-initially-empty-and-ordered") {
            val tree = reteTreeOf()

            assertIsEmptyAndOrdered(tree)
        }

        test("a-tree-can-contain-all-sorts-of-clauses") {
            val tree = reteTreeOf(defaultClauses)

            assertIsNonEmpty(tree)
            assertEquals(defaultClauses.size, tree.size)
        }

        test("an-ordered-tree-preserves-the-total-order-of-clauses1") {
            val tree = reteTreeOf(defaultClauses)

            assertIsNonEmpty(tree)
            assertEquals(defaultClauses.size, tree.size)
            assertItemsAreEquals(defaultClauses.asSequence(), tree.clauses)
        }

        test("an-ordered-tree-preserves-the-total-order-of-clauses2") {
            val tree = reteTreeOf()

            assertIsEmptyAndOrdered(tree)

            defaultClauses.forEach {
                tree.assertZ(it)
                assertIsNonEmpty(tree)
            }

            assertEquals(defaultClauses.size, tree.size)
            assertItemsAreEquals(defaultClauses.asSequence(), tree.clauses)
        }

        test("an-ordered-tree-preserves-the-total-order-of-clauses3") {
            val tree = reteTreeOf()

            assertIsEmptyAndOrdered(tree)

            defaultClauses.forEach {
                tree.assertA(it)
                assertIsNonEmpty(tree)
            }

            assertEquals(defaultClauses.size, tree.size)
            assertItemsAreEquals(defaultClauses.asReversed().asSequence(), tree.clauses)
        }

        test("a-clause-can-be-retracted-in-several-ways1") {
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

        test("a-clause-can-be-retracted-in-several-ways2") {
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

        test("a-clause-can-be-retracted-several-times1") {
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

        test("a-clause-can-be-retracted-several-times2") {
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

        test("a-tree-is-mutable1") {
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

        test("a-tree-is-mutable2") {
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

        test("a-tree-may-contain-several-copies-of-a-clause1") {
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

        test("a-tree-may-contain-several-copies-of-a-clause2") {
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

        withTests(
            nameFn = { "an-ordered-tree-preserves-the-insertion-order-of-similar-clauses1/${it.id}" },
            factsAndRulesFamilies,
        ) { (_, template, family) ->
            val tree = reteTreeOf()

            assertIsEmptyAndOrdered(tree)

            for (fact in defaultClauses) {
                tree.assertZ(fact)
            }

            assertEquals(defaultClauses.size, tree.size)

            assertPartialOrderIsTheSame(family.asSequence(), tree.clauses)
            assertItemsAreEquals(family.asSequence(), tree.get(template))
        }
        withTests(
            nameFn = { "an-ordered-tree-preserves-the-insertion-order-of-similar-clauses2/${it.id}" },
            factsAndRulesFamilies,
        ) { (_, template, family) ->
            val tree = reteTreeOf()

            assertIsEmptyAndOrdered(tree)

            for (clause in factsAndRules) {
                tree.assertA(clause)
            }

            assertEquals(factsAndRules.size, tree.size)

            assertPartialOrderIsTheSame(family.asReversed().asSequence(), tree.clauses)
            assertItemsAreEquals(family.asReversed().asSequence(), tree.get(template))
        }
        withTests(
            nameFn = { "an-ordered-tree-retracts-clauses-in-a-order-sensitive-way1/${it.id}" },
            factsAndRulesFamilies,
        ) { (_, template, family) ->
            var tree = reteTreeOf(factsAndRules)
            assertIsNonEmpty(tree)
            assertPartialOrderIsTheSame(family.asSequence(), tree.clauses)
            assertItemsAreEquals(family.asSequence(), tree.get(template))

            family.forEachIndexed { i, f ->
                val a = tree.retractFirst(template)
                assertItemsAreEquals(sequenceOf(f), a)
                assertItemsAreEquals(family.asSequence().drop(i + 1), tree.get(template))
            }

            // reset

            tree = reteTreeOf(factsAndRules)
            assertIsNonEmpty(tree)
            assertPartialOrderIsTheSame(family.asSequence(), tree.clauses)
            assertItemsAreEquals(family.asSequence(), tree.get(template))

            assertItemsAreEquals(family.subList(0, 2).asSequence(), tree.retractOnly(template, 2))
            assertItemsAreEquals(family.asSequence().drop(2), tree.get(template))
            assertItemsAreEquals(family.subList(2, 4).asSequence(), tree.retractOnly(template, 2))
            assertItemsAreEquals(family.asSequence().drop(4), tree.get(template))

            // reset

            tree = reteTreeOf(factsAndRules)
            assertIsNonEmpty(tree)
            assertPartialOrderIsTheSame(family.asSequence(), tree.clauses)
            assertItemsAreEquals(family.asSequence(), tree.get(template))

            assertItemsAreEquals(family.asSequence(), tree.retractAll(template))
            assertItemsAreEquals(emptySequence(), tree.get(template))
        }
        test("a-tree-can-always-retrieve-the-clauses-it-contains") {
            val tree = reteTreeOf(defaultClauses)

            assertIsNonEmpty(tree)
            assertEquals(defaultClauses.size, tree.size)
            assertItemsAreEquals(defaultClauses.asSequence(), tree.clauses)

            for (clause in defaultClauses) {
                assertPartialOrderIsTheSame(sequenceOf(clause), tree.get(clause))
            }
        }

        withTests(
            nameFn = { "an-ordered-tree-retracts-clauses-in-a-order-sensitive-way2/${it.id}" },
            factsAndRulesFamilies,
        ) { (_, template, family) ->
            val tree = reteTreeOf(factsAndRules)

            assertIsNonEmpty(tree)
            assertEquals(factsAndRules.size, tree.size)

            assertPartialOrderIsTheSame(family.asSequence(), tree.clauses)
            assertItemsAreEquals(family.asSequence(), tree.get(template))

            assertItemsAreEquals(sequenceOf(family[0]), tree.retractFirst(template))
            assertItemsAreEquals(family.asSequence().drop(1), tree.get(template))

            tree.assertA(family[0])
            assertItemsAreEquals(family.asSequence(), tree.get(template))

            assertItemsAreEquals(family.subList(0, 2).asSequence(), tree.retractOnly(template, 2))
            assertItemsAreEquals(family.asSequence().drop(2), tree.get(template))

            tree.assertA(family[1])
            tree.assertA(family[0])
            assertItemsAreEquals(family.asSequence(), tree.get(template))

            assertItemsAreEquals(family.asSequence(), tree.retractAll(template))
            assertItemsAreEquals(emptySequence(), tree.get(template))
        }
        test("an-ordered-tree-filters-clauses-by-body-as-well") {
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
    })

private fun reteTreeOf(vararg clauses: Clause): ReteTree = ReteTree.ordered(Unificator.default, *clauses)

private fun reteTreeOf(clauses: Iterable<Clause>): ReteTree = ReteTree.ordered(Unificator.default, clauses)
