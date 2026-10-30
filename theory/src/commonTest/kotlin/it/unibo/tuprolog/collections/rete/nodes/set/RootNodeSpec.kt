package it.unibo.tuprolog.collections.rete.nodes.set

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.collections.rete.generic.ReteNode
import it.unibo.tuprolog.collections.rete.generic.set.RootNode
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.ReteNodeUtils
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertCorrectAndPartialOrderRespected
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertRemovedFromReteNodeRespectingPartialOrder
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeEmpty
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [RootNode]
 *
 * @author Enrico
 */
class RootNodeSpec :
    FunSpec({
        lateinit var emptyRootNode: RootNode

        lateinit var filledRootNode: RootNode

        val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))

        val aDirective: Directive = Directive.of(Truth.TRUE, Var.of("B"))

        val reteTreeContentCases =
            listOf(
                ContentCase("rules", ReteNodeUtils.rules),
                ContentCase("directives", ReteNodeUtils.directives),
                ContentCase("mixed-clauses", ReteNodeUtils.mixedClauses),
            )

        fun correctInstance(clauses: List<Clause>) = RootNode().apply { clauses.forEach { put(it) } }

        fun init() {
            emptyRootNode = RootNode()
            filledRootNode = RootNode().apply { ReteNodeUtils.mixedClauses.forEach { put(it) } }
        }

        beforeTest { init() }

        test("empty-node-has-empty-children-collection") {
            assertTrue { emptyRootNode.children.isEmpty() }
        }

        test("filled-node-has-non-empty-children-collection") {
            assertTrue { filledRootNode.children.isNotEmpty() }
        }

        test("clauses-correct") {
            assertReteNodeEmpty(emptyRootNode)
            assertCorrectAndPartialOrderRespected(filledRootNode, ReteNodeUtils.mixedClauses)
        }

        test("put-clause-inserts-rule") {
            emptyRootNode.put(aRule)

            assertEquals(aRule, emptyRootNode.indexedElements.single())
        }

        test("put-clause-inserts-relatively-after-by-default") {
            filledRootNode.put(aRule)
            filledRootNode.put(aDirective)

            assertCorrectAndPartialOrderRespected(
                filledRootNode,
                ReteNodeUtils.mixedClauses + listOf(aRule, aDirective),
            )
        }

        test("put-clause-inserts-relatively-before-all-if-specified") {
            filledRootNode.put(aDirective, true)
            filledRootNode.put(aRule, true)

            assertCorrectAndPartialOrderRespected(
                filledRootNode,
                ReteNodeUtils.mixedClauses.toMutableList().apply {
                    addAll(0, listOf(aRule, aDirective))
                },
            )
        }

        withTests(nameFn = { "get-clause/${it.id}" }, ReteNodeUtils.mixedClauseQueryCases) { case ->
            assertEquals(case.expected, filledRootNode.get(case.query).toList())
        }

        withTests(nameFn = {
            "remove-clause-with-zero-limit-does-nothing/${it.id}"
        }, ReteNodeUtils.mixedClauseQueryCases) { case ->
            assertNoChangesInReteNode(filledRootNode) { remove(case.query, 0) }
        }

        test("remove-clause-from-empty-node-does-nothing") {
            assertNoChangesInReteNode(emptyRootNode) { remove(aRule) }
        }

        withTests(
            nameFn = { (limit, case) -> "remove-clause-with-limit-works-as-expected/limit-$limit/${case.id}" },
            (0..10).flatMap { limit -> ReteNodeUtils.mixedClauseQueryCases.map { limit to it } },
        ) { (limit, case) ->
            assertRemovedFromReteNodeRespectingPartialOrder(
                filledRootNode,
                case.expected,
                limit,
            ) { remove(case.query, limit) }
        }

        withTests(
            nameFn = { "remove-clause-with-negative-limit-removes-all-matching-rules/${it.id}" },
            ReteNodeUtils.mixedClauseQueryCases,
        ) { case ->
            assertRemovedFromReteNodeRespectingPartialOrder(filledRootNode, case.expected) { remove(case.query, -1) }
        }

        withTests(nameFn = { "remove-all-clause/${it.id}" }, ReteNodeUtils.mixedClauseQueryCases) { case ->
            assertRemovedFromReteNodeRespectingPartialOrder(filledRootNode, case.expected) { removeAll(case.query) }
        }

        test("deep-copy-creates-independent-instance") {
            emptyRootNode.put(aRule)

            val independentCopy = emptyRootNode.deepCopy()

            assertReteNodeClausesCorrect(emptyRootNode, listOf(aRule))
            assertReteNodeClausesCorrect(independentCopy, listOf(aRule))

            emptyRootNode.remove(aRule)

            assertReteNodeClausesCorrect(emptyRootNode, emptyList())
            assertReteNodeClausesCorrect(independentCopy, listOf(aRule))
        }

        test("deep-copy-does-not-copy-indexed-instances") {
            emptyRootNode.put(aRule)

            val independentCopy = emptyRootNode.deepCopy()

            assertSame(emptyRootNode.indexedElements.single(), independentCopy.indexedElements.single())
        }

        withTests(
            nameFn = { "rete-tree-of-iterable-creates-correct-instances/${it.id}" },
            reteTreeContentCases,
        ) { case ->
            assertEquals(correctInstance(case.clauses), ReteNode.ofSet(case.clauses))
        }

        withTests(
            nameFn = { "rete-tree-of-varargs-creates-correct-instances/${it.id}" },
            reteTreeContentCases,
        ) { case ->
            assertEquals(correctInstance(case.clauses), ReteNode.ofSet(*case.clauses.toTypedArray()))
        }
    })

private data class ContentCase(
    val id: String,
    val clauses: List<Clause>,
)
