package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertCorrectAndPartialOrderRespected
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertRemovedFromReteNodeRespectingPartialOrder
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertReteNodeEmpty
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertRuleHeadPartialOrderingRespected
import kotlin.test.*

/**
 * Test class for [ReteTree.ArityNode]
 *
 * @author Enrico
 */
internal class ArityNodeTest {

    private lateinit var emptyArityNodes: Iterable<ReteTree.ArityNode>
    private lateinit var filledArityNodes: Iterable<ReteTree.ArityNode>

    private val aRule: Rule = Rule.of(Atom.of("myTestRule"), Var.of("A"))
    private val aDirective: Directive = Directive.of(Truth.`true`(), Var.of("B"))

    @BeforeTest
    fun init() {
        emptyArityNodes = (0..3).map { ReteTree.ArityNode(it) }
        filledArityNodes = (0..3).map { arity ->
            ReteTree.ArityNode(arity).apply { ReteTreeUtils.rules.forEach { put(it) } }
        }
    }

    @Test
    fun arityNodeCannotAcceptNegativeArityUponConstruction() {
        assertFailsWith<IllegalArgumentException> { ReteTree.ArityNode(-1) }
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
        filledArityNodes.forEach { assertCorrectAndPartialOrderRespected(it, ReteTreeUtils.rules) }
    }

    @Test
    fun putClauseInsertsRule() {
        // notice that no check is made, at this level, to ensure that inserted clause has correct "arity"
        emptyArityNodes.forEach { it.put(aRule) }

        emptyArityNodes.forEach { assertEquals(aRule, it.clauses.single()) }
    }

    @Test
    fun putClauseDoesntInsertDirective() {
        emptyArityNodes.forEach { it.put(aDirective) }

        emptyArityNodes.forEach { assertReteNodeEmpty(it) }
    }

    @Test
    fun putClauseInsertsRelativelyAfterByDefault() {
        filledArityNodes.forEach { it.put(aRule) }

        filledArityNodes.forEach { assertCorrectAndPartialOrderRespected(it, ReteTreeUtils.rules + aRule) }
    }

    @Test
    fun putClauseInsertsRelativelyBeforeAllIfSpecified() {
        filledArityNodes.forEach { it.put(aRule, true) }

        filledArityNodes.forEach {
            assertCorrectAndPartialOrderRespected(it, ReteTreeUtils.rules.toMutableList().apply { add(0, aRule) })
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
        ReteTreeUtils.rulesQueryResultsMap.forEach { (query, result) ->
            filledArityNodes.forEach { assertRuleHeadPartialOrderingRespected(result, it.get(query).map { r -> r as Rule }.toList()) }
        }
    }

    @Test
    fun getClauseWithDifferentTypeQueryAlwaysEmpty() {
        ReteTreeUtils.directivesQueryResultsMap.forEach { (query, _) ->
            filledArityNodes.forEach { assertTrue { it.get(query).none() } }
        }
    }

    @Test
    fun removeClauseWithZeroLimitDoesNothing() {
        ReteTreeUtils.rulesQueryResultsMap.forEach { (query, _) ->
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
            ReteTreeUtils.rulesQueryResultsMap.forEach { (query, allMatching) ->
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
        ReteTreeUtils.rulesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            filledArityNodes.forEach {
                assertRemovedFromReteNodeRespectingPartialOrder(it, allMatching) { remove(query, negativeLimit) }
            }
        }
    }

    @Test
    fun removeAllClause() {
        ReteTreeUtils.rulesQueryResultsMap.forEach { (query, allMatching) ->
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

            assertSame(it.clauses.single(), independentCopy.clauses.single())
        }
    }

}
