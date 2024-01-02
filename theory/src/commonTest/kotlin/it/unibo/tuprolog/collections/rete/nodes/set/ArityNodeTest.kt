package it.unibo.tuprolog.collections.rete.nodes.set

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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [ArityNode]
 *
 * @author Enrico
 */
internal class ArityNodeTest {
    private lateinit var emptyArityNodes: Iterable<ArityNode>
    private lateinit var filledArityNodes: Iterable<ArityNode>

    private val aRule: Rule = Rule.of(Atom.of("myTestRule"), Var.of("A"))

    @BeforeTest
    fun init() {
        emptyArityNodes = (0..3).map { ArityNode(it) }
        filledArityNodes =
            (0..3).map { arity ->
                ArityNode(arity).apply { ReteNodeUtils.rules.forEach { put(it) } }
            }
    }

    @Test
    fun arityNodeCannotAcceptNegativeArityUponConstruction() {
        assertFailsWith<IllegalArgumentException> {
            ArityNode(
                -1,
            )
        }
    }

    @Test
    fun emptyNodeHasEmptyChildrenCollection() {
        emptyArityNodes.forEach { assertTrue { it.children.isEmpty() } }
    }

    @Test
    fun filledNodeHasNonEmptyChildrenCollection() {
        filledArityNodes.forEach { assertTrue { it.children.isNotEmpty() } }
    }

    @Test
    fun clausesCorrect() {
        emptyArityNodes.forEach { assertReteNodeEmpty(it) }
        filledArityNodes.forEach { assertCorrectAndPartialOrderRespected(it, ReteNodeUtils.rules) }
    }

    @Test
    fun putClauseInsertsRule() {
        // notice that no check is made, at this level, to ensure that inserted clause has correct "arity"
        emptyArityNodes.forEach { it.put(aRule) }

        emptyArityNodes.forEach { assertEquals(aRule, it.indexedElements.single()) }
    }

    @Test
    fun putClauseInsertsRelativelyAfterByDefault() {
        filledArityNodes.forEach { it.put(aRule) }

        filledArityNodes.forEach { assertCorrectAndPartialOrderRespected(it, ReteNodeUtils.rules + aRule) }
    }

    @Test
    fun putClauseInsertsRelativelyBeforeAllIfSpecified() {
        filledArityNodes.forEach { it.put(aRule, true) }

        filledArityNodes.forEach {
            assertCorrectAndPartialOrderRespected(it, ReteNodeUtils.rules.toMutableList().apply { add(0, aRule) })
        }
    }

    @Test
    fun putClauseCreatesDifferentChildrenForDifferentFirstArgument() {
        emptyArityNodes.forEach {
            it.put(Fact.of(Struct.of("f", Atom.of("a"), Empty.list())))
            it.put(Fact.of(Struct.of("f", Atom.of("b"), Empty.list())))

            assertEquals(2, it.children.count())
        }
    }

    @Test
    fun putClauseReusesChildrenWithEqualsFirstArgument() {
        emptyArityNodes.forEach {
            it.put(Fact.of(Struct.of("f", Atom.of("a"), Atom.of("b"))))
            it.put(Fact.of(Struct.of("f", Atom.of("a"), Atom.of("a"))))

            assertEquals(1, it.children.count())
        }
    }

    @Test
    fun putClauseReusesStructurallyEqualsChildren() {
        emptyArityNodes.forEach {
            it.put(Fact.of(Struct.of("f", Var.of("A"), Atom.of("a"))))
            it.put(Fact.of(Struct.of("f", Var.anonymous(), Atom.of("a"))))

            assertEquals(1, it.children.count())
        }
    }

    @Test
    fun putClauseReusesChildrenForDifferentArityButSameFirstArg() {
        emptyArityNodes.forEach {
            it.put(Fact.of(Struct.of("f", Atom.of("a"))))
            it.put(Fact.of(Struct.of("f", Atom.of("a"), Empty.list())))

            assertEquals(1, it.children.count())
        }
    }

    @Test
    fun getClause() {
        ReteNodeUtils.rulesQueryResultsMap.forEach { (query, result) ->
            filledArityNodes.forEach { assertClauseHeadPartialOrderingRespected(result, it.get(query).toList()) }
        }
    }

    @Test
    fun removeClauseWithZeroLimitDoesNothing() {
        ReteNodeUtils.rulesQueryResultsMap.forEach { (query, _) ->
            filledArityNodes.forEach { assertNoChangesInReteNode(it) { remove(query, 0) } }
        }
    }

    @Test
    fun removeClauseFromEmptyNodeDoesNothing() {
        filledArityNodes.forEach { assertNoChangesInReteNode(it) { remove(aRule) } }
    }

    @Test
    fun removeClauseWithLimitWorksAsExpected() {
        for (limit in 0..10) {
            ReteNodeUtils.rulesQueryResultsMap.forEach { (query, allMatching) ->
                init() // because removal of side-effects is needed

                filledArityNodes.forEach {
                    assertRemovedFromReteNodeRespectingPartialOrder(it, allMatching, limit) { remove(query, limit) }
                }
            }
        }
    }

    @Test
    fun removeClauseWithNegativeLimitRemovesAllMatchingRules() {
        val negativeLimit = -1
        ReteNodeUtils.rulesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            filledArityNodes.forEach {
                assertRemovedFromReteNodeRespectingPartialOrder(it, allMatching) { remove(query, negativeLimit) }
            }
        }
    }

    @Test
    fun removeAllClause() {
        ReteNodeUtils.rulesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            filledArityNodes.forEach {
                assertRemovedFromReteNodeRespectingPartialOrder(it, allMatching) { removeAll(query) }
            }
        }
    }

    @Test
    fun deepCopyCreatesIndependentInstance() {
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

    @Test
    fun deepCopyDoesNotCopyIndexedInstances() {
        emptyArityNodes.forEach {
            it.put(aRule)

            val independentCopy = it.deepCopy()

            assertSame(it.indexedElements.single(), independentCopy.indexedElements.single())
        }
    }
}
