package it.unibo.tuprolog.collections.rete.nodes.set

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.collections.rete.generic.set.RuleNode
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.ReteNodeUtils
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertRemovedFromReteNode
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeEmpty
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [RuleNode]
 *
 * @author Enrico
 */
class RuleNodeSpec :
    FunSpec({
        lateinit var emptyRuleNode: RuleNode

        lateinit var filledRuleNode: RuleNode

        val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))

        fun init() {
            emptyRuleNode = RuleNode()
            filledRuleNode = RuleNode().apply { ReteNodeUtils.rules.forEach { put(it) } }
        }

        beforeTest { init() }

        test("children-empty") {
            assertTrue(emptyRuleNode.children.isEmpty())
            assertTrue(filledRuleNode.children.isEmpty())
        }

        test("clauses-correct") {
            assertReteNodeEmpty(emptyRuleNode)
            assertReteNodeClausesCorrect(filledRuleNode, ReteNodeUtils.rules)
        }

        test("put-clause-inserts-rule") {
            emptyRuleNode.put(aRule)

            assertEquals(aRule, emptyRuleNode.indexedElements.single())
        }

        test("put-clause-inserts-after-by-default") {
            filledRuleNode.put(aRule)

            assertEquals(aRule, filledRuleNode.indexedElements.last())
        }

        test("put-clause-inserts-before-all-if-specified") {
            filledRuleNode.put(aRule, true)

            assertEquals(aRule, filledRuleNode.indexedElements.first())
        }

        withTests(nameFn = { "get-clause/${it.id}" }, ReteNodeUtils.ruleQueryCases) { case ->
            assertEquals(case.expected, filledRuleNode.get(case.query).toList())
        }

        withTests(
            nameFn = { "remove-clause-with-zero-limit-does-nothing/${it.id}" },
            ReteNodeUtils.ruleQueryCases,
        ) { case ->
            assertNoChangesInReteNode(filledRuleNode) { remove(case.query, 0) }
        }

        test("remove-clause-from-empty-node-does-nothing") {
            assertNoChangesInReteNode(emptyRuleNode) { remove(aRule) }
        }

        withTests(
            nameFn = { (limit, case) -> "remove-clause-with-limit-works-as-expected/limit-$limit/${case.id}" },
            (0..10).flatMap { limit -> ReteNodeUtils.ruleQueryCases.map { limit to it } },
        ) { (limit, case) ->
            assertRemovedFromReteNode(filledRuleNode, case.expected.take(limit)) { remove(case.query, limit) }
        }

        withTests(
            nameFn = { "remove-clause-with-negative-limit-removes-all-matching-rules/${it.id}" },
            ReteNodeUtils.ruleQueryCases,
        ) { case ->
            assertRemovedFromReteNode(filledRuleNode, case.expected) { remove(case.query, -1) }
        }

        withTests(nameFn = { "remove-all-clause/${it.id}" }, ReteNodeUtils.ruleQueryCases) { case ->
            assertRemovedFromReteNode(filledRuleNode, case.expected) { removeAll(case.query) }
        }

        test("deep-copy-creates-independent-instance") {
            emptyRuleNode.put(aRule)

            val independentCopy = emptyRuleNode.deepCopy()

            assertReteNodeClausesCorrect(emptyRuleNode, listOf(aRule))
            assertReteNodeClausesCorrect(independentCopy, listOf(aRule))

            emptyRuleNode.remove(aRule)

            assertReteNodeClausesCorrect(emptyRuleNode, emptyList())
            assertReteNodeClausesCorrect(independentCopy, listOf(aRule))
        }

        test("deep-copy-does-not-copy-indexed-instances") {
            emptyRuleNode.put(aRule)

            val independentCopy = emptyRuleNode.deepCopy()

            assertSame(emptyRuleNode.indexedElements.single(), independentCopy.indexedElements.single())
        }
    })
