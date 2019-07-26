package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertClauseHeadPartialOrderingRespected
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertCorrectAndPartialOrderRespected
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertRemovedFromReteNodeRespectingPartialOrder
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertReteNodeEmpty
import kotlin.test.*

/**
 * Test class for [ReteTree.FunctorNode]
 *
 * @author Enrico
 */
internal class FunctorNodeTest {

    private lateinit var emptyAFunctorNode: ReteTree.FunctorNode
    private lateinit var filledAFunctorNode: ReteTree.FunctorNode

    private val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))
    private val aDirective: Directive = Directive.of(Truth.`true`(), Var.of("B"))

    @BeforeTest
    fun init() {
        emptyAFunctorNode = ReteTree.FunctorNode("a")
        filledAFunctorNode = ReteTree.FunctorNode("a").apply { ReteTreeUtils.aFunctorRules.forEach { put(it) } }
    }

    @Test
    fun emptyNodeHasEmptyChildrenCollection() {
        assertTrue { emptyAFunctorNode.children.isEmpty() }
    }

    @Test
    fun filledNodeHasNonEmptyChildrenCollection() {
        assertTrue { filledAFunctorNode.children.isNotEmpty() }
    }

    @Test
    fun clausesCorrect() {
        assertReteNodeEmpty(emptyAFunctorNode)
        assertCorrectAndPartialOrderRespected(filledAFunctorNode, ReteTreeUtils.aFunctorRules)
    }

    @Test
    fun putClauseInsertsRule() {
        emptyAFunctorNode.put(aRule)

        assertEquals(aRule, emptyAFunctorNode.clauses.single())
    }

    @Test
    fun putClauseDoesntInsertWrongFunctorRule() {
        ReteTreeUtils.fFunctorRules.forEach { emptyAFunctorNode.put(it) }

        assertReteNodeEmpty(emptyAFunctorNode)
    }

    @Test
    fun putClauseDoesntInsertDirective() {
        emptyAFunctorNode.put(aDirective)

        assertReteNodeEmpty(emptyAFunctorNode)
    }

    @Test
    fun putClauseInsertsRelativelyAfterByDefault() {
        filledAFunctorNode.put(aRule)

        assertCorrectAndPartialOrderRespected(filledAFunctorNode, ReteTreeUtils.aFunctorRules + aRule)
    }

    @Test
    fun putClauseInsertsRelativelyBeforeAllIfSpecified() {
        filledAFunctorNode.put(aRule, true)

        assertCorrectAndPartialOrderRespected(filledAFunctorNode, ReteTreeUtils.aFunctorRules.toMutableList().apply { add(0, aRule) })
    }

    @Test
    fun getClause() {
        ReteTreeUtils.aFunctorRulesQueryResultsMap.forEach { (query, result) ->
            assertClauseHeadPartialOrderingRespected(result, filledAFunctorNode.get(query).toList())
        }
    }

    @Test
    fun getClauseWithDifferentTypeQueryAlwaysEmpty() {
        ReteTreeUtils.directivesQueryResultsMap.forEach { (query, _) ->
            assertTrue { filledAFunctorNode.get(query).none() }
        }
    }

    @Test
    fun removeClauseWithZeroLimitDoesNothing() {
        ReteTreeUtils.aFunctorRulesQueryResultsMap.forEach { (query, _) ->
            assertNoChangesInReteNode(filledAFunctorNode) { remove(query, 0) }
        }
    }

    @Test
    fun removeClauseFromEmptyNodeDoesNothing() {
        assertNoChangesInReteNode(emptyAFunctorNode) { remove(aRule) }
    }

    @Test
    fun removeClauseWithLimitWorksAsExpected() {
        for (limit in 0..10) {
            ReteTreeUtils.aFunctorRulesQueryResultsMap.forEach { (query, allMatching) ->
                init() // because removal of side-effects is needed

                assertRemovedFromReteNodeRespectingPartialOrder(filledAFunctorNode, allMatching, limit) { remove(query, limit) }
            }
        }
    }

    @Test
    fun removeClauseWithNegativeLimitRemovesAllMatchingRules() {
        val negativeLimit = -1
        ReteTreeUtils.aFunctorRulesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNodeRespectingPartialOrder(filledAFunctorNode, allMatching) { remove(query, negativeLimit) }
        }
    }

    @Test
    fun removeAllClause() {
        ReteTreeUtils.aFunctorRulesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNodeRespectingPartialOrder(filledAFunctorNode, allMatching) { removeAll(query) }
        }
    }

    @Test
    fun deepCopyCreatesIndependentInstance() {
        emptyAFunctorNode.put(aRule)

        val independentCopy = emptyAFunctorNode.deepCopy()

        assertReteNodeClausesCorrect(emptyAFunctorNode, listOf(aRule))
        assertReteNodeClausesCorrect(independentCopy, listOf(aRule))

        emptyAFunctorNode.remove(aRule)

        assertReteNodeClausesCorrect(emptyAFunctorNode, emptyList())
        assertReteNodeClausesCorrect(independentCopy, listOf(aRule))
    }

    @Test
    fun deepCopyDoesNotCopyIndexedInstances() {
        emptyAFunctorNode.put(aRule)

        val independentCopy = emptyAFunctorNode.deepCopy()

        assertSame(emptyAFunctorNode.clauses.single(), independentCopy.clauses.single())
    }
}
