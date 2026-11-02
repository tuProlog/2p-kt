package it.unibo.tuprolog.collections.rete.nodes.set

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.collections.rete.generic.set.DirectiveNode
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.ReteNodeUtils
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertRemovedFromReteNode
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeEmpty

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
            emptyDirectiveNode.children.isEmpty().shouldBeTrue()
            filledDirectiveNode.children.isEmpty().shouldBeTrue()
        }

        test("clauses-correct") {
            assertReteNodeEmpty(emptyDirectiveNode)
            assertReteNodeClausesCorrect(filledDirectiveNode, ReteNodeUtils.directives)
        }

        test("put-clause-inserts-directive") {
            emptyDirectiveNode.put(aDirective)

            emptyDirectiveNode.indexedElements.single() shouldBe aDirective
        }

        test("put-clause-inserts-after-by-default") {
            filledDirectiveNode.put(aDirective)

            filledDirectiveNode.indexedElements.last() shouldBe aDirective
        }

        test("put-clause-inserts-before-all-if-specified") {
            filledDirectiveNode.put(aDirective, true)

            filledDirectiveNode.indexedElements.first() shouldBe aDirective
        }

        withTests(nameFn = { "get-clause/${it.id}" }, ReteNodeUtils.directiveQueryCases) { case ->
            filledDirectiveNode.get(case.query).toList() shouldBe case.expected
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

            independentCopy.indexedElements.single() shouldBeSameInstanceAs emptyDirectiveNode.indexedElements.single()
        }
    })
