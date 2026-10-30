package it.unibo.tuprolog.collections.rete.nodes.set

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.collections.rete.generic.set.DirectiveNode
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Truth
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
 * Test class for [DirectiveNode]
 *
 * @author Enrico
 */
class DirectiveNodeSpec :
    FunSpec({
        lateinit var emptyDirectiveNode: DirectiveNode

        lateinit var filledDirectiveNode: DirectiveNode

        val aDirective: Directive = Directive.of(Truth.TRUE, Var.of("B"))

        fun init() {
            emptyDirectiveNode = DirectiveNode()
            filledDirectiveNode = DirectiveNode().apply { ReteNodeUtils.directives.forEach { put(it) } }
        }

        beforeTest { init() }

        test("children-empty") {
            assertTrue(emptyDirectiveNode.children.isEmpty())
            assertTrue(filledDirectiveNode.children.isEmpty())
        }

        test("clauses-correct") {
            assertReteNodeEmpty(emptyDirectiveNode)
            assertReteNodeClausesCorrect(filledDirectiveNode, ReteNodeUtils.directives)
        }

        test("put-clause-inserts-directive") {
            emptyDirectiveNode.put(aDirective)

            assertEquals(aDirective, emptyDirectiveNode.indexedElements.single())
        }

        test("put-clause-inserts-after-by-default") {
            filledDirectiveNode.put(aDirective)

            assertEquals(aDirective, filledDirectiveNode.indexedElements.last())
        }

        test("put-clause-inserts-before-all-if-specified") {
            filledDirectiveNode.put(aDirective, true)

            assertEquals(aDirective, filledDirectiveNode.indexedElements.first())
        }

        withTests(nameFn = { "get-clause/${it.id}" }, ReteNodeUtils.directiveQueryCases) { case ->
            assertEquals(case.expected, filledDirectiveNode.get(case.query).toList())
        }

        withTests(nameFn = {
            "remove-clause-with-zero-limit-does-nothing/${it.id}"
        }, ReteNodeUtils.directiveQueryCases) { case ->
            assertNoChangesInReteNode(filledDirectiveNode) { remove(case.query, 0) }
        }

        test("remove-clause-from-empty-node-does-nothing") {
            assertNoChangesInReteNode(emptyDirectiveNode) { remove(aDirective) }
        }

        withTests(
            nameFn = { (limit, case) -> "remove-clause-with-limit-works-as-expected/limit-$limit/${case.id}" },
            (0..10).flatMap { limit -> ReteNodeUtils.directiveQueryCases.map { limit to it } },
        ) { (limit, case) ->
            assertRemovedFromReteNode(filledDirectiveNode, case.expected.take(limit)) { remove(case.query, limit) }
        }

        withTests(
            nameFn = { "remove-clause-with-negative-limit-removes-all-matching-directives/${it.id}" },
            ReteNodeUtils.directiveQueryCases,
        ) { case ->
            assertRemovedFromReteNode(filledDirectiveNode, case.expected) { remove(case.query, -1) }
        }

        withTests(nameFn = { "remove-all-clause/${it.id}" }, ReteNodeUtils.directiveQueryCases) { case ->
            assertRemovedFromReteNode(filledDirectiveNode, case.expected) { removeAll(case.query) }
        }

        test("deep-copy-creates-independent-instance") {
            emptyDirectiveNode.put(aDirective)

            val independentCopy = emptyDirectiveNode.deepCopy()

            assertReteNodeClausesCorrect(emptyDirectiveNode, listOf(aDirective))
            assertReteNodeClausesCorrect(independentCopy, listOf(aDirective))

            emptyDirectiveNode.remove(aDirective)

            assertReteNodeClausesCorrect(emptyDirectiveNode, emptyList())
            assertReteNodeClausesCorrect(independentCopy, listOf(aDirective))
        }

        test("deep-copy-does-not-copy-indexed-instances") {
            emptyDirectiveNode.put(aDirective)

            val independentCopy = emptyDirectiveNode.deepCopy()

            assertSame(emptyDirectiveNode.indexedElements.single(), independentCopy.indexedElements.single())
        }
    })
