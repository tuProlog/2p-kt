package it.unibo.tuprolog.collections.rete.nodes.set

import io.kotest.core.spec.style.FunSpec
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
        lateinit var emptyArityNodes: Iterable<ArityNode>

        lateinit var filledArityNodes: Iterable<ArityNode>

        val aRule: Rule = Rule.of(Atom.of("myTestRule"), Var.of("A"))

        fun init() {
            emptyArityNodes = (0..3).map { ArityNode(it) }
            filledArityNodes =
                (0..3).map { arity ->
                    ArityNode(arity).apply { ReteNodeUtils.rules.forEach { put(it) } }
                }
        }

        beforeTest { init() }

        test("arity-node-cannot-accept-negative-arity-upon-construction") {
            assertFailsWith<IllegalArgumentException> {
                ArityNode(
                    -1,
                )
            }
        }

        test("empty-node-has-empty-children-collection") {
            emptyArityNodes.forEach { assertTrue { it.children.isEmpty() } }
        }

        test("filled-node-has-non-empty-children-collection") {
            filledArityNodes.forEach { assertTrue { it.children.isNotEmpty() } }
        }

        test("clauses-correct") {
            emptyArityNodes.forEach { assertReteNodeEmpty(it) }
            filledArityNodes.forEach { assertCorrectAndPartialOrderRespected(it, ReteNodeUtils.rules) }
        }

        test("put-clause-inserts-rule") {
            // notice that no check is made, at this level, to ensure that inserted clause has correct "arity"
            emptyArityNodes.forEach { it.put(aRule) }

            emptyArityNodes.forEach { assertEquals(aRule, it.indexedElements.single()) }
        }

        test("put-clause-inserts-relatively-after-by-default") {
            filledArityNodes.forEach { it.put(aRule) }

            filledArityNodes.forEach { assertCorrectAndPartialOrderRespected(it, ReteNodeUtils.rules + aRule) }
        }

        test("put-clause-inserts-relatively-before-all-if-specified") {
            filledArityNodes.forEach { it.put(aRule, true) }

            filledArityNodes.forEach {
                assertCorrectAndPartialOrderRespected(it, ReteNodeUtils.rules.toMutableList().apply { add(0, aRule) })
            }
        }

        test("put-clause-creates-different-children-for-different-first-argument") {
            emptyArityNodes.forEach {
                it.put(Fact.of(Struct.of("f", Atom.of("a"), Empty.list())))
                it.put(Fact.of(Struct.of("f", Atom.of("b"), Empty.list())))

                assertEquals(2, it.children.count())
            }
        }

        test("put-clause-reuses-children-with-equals-first-argument") {
            emptyArityNodes.forEach {
                it.put(Fact.of(Struct.of("f", Atom.of("a"), Atom.of("b"))))
                it.put(Fact.of(Struct.of("f", Atom.of("a"), Atom.of("a"))))

                assertEquals(1, it.children.count())
            }
        }

        test("put-clause-reuses-structurally-equals-children") {
            emptyArityNodes.forEach {
                it.put(Fact.of(Struct.of("f", Var.of("A"), Atom.of("a"))))
                it.put(Fact.of(Struct.of("f", Var.anonymous(), Atom.of("a"))))

                assertEquals(1, it.children.count())
            }
        }

        test("put-clause-reuses-children-for-different-arity-but-same-first-arg") {
            emptyArityNodes.forEach {
                it.put(Fact.of(Struct.of("f", Atom.of("a"))))
                it.put(Fact.of(Struct.of("f", Atom.of("a"), Empty.list())))

                assertEquals(1, it.children.count())
            }
        }

        test("get-clause") {
            ReteNodeUtils.rulesQueryResultsMap.forEach { (query, result) ->
                filledArityNodes.forEach { assertClauseHeadPartialOrderingRespected(result, it.get(query).toList()) }
            }
        }

        test("remove-clause-with-zero-limit-does-nothing") {
            ReteNodeUtils.rulesQueryResultsMap.forEach { (query, _) ->
                filledArityNodes.forEach { assertNoChangesInReteNode(it) { remove(query, 0) } }
            }
        }

        test("remove-clause-from-empty-node-does-nothing") {
            filledArityNodes.forEach { assertNoChangesInReteNode(it) { remove(aRule) } }
        }

        test("remove-clause-with-limit-works-as-expected") {
            for (limit in 0..10) {
                ReteNodeUtils.rulesQueryResultsMap.forEach { (query, allMatching) ->
                    init() // because removal of side-effects is needed

                    filledArityNodes.forEach {
                        assertRemovedFromReteNodeRespectingPartialOrder(it, allMatching, limit) { remove(query, limit) }
                    }
                }
            }
        }

        test("remove-clause-with-negative-limit-removes-all-matching-rules") {
            val negativeLimit = -1
            ReteNodeUtils.rulesQueryResultsMap.forEach { (query, allMatching) ->
                init() // because removal of side-effects is needed

                filledArityNodes.forEach {
                    assertRemovedFromReteNodeRespectingPartialOrder(it, allMatching) { remove(query, negativeLimit) }
                }
            }
        }

        test("remove-all-clause") {
            ReteNodeUtils.rulesQueryResultsMap.forEach { (query, allMatching) ->
                init() // because removal of side-effects is needed

                filledArityNodes.forEach {
                    assertRemovedFromReteNodeRespectingPartialOrder(it, allMatching) { removeAll(query) }
                }
            }
        }

        test("deep-copy-creates-independent-instance") {
            emptyArityNodes.forEach {
                it.put(aRule)

                val independentCopy = it.deepCopy()

                assertReteNodeClausesCorrect(it, listOf(aRule))
                assertReteNodeClausesCorrect(independentCopy, listOf(aRule))

                it.remove(aRule)

                assertReteNodeClausesCorrect(it, emptyList())
                assertReteNodeClausesCorrect(independentCopy, listOf(aRule))
            }
        }

        test("deep-copy-does-not-copy-indexed-instances") {
            emptyArityNodes.forEach {
                it.put(aRule)

                val independentCopy = it.deepCopy()

                assertSame(it.indexedElements.single(), independentCopy.indexedElements.single())
            }
        }
    })
