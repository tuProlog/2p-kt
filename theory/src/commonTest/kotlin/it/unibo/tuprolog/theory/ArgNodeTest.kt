package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertReteNodeEmpty
import kotlin.test.*

/**
 * Test class for [ReteTree.ArgNode]
 *
 * @author Enrico
 */
internal class ArgNodeTest {

    private val aAtom = Atom.of("a")
    private val bAtom = Atom.of("b")

    private val aAtomAsFirstHeadArgRule = Fact.of(Struct.of("f", aAtom))
    private val aAtomAsThirdHeadArgRule = Fact.of(Struct.of("f", Var.anonymous(), Var.anonymous(), aAtom))

    private val aAtomRules = listOf(aAtomAsFirstHeadArgRule, aAtomAsThirdHeadArgRule)

    private val aAtomAsFirstHeadArgRuleNode = ReteTree.RuleNode(mutableListOf(aAtomAsFirstHeadArgRule))
    private val aAtomAsThirdHeadArgRuleNode = ReteTree.RuleNode(mutableListOf(aAtomAsThirdHeadArgRule))

    private lateinit var argNodeZeroIndexAndAAtom: ReteTree.ArgNode
    private lateinit var argNodeZeroIndexAndBAtom: ReteTree.ArgNode
    private lateinit var argNodeTwoIndexAndAAtom: ReteTree.ArgNode
    private lateinit var argNodeTwoIndexAndBAtom: ReteTree.ArgNode

    private lateinit var zeroIndexArgNodes: Iterable<ReteTree.ArgNode>
    private lateinit var twoIndexArgNodes: Iterable<ReteTree.ArgNode>
    private lateinit var allArgNodes: Iterable<ReteTree.ArgNode>
    private lateinit var allFilledArgNodes: Iterable<ReteTree.ArgNode>

    @BeforeTest
    fun init() {
        argNodeZeroIndexAndAAtom = ReteTree.ArgNode(0, aAtom)
        argNodeZeroIndexAndBAtom = ReteTree.ArgNode(0, bAtom)
        argNodeTwoIndexAndAAtom = ReteTree.ArgNode(2, aAtom)
        argNodeTwoIndexAndBAtom = ReteTree.ArgNode(2, bAtom)

        zeroIndexArgNodes = listOf(argNodeZeroIndexAndAAtom, argNodeZeroIndexAndBAtom)
        twoIndexArgNodes = listOf(argNodeTwoIndexAndAAtom, argNodeTwoIndexAndBAtom)
        allArgNodes = zeroIndexArgNodes + twoIndexArgNodes

        allFilledArgNodes = listOf(
                ReteTree.ArgNode(0, aAtom), ReteTree.ArgNode(0, bAtom),
                ReteTree.ArgNode(2, aAtom), ReteTree.ArgNode(2, bAtom)
        ).map { argNode -> argNode.apply { aAtomRules.forEach { put(it) } } }
    }

    @Test
    fun argNodeCannotAcceptNegativeIndexUponConstruction() {
        assertFailsWith<IllegalArgumentException> { ReteTree.ArgNode(-1, Atom.of("a")) }
    }

    @Test
    fun putClauseDoesntInsertAnythingIfNotARule() {
        allArgNodes.forEach {
            it.put(Directive.of(aAtom))

            assertReteNodeEmpty(it)
        }
    }

    @Test
    fun putClauseInsertsDirectlyRuleNodeChildIfNoMoreArgumentAfterThatIndexArePresent() {
        allArgNodes.forEach {
            it.put(aAtomAsFirstHeadArgRule)

            assertEquals(aAtomAsFirstHeadArgRuleNode, it.children[null])
        }

        init() // side-effects cleaning needed

        twoIndexArgNodes.forEach {
            it.put(aAtomAsThirdHeadArgRule)

            assertEquals(aAtomAsThirdHeadArgRuleNode, it.children[null])
        }
    }

    @Test
    fun putClauseCreatesArgNodeChildrenIfMoreArgumentsArePresentAfterItsIndex() {
        zeroIndexArgNodes.forEach {
            it.put(aAtomAsThirdHeadArgRule)

            val childOfZeroIndexArgNode = it.children[aAtomAsThirdHeadArgRule.head[1]] as ReteTree.ArgNode
            val childOfOneIndexArgNode = childOfZeroIndexArgNode.children[aAtomAsThirdHeadArgRule.head[2]] as ReteTree.ArgNode

            assertEquals(aAtomAsThirdHeadArgRuleNode, childOfOneIndexArgNode.children[null])
        }
    }

    @Test
    fun putClauseCreatesDifferentChildrenForDifferentArguments() {
        zeroIndexArgNodes.forEach {
            it.put(Fact.of(Struct.of("f", aAtom, Empty.list())))
            it.put(Fact.of(Struct.of("f", aAtom, Empty.set())))

            assertEquals(2, it.children.count())
        }
    }

    @Test
    fun putClauseReusesStructurallyEqualsChildren() {
        zeroIndexArgNodes.forEach {
            it.put(Fact.of(Struct.of("f", aAtom, Var.of("A"))))
            it.put(Fact.of(Struct.of("f", aAtom, Var.anonymous())))

            assertEquals(1, it.children.count())
        }
    }

    @Test
    fun putClauseForwardsCorrectlyTheBeforeFlagToChildren() {
        zeroIndexArgNodes.forEach {
            val clauses = listOf(
                    Fact.of(Struct.of("f", aAtom, Var.of("A"))),
                    Fact.of(Struct.of("f", aAtom, Var.anonymous())))

            it.run { clauses.forEach { clause -> put(clause, true) } }

            assertReteNodeClausesCorrect(it, clauses.reversed())
        }
    }

    @Test
    fun clausesCorrect() {
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
    fun getClauseWithDifferentTypeQueryAlwaysEmpty() {
        allFilledArgNodes.forEach { assertTrue { it.get(Directive.of(aAtom)).none() } }
    }

    @Test
    fun removeClauseWithZeroLimitDoesNothing() {
        // TODO
    }

}
