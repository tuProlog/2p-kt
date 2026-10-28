package it.unibo.tuprolog.collections.rete.nodes.set

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.collections.rete.generic.set.FunctorNode
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.ReteNodeUtils
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertClauseHeadPartialOrderingRespected
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertCorrectAndPartialOrderRespected
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertRemovedFromReteNodeRespectingPartialOrder
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeEmpty
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [FunctorNode]
 *
 * @author Enrico
 */
class FunctorNodeSpec :
    FunSpec({
        lateinit var emptyAFunctorNode: FunctorNode

        lateinit var filledAFunctorNode: FunctorNode

        val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))

        fun init() {
            emptyAFunctorNode = FunctorNode("a")
            filledAFunctorNode = FunctorNode("a").apply { ReteNodeUtils.aFunctorRules.forEach { put(it) } }
        }

        beforeTest { init() }

        test("empty-node-has-empty-children-collection") {
            assertTrue { emptyAFunctorNode.children.isEmpty() }
        }

        test("filled-node-has-non-empty-children-collection") {
            assertTrue { filledAFunctorNode.children.isNotEmpty() }
        }

        test("clauses-correct") {
            assertReteNodeEmpty(emptyAFunctorNode)
            assertCorrectAndPartialOrderRespected(filledAFunctorNode, ReteNodeUtils.aFunctorRules)
        }

        test("put-clause-inserts-rule") {
            emptyAFunctorNode.put(aRule)

            assertEquals(aRule, emptyAFunctorNode.indexedElements.single())
        }

        test("put-clause-doesnt-insert-wrong-functor-rule") {
            ReteNodeUtils.fFunctorRules.forEach { emptyAFunctorNode.put(it) }

            assertReteNodeEmpty(emptyAFunctorNode)
        }

        test("put-clause-inserts-relatively-after-by-default") {
            filledAFunctorNode.put(aRule)

            assertCorrectAndPartialOrderRespected(filledAFunctorNode, ReteNodeUtils.aFunctorRules + aRule)
        }

        test("put-clause-inserts-relatively-before-all-if-specified") {
            filledAFunctorNode.put(aRule, true)

            assertCorrectAndPartialOrderRespected(
                filledAFunctorNode,
                ReteNodeUtils.aFunctorRules.toMutableList().apply { add(0, aRule) },
            )
        }

        withTests(nameFn = { "get-clause/${it.id}" }, ReteNodeUtils.aFunctorRuleQueryCases) { case ->
            assertClauseHeadPartialOrderingRespected(case.expected, filledAFunctorNode.get(case.query).toList())
        }

        withTests(nameFn = {
            "remove-clause-with-zero-limit-does-nothing/${it.id}"
        }, ReteNodeUtils.aFunctorRuleQueryCases) { case ->
            assertNoChangesInReteNode(filledAFunctorNode) { remove(case.query, 0) }
        }

        test("remove-clause-from-empty-node-does-nothing") {
            assertNoChangesInReteNode(emptyAFunctorNode) { remove(aRule) }
        }

        withTests(
            nameFn = { (limit, case) -> "remove-clause-with-limit-works-as-expected/limit-$limit/${case.id}" },
            (0..10).flatMap { limit -> ReteNodeUtils.aFunctorRuleQueryCases.map { limit to it } },
        ) { (limit, case) ->
            assertRemovedFromReteNodeRespectingPartialOrder(
                filledAFunctorNode,
                case.expected,
                limit,
            ) { remove(case.query, limit) }
        }

        withTests(
            nameFn = { "remove-clause-with-negative-limit-removes-all-matching-rules/${it.id}" },
            ReteNodeUtils.aFunctorRuleQueryCases,
        ) { case ->
            assertRemovedFromReteNodeRespectingPartialOrder(
                filledAFunctorNode,
                case.expected,
            ) { remove(case.query, -1) }
        }

        withTests(nameFn = { "remove-all-clause/${it.id}" }, ReteNodeUtils.aFunctorRuleQueryCases) { case ->
            assertRemovedFromReteNodeRespectingPartialOrder(filledAFunctorNode, case.expected) { removeAll(case.query) }
        }

        test("deep-copy-creates-independent-instance") {
            emptyAFunctorNode.put(aRule)

            val independentCopy = emptyAFunctorNode.deepCopy()

            assertReteNodeClausesCorrect(emptyAFunctorNode, listOf(aRule))
            assertReteNodeClausesCorrect(independentCopy, listOf(aRule))

            emptyAFunctorNode.remove(aRule)

            assertReteNodeClausesCorrect(emptyAFunctorNode, emptyList())
            assertReteNodeClausesCorrect(independentCopy, listOf(aRule))
        }

        test("deep-copy-does-not-copy-indexed-instances") {
            emptyAFunctorNode.put(aRule)

            val independentCopy = emptyAFunctorNode.deepCopy()

            assertSame(emptyAFunctorNode.indexedElements.single(), independentCopy.indexedElements.single())
        }
    })
