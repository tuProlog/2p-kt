package it.unibo.tuprolog.collections.rete.nodes.set

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.collections.rete.generic.set.ArgNode
import it.unibo.tuprolog.collections.rete.generic.set.RuleNode
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
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

        lateinit var argNodeZeroIndexAndAAtom: ArgNode

        lateinit var argNodeZeroIndexAndBAtom: ArgNode

        lateinit var argNodeTwoIndexAndAAtom: ArgNode

        lateinit var argNodeTwoIndexAndBAtom: ArgNode

        lateinit var zeroIndexEmptyArgNodes: Iterable<ArgNode>

        lateinit var twoIndexEmptyArgNodes: Iterable<ArgNode>

        lateinit var allEmptyArgNodes: Iterable<ArgNode>

        lateinit var allFilledArgNodes: Iterable<ArgNode>

        fun init() {
            argNodeZeroIndexAndAAtom = ArgNode(0, aAtom)
            argNodeZeroIndexAndBAtom = ArgNode(0, bAtom)
            argNodeTwoIndexAndAAtom = ArgNode(2, aAtom)
            argNodeTwoIndexAndBAtom = ArgNode(2, bAtom)

            zeroIndexEmptyArgNodes = listOf(argNodeZeroIndexAndAAtom, argNodeZeroIndexAndBAtom)
            twoIndexEmptyArgNodes = listOf(argNodeTwoIndexAndAAtom, argNodeTwoIndexAndBAtom)
            allEmptyArgNodes = zeroIndexEmptyArgNodes + twoIndexEmptyArgNodes

            allFilledArgNodes =
                listOf(
                    ArgNode(0, aAtom),
                    ArgNode(0, bAtom),
                    ArgNode(2, aAtom),
                    ArgNode(2, bAtom),
                ).map { argNode -> argNode.apply { aAtomRules.forEach { put(it) } } }
        }

        beforeTest { init() }

        test("arg-node-cannot-accept-negative-index-upon-construction") {
            assertFailsWith<IllegalArgumentException> {
                ArgNode(
                    -1,
                    Atom.of("a"),
                )
            }
        }

        test("put-clause-inserts-directly-rule-node-child-if-no-more-argument-after-that-index-are-present") {
            // notice that no check is made, at this level, to ensure that inserted clause has correct "term" at correct "index"
            allEmptyArgNodes.forEach {
                it.put(aAtomAsFirstHeadArgRule)

                assertEquals(aAtomAsFirstHeadArgRuleNode, it.children[null])
            }

            init() // side-effects cleaning needed

            twoIndexEmptyArgNodes.forEach {
                it.put(aAtomAsThirdHeadArgRule)

                assertEquals(aAtomAsThirdHeadArgRuleNode, it.children[null])
            }
        }

        test("put-clause-inserts-as-child-even-no-args-headed-rules") {
            // note that no check is made, at this level, to guarantee that no args headed rules should not be inserted there
            val noArgsHeadedRule = Fact.of(aAtom)
            allEmptyArgNodes.forEach {
                it.put(noArgsHeadedRule)

                assertEquals(
                    RuleNode(
                        mutableListOf(
                            noArgsHeadedRule,
                        ),
                    ),
                    it.children[null],
                )
            }
        }

        test("put-clause-creates-arg-node-children-if-more-arguments-are-present-after-its-index") {
            zeroIndexEmptyArgNodes.forEach {
                it.put(aAtomAsThirdHeadArgRule)

                val childOfZeroIndexArgNode = it.children[aAtomAsThirdHeadArgRule.head[1]] as ArgNode
                val childOfOneIndexArgNode =
                    childOfZeroIndexArgNode.children[aAtomAsThirdHeadArgRule.head[2]] as ArgNode

                assertEquals(aAtomAsThirdHeadArgRuleNode, childOfOneIndexArgNode.children[null])
            }
        }

        test("put-clause-creates-different-children-for-different-arguments") {
            zeroIndexEmptyArgNodes.forEach {
                it.put(Fact.of(Struct.of("f", aAtom, Empty.list())))
                it.put(Fact.of(Struct.of("f", aAtom, Empty.block())))

                assertEquals(2, it.children.count())
            }
        }

        test("put-clause-reuses-structurally-equals-children") {
            zeroIndexEmptyArgNodes.forEach {
                it.put(Fact.of(Struct.of("f", aAtom, Var.of("A"))))
                it.put(Fact.of(Struct.of("f", aAtom, Var.anonymous())))

                assertEquals(1, it.children.count())
            }
        }

        test("put-clause-forwards-correctly-the-before-flag-to-children") {
            zeroIndexEmptyArgNodes.forEach {
                val clauses =
                    listOf(
                        Fact.of(Struct.of("f", aAtom, Var.of("A"))),
                        Fact.of(Struct.of("f", aAtom, Var.anonymous())),
                    )

                it.run { clauses.forEach { clause -> put(clause, true) } }

                assertReteNodeClausesCorrect(it, clauses.reversed())
            }
        }

        test("clauses-correct") {
            allEmptyArgNodes.forEach { assertReteNodeEmpty(it) }
            allFilledArgNodes.forEach { assertReteNodeClausesCorrect(it, aAtomRules) }
        }

        test("get-clause-of-direct-child-returns-correctly") {
            allFilledArgNodes.forEach {
                assertEquals(aAtomAsFirstHeadArgRule, it.get(aAtomAsFirstHeadArgRule).single())
            }
        }

        test("get-clause-of-deep-children-returns-correctly") {
            allFilledArgNodes.forEach {
                assertEquals(aAtomAsThirdHeadArgRule, it.get(aAtomAsThirdHeadArgRule).single())
            }
        }

        test("remove-from-empty-node-does-nothing") {
            allEmptyArgNodes.forEach { assertNoChangesInReteNode(it) { remove(aAtomAsFirstHeadArgRule) } }
        }

        test("remove-clause-with-zero-limit-does-nothing") {
            allFilledArgNodes.forEach { argNode ->
                aAtomRules.forEach {
                    assertNoChangesInReteNode(argNode) { remove(it, limit = 0) }
                }
            }
        }

        test("remove-clause-with-limit-works-as-expected") {
            allFilledArgNodes.forEach { argNode ->
                init() // needed side-effects cleaning

                val oneArgClause = Fact.of(Struct.of("f", Var.anonymous()))
                argNode.put(oneArgClause, true)

                assertRemovedFromReteNodeRespectingPartialOrder(argNode, listOf(oneArgClause), 1) {
                    remove(oneArgClause, limit = 1)
                }
            }
        }

        test("remove-clause-with-negative-limit-removes-all-matching-rules") {
            allFilledArgNodes.forEach { argNode ->
                init() // needed side-effects cleaning

                val oneArgClause = Fact.of(Struct.of("f", Var.anonymous()))
                argNode.put(oneArgClause, true)

                assertRemovedFromReteNodeRespectingPartialOrder(
                    argNode,
                    listOf(oneArgClause, aAtomAsFirstHeadArgRule),
                ) {
                    remove(oneArgClause, limit = -1)
                }
            }
        }

        test("remove-all-clauses-works-as-expected") {
            allFilledArgNodes.forEach { argNode ->
                init() // needed side-effects cleaning

                val threeArgClause = Fact.of(Struct.of("f", aAtom, bAtom, Var.anonymous()))
                argNode.put(threeArgClause, true)

                assertRemovedFromReteNodeRespectingPartialOrder(
                    argNode,
                    listOf(threeArgClause, aAtomAsThirdHeadArgRule),
                ) {
                    removeAll(threeArgClause)
                }
            }
        }

        test("deep-copy-creates-independent-instance") {
            allEmptyArgNodes.forEach { argNode ->
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
        }
    })
