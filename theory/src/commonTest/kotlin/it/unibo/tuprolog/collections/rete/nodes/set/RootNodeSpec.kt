package it.unibo.tuprolog.collections.rete.nodes.set

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.collections.rete.generic.ReteNode
import it.unibo.tuprolog.collections.rete.generic.set.RootNode
import it.unibo.tuprolog.core.Atom
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

        val reteTreeTestContents by lazy {
            listOf(
                ReteNodeUtils.rules,
                ReteNodeUtils.directives,
                ReteNodeUtils.mixedClauses,
            )
        }

        val correctInstances =
            reteTreeTestContents.map { clauses ->
                RootNode().apply { clauses.forEach { put(it) } }
            }

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

        test("get-clause") {
            ReteNodeUtils.mixedClausesQueryResultsMap.forEach { (query, result) ->
                assertEquals(result, filledRootNode.get(query).toList())
            }
        }

        test("remove-clause-with-zero-limit-does-nothing") {
            ReteNodeUtils.mixedClausesQueryResultsMap.forEach { (query, _) ->
                assertNoChangesInReteNode(filledRootNode) { remove(query, 0) }
            }
        }

        test("remove-clause-from-empty-node-does-nothing") {
            assertNoChangesInReteNode(emptyRootNode) { remove(aRule) }
        }

        test("remove-clause-with-limit-works-as-expected") {
            for (limit in 0..10) {
                ReteNodeUtils.mixedClausesQueryResultsMap.forEach { (query, allMatching) ->
                    init() // because removal of side-effects is needed

                    assertRemovedFromReteNodeRespectingPartialOrder(filledRootNode, allMatching, limit) {
                        remove(query, limit)
                    }
                }
            }
        }

        test("remove-clause-with-negative-limit-removes-all-matching-rules") {
            val negativeLimit = -1
            ReteNodeUtils.mixedClausesQueryResultsMap.forEach { (query, allMatching) ->
                init() // because removal of side-effects is needed

                assertRemovedFromReteNodeRespectingPartialOrder(filledRootNode, allMatching) {
                    remove(query, negativeLimit)
                }
            }
        }

        test("remove-all-clause") {
            ReteNodeUtils.mixedClausesQueryResultsMap.forEach { (query, allMatching) ->
                init() // because removal of side-effects is needed

                assertRemovedFromReteNodeRespectingPartialOrder(filledRootNode, allMatching) { removeAll(query) }
            }
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

        test("rete-tree-of-iterable-creates-correct-instances") {
            val toBeTested = reteTreeTestContents.map { ReteNode.ofSet(it) }

            assertEquals(correctInstances, toBeTested)
        }

        test("rete-tree-of-varargs-creates-correct-instances") {
            val toBeTested = reteTreeTestContents.map { ReteNode.ofSet(*it.toTypedArray()) }

            assertEquals(correctInstances, toBeTested)
        }
    })
