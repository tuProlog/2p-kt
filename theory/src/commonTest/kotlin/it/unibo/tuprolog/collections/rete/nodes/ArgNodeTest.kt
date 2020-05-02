package it.unibo.tuprolog.collections.rete.nodes

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.collections.rete.nodes.old.ArgNode
import it.unibo.tuprolog.collections.rete.nodes.old.RuleNode
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils.assertRemovedFromReteNodeRespectingPartialOrder
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils.assertReteNodeEmpty
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [ArgNode]
 *
 * @author Enrico
 */
internal class ArgNodeTest {

    private val aAtom = Atom.of("a")
    private val bAtom = Atom.of("b")

    private val aAtomAsFirstHeadArgRule = Fact.of(Struct.of("f", aAtom))
    private val aAtomAsThirdHeadArgRule = Fact.of(Struct.of("f", Var.anonymous(), Var.anonymous(), aAtom))

    private val aAtomRules = listOf(aAtomAsFirstHeadArgRule, aAtomAsThirdHeadArgRule)

    private val aAtomAsFirstHeadArgRuleNode =
        RuleNode(mutableListOf(aAtomAsFirstHeadArgRule))
    private val aAtomAsThirdHeadArgRuleNode =
        RuleNode(mutableListOf(aAtomAsThirdHeadArgRule))

    private lateinit var argNodeZeroIndexAndAAtom: ArgNode
    private lateinit var argNodeZeroIndexAndBAtom: ArgNode
    private lateinit var argNodeTwoIndexAndAAtom: ArgNode
    private lateinit var argNodeTwoIndexAndBAtom: ArgNode

    private lateinit var zeroIndexEmptyArgNodes: Iterable<ArgNode>
    private lateinit var twoIndexEmptyArgNodes: Iterable<ArgNode>
    private lateinit var allEmptyArgNodes: Iterable<ArgNode>
    private lateinit var allFilledArgNodes: Iterable<ArgNode>

    @BeforeTest
    fun init() {
        argNodeZeroIndexAndAAtom = ArgNode(0, aAtom)
        argNodeZeroIndexAndBAtom = ArgNode(0, bAtom)
        argNodeTwoIndexAndAAtom = ArgNode(2, aAtom)
        argNodeTwoIndexAndBAtom = ArgNode(2, bAtom)

        zeroIndexEmptyArgNodes = listOf(argNodeZeroIndexAndAAtom, argNodeZeroIndexAndBAtom)
        twoIndexEmptyArgNodes = listOf(argNodeTwoIndexAndAAtom, argNodeTwoIndexAndBAtom)
        allEmptyArgNodes = zeroIndexEmptyArgNodes + twoIndexEmptyArgNodes

        allFilledArgNodes = listOf(
            ArgNode(0, aAtom),
            ArgNode(0, bAtom),
            ArgNode(2, aAtom),
            ArgNode(2, bAtom)
        ).map { argNode -> argNode.apply { aAtomRules.forEach { put(it) } } }
    }

    @Test
    fun argNodeCannotAcceptNegativeIndexUponConstruction() {
        assertFailsWith<IllegalArgumentException> {
            ArgNode(
                -1,
                Atom.of("a")
            )
        }
    }

    @Test
    fun putClauseInsertsDirectlyRuleNodeChildIfNoMoreArgumentAfterThatIndexArePresent() {
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

    @Test
    fun putClauseInsertsAsChildEvenNoArgsHeadedRules() {
        // note that no check is made, at this level, to guarantee that no args headed rules should not be inserted there
        val noArgsHeadedRule = Fact.of(aAtom)
        allEmptyArgNodes.forEach {
            it.put(noArgsHeadedRule)

            assertEquals(
                RuleNode(
                    mutableListOf(
                        noArgsHeadedRule
                    )
                ), it.children[null])
        }
    }

    @Test
    fun putClauseCreatesArgNodeChildrenIfMoreArgumentsArePresentAfterItsIndex() {
        zeroIndexEmptyArgNodes.forEach {
            it.put(aAtomAsThirdHeadArgRule)

            val childOfZeroIndexArgNode = it.children[aAtomAsThirdHeadArgRule.head[1]] as ArgNode
            val childOfOneIndexArgNode = childOfZeroIndexArgNode.children[aAtomAsThirdHeadArgRule.head[2]] as ArgNode

            assertEquals(aAtomAsThirdHeadArgRuleNode, childOfOneIndexArgNode.children[null])
        }
    }

    @Test
    fun putClauseCreatesDifferentChildrenForDifferentArguments() {
        zeroIndexEmptyArgNodes.forEach {
            it.put(Fact.of(Struct.of("f", aAtom, Empty.list())))
            it.put(Fact.of(Struct.of("f", aAtom, Empty.set())))

            assertEquals(2, it.children.count())
        }
    }

    @Test
    fun putClauseReusesStructurallyEqualsChildren() {
        zeroIndexEmptyArgNodes.forEach {
            it.put(Fact.of(Struct.of("f", aAtom, Var.of("A"))))
            it.put(Fact.of(Struct.of("f", aAtom, Var.anonymous())))

            assertEquals(1, it.children.count())
        }
    }

    @Test
    fun putClauseForwardsCorrectlyTheBeforeFlagToChildren() {
        zeroIndexEmptyArgNodes.forEach {
            val clauses = listOf(
                Fact.of(Struct.of("f", aAtom, Var.of("A"))),
                Fact.of(Struct.of("f", aAtom, Var.anonymous()))
            )

            it.run { clauses.forEach { clause -> put(clause, true) } }

            assertReteNodeClausesCorrect(it, clauses.reversed())
        }
    }

    @Test
    fun clausesCorrect() {
        allEmptyArgNodes.forEach { assertReteNodeEmpty(it) }
        allFilledArgNodes.forEach { assertReteNodeClausesCorrect(it, aAtomRules) }
    }

    @Test
    fun getClauseOfDirectChildReturnsCorrectly() {
        allFilledArgNodes.forEach {
            assertEquals(aAtomAsFirstHeadArgRule, it.get(aAtomAsFirstHeadArgRule).single())
        }
    }

    @Test
    fun getClauseOfDeepChildrenReturnsCorrectly() {
        allFilledArgNodes.forEach {
            assertEquals(aAtomAsThirdHeadArgRule, it.get(aAtomAsThirdHeadArgRule).single())
        }
    }

    @Test
    fun removeFromEmptyNodeDoesNothing() {
        allEmptyArgNodes.forEach { assertNoChangesInReteNode(it) { remove(aAtomAsFirstHeadArgRule) } }
    }

    @Test
    fun removeClauseWithZeroLimitDoesNothing() {
        allFilledArgNodes.forEach { argNode ->
            aAtomRules.forEach {
                assertNoChangesInReteNode(argNode) { remove(it, limit = 0) }
            }
        }
    }

    @Test
    fun removeClauseWithLimitWorksAsExpected() {
        allFilledArgNodes.forEach { argNode ->
            init() // needed side-effects cleaning

            val oneArgClause = Fact.of(Struct.of("f", Var.anonymous()))
            argNode.put(oneArgClause, true)

            assertRemovedFromReteNodeRespectingPartialOrder(argNode, listOf(oneArgClause), 1) {
                remove(oneArgClause, limit = 1)
            }
        }
    }

    @Test
    fun removeClauseWithNegativeLimitRemovesAllMatchingRules() {
        allFilledArgNodes.forEach { argNode ->
            init() // needed side-effects cleaning

            val oneArgClause = Fact.of(Struct.of("f", Var.anonymous()))
            argNode.put(oneArgClause, true)

            assertRemovedFromReteNodeRespectingPartialOrder(argNode, listOf(oneArgClause, aAtomAsFirstHeadArgRule)) {
                remove(oneArgClause, limit = -1)
            }
        }
    }

    @Test
    fun removeAllClausesWorksAsExpected() {
        allFilledArgNodes.forEach { argNode ->
            init() // needed side-effects cleaning

            val threeArgClause = Fact.of(Struct.of("f", aAtom, bAtom, Var.anonymous()))
            argNode.put(threeArgClause, true)

            assertRemovedFromReteNodeRespectingPartialOrder(argNode, listOf(threeArgClause, aAtomAsThirdHeadArgRule)) {
                removeAll(threeArgClause)
            }
        }
    }

    @Test
    fun deepCopyCreatesIndependentInstance() {
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
}
