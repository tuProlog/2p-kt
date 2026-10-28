package it.unibo.tuprolog.collections.rete.nodes.set

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.collections.rete.generic.set.ArgNode
import it.unibo.tuprolog.collections.rete.generic.set.RuleNode
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertRemovedFromReteNodeRespectingPartialOrder
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeEmpty
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [ArgNode]
 *
 * @author Enrico
 */
class ArgNodeSpec :
    FunSpec({
        val aAtom = Atom.of("a")

        val bAtom = Atom.of("b")

        val aAtomAsFirstHeadArgRule = Fact.of(Struct.of("f", aAtom))

        val aAtomAsThirdHeadArgRule = Fact.of(Struct.of("f", Var.anonymous(), Var.anonymous(), aAtom))

        val aAtomRules = listOf(aAtomAsFirstHeadArgRule, aAtomAsThirdHeadArgRule)

        val aAtomAsFirstHeadArgRuleNode =
            RuleNode(mutableListOf(aAtomAsFirstHeadArgRule))

        val aAtomAsThirdHeadArgRuleNode =
            RuleNode(mutableListOf(aAtomAsThirdHeadArgRule))

        val zeroIndexArgNodes =
            listOf(
                ArgNodeCase("index-0-a", 0, aAtom),
                ArgNodeCase("index-0-b", 0, bAtom),
            )

        val twoIndexArgNodes =
            listOf(
                ArgNodeCase("index-2-a", 2, aAtom),
                ArgNodeCase("index-2-b", 2, bAtom),
            )

        val allArgNodes = zeroIndexArgNodes + twoIndexArgNodes

        fun ArgNodeCase.filled() = empty().apply { aAtomRules.forEach { put(it) } }

        fun argNodes(
            property: String,
            cases: List<ArgNodeCase> = allArgNodes,
            test: (ArgNodeCase) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, cases) { test(it) }

        test("arg-node-cannot-accept-negative-index-upon-construction") {
            assertFailsWith<IllegalArgumentException> {
                ArgNode(
                    -1,
                    Atom.of("a"),
                )
            }
        }

        // notice that no check is made, at this level, to ensure that inserted clause has correct "term" at correct "index"
        argNodes("put-clause-inserts-directly-rule-node-child-if-no-more-argument-after-that-index-are-present") {
            val argNode = it.empty().apply { put(aAtomAsFirstHeadArgRule) }

            assertEquals(aAtomAsFirstHeadArgRuleNode, argNode.children[null])
        }

        argNodes(
            "put-third-arg-clause-inserts-directly-rule-node-child-if-no-more-argument-after-that-index-are-present",
            twoIndexArgNodes,
        ) {
            val argNode = it.empty().apply { put(aAtomAsThirdHeadArgRule) }

            assertEquals(aAtomAsThirdHeadArgRuleNode, argNode.children[null])
        }

        argNodes("put-clause-inserts-as-child-even-no-args-headed-rules") {
            // note that no check is made, at this level, to guarantee that no args headed rules should not be inserted there
            val noArgsHeadedRule = Fact.of(aAtom)
            val argNode = it.empty().apply { put(noArgsHeadedRule) }

            assertEquals(
                RuleNode(
                    mutableListOf(
                        noArgsHeadedRule,
                    ),
                ),
                argNode.children[null],
            )
        }

        argNodes(
            "put-clause-creates-arg-node-children-if-more-arguments-are-present-after-its-index",
            zeroIndexArgNodes,
        ) {
            val argNode = it.empty().apply { put(aAtomAsThirdHeadArgRule) }

            val childOfZeroIndexArgNode = argNode.children[aAtomAsThirdHeadArgRule.head[1]] as ArgNode
            val childOfOneIndexArgNode =
                childOfZeroIndexArgNode.children[aAtomAsThirdHeadArgRule.head[2]] as ArgNode

            assertEquals(aAtomAsThirdHeadArgRuleNode, childOfOneIndexArgNode.children[null])
        }

        argNodes("put-clause-creates-different-children-for-different-arguments", zeroIndexArgNodes) {
            val argNode = it.empty()
            argNode.put(Fact.of(Struct.of("f", aAtom, Empty.list())))
            argNode.put(Fact.of(Struct.of("f", aAtom, Empty.block())))

            assertEquals(2, argNode.children.count())
        }

        argNodes("put-clause-reuses-structurally-equals-children", zeroIndexArgNodes) {
            val argNode = it.empty()
            argNode.put(Fact.of(Struct.of("f", aAtom, Var.of("A"))))
            argNode.put(Fact.of(Struct.of("f", aAtom, Var.anonymous())))

            assertEquals(1, argNode.children.count())
        }

        argNodes("put-clause-forwards-correctly-the-before-flag-to-children", zeroIndexArgNodes) {
            val argNode = it.empty()
            val clauses =
                listOf(
                    Fact.of(Struct.of("f", aAtom, Var.of("A"))),
                    Fact.of(Struct.of("f", aAtom, Var.anonymous())),
                )

            clauses.forEach { clause -> argNode.put(clause, true) }

            assertReteNodeClausesCorrect(argNode, clauses.reversed())
        }

        argNodes("clauses-correct") {
            assertReteNodeEmpty(it.empty())
            assertReteNodeClausesCorrect(it.filled(), aAtomRules)
        }

        argNodes("get-clause-of-direct-child-returns-correctly") {
            assertEquals(aAtomAsFirstHeadArgRule, it.filled().get(aAtomAsFirstHeadArgRule).single())
        }

        argNodes("get-clause-of-deep-children-returns-correctly") {
            assertEquals(aAtomAsThirdHeadArgRule, it.filled().get(aAtomAsThirdHeadArgRule).single())
        }

        argNodes("remove-from-empty-node-does-nothing") {
            assertNoChangesInReteNode(it.empty()) { remove(aAtomAsFirstHeadArgRule) }
        }

        argNodes("remove-clause-with-zero-limit-does-nothing") {
            val argNode = it.filled()
            aAtomRules.forEach { rule ->
                assertNoChangesInReteNode(argNode) { remove(rule, limit = 0) }
            }
        }

        argNodes("remove-clause-with-limit-works-as-expected") {
            val argNode = it.filled()
            val oneArgClause = Fact.of(Struct.of("f", Var.anonymous()))
            argNode.put(oneArgClause, true)

            assertRemovedFromReteNodeRespectingPartialOrder(argNode, listOf(oneArgClause), 1) {
                remove(oneArgClause, limit = 1)
            }
        }

        argNodes("remove-clause-with-negative-limit-removes-all-matching-rules") {
            val argNode = it.filled()
            val oneArgClause = Fact.of(Struct.of("f", Var.anonymous()))
            argNode.put(oneArgClause, true)

            assertRemovedFromReteNodeRespectingPartialOrder(
                argNode,
                listOf(oneArgClause, aAtomAsFirstHeadArgRule),
            ) {
                remove(oneArgClause, limit = -1)
            }
        }

        argNodes("remove-all-clauses-works-as-expected") {
            val argNode = it.filled()
            val threeArgClause = Fact.of(Struct.of("f", aAtom, bAtom, Var.anonymous()))
            argNode.put(threeArgClause, true)

            assertRemovedFromReteNodeRespectingPartialOrder(
                argNode,
                listOf(threeArgClause, aAtomAsThirdHeadArgRule),
            ) {
                removeAll(threeArgClause)
            }
        }

        argNodes("deep-copy-creates-independent-instance") {
            val argNode = it.empty()
            aAtomRules.forEach { aRule ->
                argNode.put(aRule)

                val independentCopy = argNode.deepCopy()

                assertReteNodeClausesCorrect(argNode, listOf(aRule))
                assertReteNodeClausesCorrect(independentCopy, listOf(aRule))

                argNode.remove(aRule)

                assertReteNodeClausesCorrect(argNode, emptyList())
                assertReteNodeClausesCorrect(independentCopy, listOf(aRule))
            }
        }
    })

private data class ArgNodeCase(
    val id: String,
    val index: Int,
    val term: Term,
) {
    fun empty() = ArgNode(index, term)
}
