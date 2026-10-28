package it.unibo.tuprolog.collections.rete.nodes.set

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.collections.rete.generic.set.ArityNode
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.ReteNodeUtils
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertClauseHeadPartialOrderingRespected
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertCorrectAndPartialOrderRespected
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertRemovedFromReteNodeRespectingPartialOrder
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeEmpty
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [ArityNode]
 *
 * @author Enrico
 */
class ArityNodeSpec :
    FunSpec({
        val aRule: Rule = Rule.of(Atom.of("myTestRule"), Var.of("A"))

        val arities = (0..3).toList()

        fun filledArityNode(arity: Int) = ArityNode(arity).apply { ReteNodeUtils.rules.forEach { put(it) } }

        fun arities(
            property: String,
            test: (Int) -> Unit,
        ) = withTests(nameFn = { "$property/arity-$it" }, arities) { test(it) }

        fun queries(
            property: String,
            test: (Int, ReteNodeUtils.QueryCase<Rule>) -> Unit,
        ) = withTests(
            nameFn = { (arity, case) -> "$property/arity-$arity/${case.id}" },
            arities.flatMap { arity -> ReteNodeUtils.ruleQueryCases.map { arity to it } },
        ) { (arity, case) -> test(arity, case) }

        test("arity-node-cannot-accept-negative-arity-upon-construction") {
            assertFailsWith<IllegalArgumentException> {
                ArityNode(
                    -1,
                )
            }
        }

        arities("empty-node-has-empty-children-collection") {
            assertTrue { ArityNode(it).children.isEmpty() }
        }

        arities("filled-node-has-non-empty-children-collection") {
            assertTrue { filledArityNode(it).children.isNotEmpty() }
        }

        arities("clauses-correct") {
            assertReteNodeEmpty(ArityNode(it))
            assertCorrectAndPartialOrderRespected(filledArityNode(it), ReteNodeUtils.rules)
        }

        arities("put-clause-inserts-rule") {
            // notice that no check is made, at this level, to ensure that inserted clause has correct "arity"
            val node = ArityNode(it).apply { put(aRule) }

            assertEquals(aRule, node.indexedElements.single())
        }

        arities("put-clause-inserts-relatively-after-by-default") {
            val node = filledArityNode(it).apply { put(aRule) }

            assertCorrectAndPartialOrderRespected(node, ReteNodeUtils.rules + aRule)
        }

        arities("put-clause-inserts-relatively-before-all-if-specified") {
            val node = filledArityNode(it).apply { put(aRule, true) }

            assertCorrectAndPartialOrderRespected(node, ReteNodeUtils.rules.toMutableList().apply { add(0, aRule) })
        }

        arities("put-clause-creates-different-children-for-different-first-argument") {
            val node = ArityNode(it)
            node.put(Fact.of(Struct.of("f", Atom.of("a"), Empty.list())))
            node.put(Fact.of(Struct.of("f", Atom.of("b"), Empty.list())))

            assertEquals(2, node.children.count())
        }

        arities("put-clause-reuses-children-with-equals-first-argument") {
            val node = ArityNode(it)
            node.put(Fact.of(Struct.of("f", Atom.of("a"), Atom.of("b"))))
            node.put(Fact.of(Struct.of("f", Atom.of("a"), Atom.of("a"))))

            assertEquals(1, node.children.count())
        }

        arities("put-clause-reuses-structurally-equals-children") {
            val node = ArityNode(it)
            node.put(Fact.of(Struct.of("f", Var.of("A"), Atom.of("a"))))
            node.put(Fact.of(Struct.of("f", Var.anonymous(), Atom.of("a"))))

            assertEquals(1, node.children.count())
        }

        arities("put-clause-reuses-children-for-different-arity-but-same-first-arg") {
            val node = ArityNode(it)
            node.put(Fact.of(Struct.of("f", Atom.of("a"))))
            node.put(Fact.of(Struct.of("f", Atom.of("a"), Empty.list())))

            assertEquals(1, node.children.count())
        }

        queries("get-clause") { arity, case ->
            assertClauseHeadPartialOrderingRespected(case.expected, filledArityNode(arity).get(case.query).toList())
        }

        queries("remove-clause-with-zero-limit-does-nothing") { arity, case ->
            assertNoChangesInReteNode(filledArityNode(arity)) { remove(case.query, 0) }
        }

        arities("remove-clause-from-empty-node-does-nothing") {
            assertNoChangesInReteNode(filledArityNode(it)) { remove(aRule) }
        }

        withTests(
            nameFn = { (limit, arity, case) ->
                "remove-clause-with-limit-works-as-expected/limit-$limit/arity-$arity/${case.id}"
            },
            (0..10).flatMap { limit ->
                arities.flatMap { arity -> ReteNodeUtils.ruleQueryCases.map { Triple(limit, arity, it) } }
            },
        ) { (limit, arity, case) ->
            assertRemovedFromReteNodeRespectingPartialOrder(filledArityNode(arity), case.expected, limit) {
                remove(case.query, limit)
            }
        }

        queries("remove-clause-with-negative-limit-removes-all-matching-rules") { arity, case ->
            assertRemovedFromReteNodeRespectingPartialOrder(filledArityNode(arity), case.expected) {
                remove(case.query, -1)
            }
        }

        queries("remove-all-clause") { arity, case ->
            assertRemovedFromReteNodeRespectingPartialOrder(filledArityNode(arity), case.expected) {
                removeAll(case.query)
            }
        }

        arities("deep-copy-creates-independent-instance") {
            val node = ArityNode(it)
            node.put(aRule)

            val independentCopy = node.deepCopy()

            assertReteNodeClausesCorrect(node, listOf(aRule))
            assertReteNodeClausesCorrect(independentCopy, listOf(aRule))

            node.remove(aRule)

            assertReteNodeClausesCorrect(node, emptyList())
            assertReteNodeClausesCorrect(independentCopy, listOf(aRule))
        }

        arities("deep-copy-does-not-copy-indexed-instances") {
            val node = ArityNode(it)
            node.put(aRule)

            val independentCopy = node.deepCopy()

            assertSame(node.indexedElements.single(), independentCopy.indexedElements.single())
        }
    })
