package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.DirectiveUtils
import it.unibo.tuprolog.core.testutils.FactUtils
import it.unibo.tuprolog.core.testutils.RuleUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [Clause] companion object
 *
 * @author Enrico
 */
internal class ClauseTest {
    private val mixedClauses = RuleUtils.mixedRules + DirectiveUtils.mixedDirectives.map { Pair(null, it) }

    private val correctInstances =
        RuleUtils.mixedRules.map { (head, body) -> Rule.of(head, body) } +
            DirectiveUtils.mixedDirectives.map { Directive.of(it) }

    private val wellFormedClauseInstances =
        RuleUtils.wellFormedRules.map { (head, body) -> Rule.of(head, body) } +
            DirectiveUtils.wellFormedDirectives.map { Directive.of(it) }

    private val nonWellFormedClauseInstances =
        RuleUtils.nonWellFormedRules.map { (head, body) -> Rule.of(head, body) } +
            DirectiveUtils.nonWellFormedDirectives.map { Directive.of(it) }

    /**
     * A function replacing correctly variables with call structure where needed
     *
     * For example, the [Clause] `product(A) :- A, A` is stored in the database, after preparation for execution,
     * as the Term: `product(A) :- call(A), call(A)`
     */
    private fun replaceCorrectVarWithCalls(term: Term): Term =
        when (term) {
            is Clause -> Clause.of(term.head, replaceCorrectVarWithCalls(term.body))
            is Struct ->
                when {
                    term.functor in Clause.notableFunctors && term.arity == 2 ->
                        Struct.of(term.functor, term.argsSequence.map { replaceCorrectVarWithCalls(it) })
                    else -> term
                }
            is Var -> Struct.of("call", term)
            else -> term
        }

    private val wellFormedClausesCorrectlyPreparedForExecution =
        correctInstances.filter { it.isWellFormed }.map { replaceCorrectVarWithCalls(it) as Clause }

    @Test
    fun clauseOfReturnsCorrectRuleOrDirectiveInstance() {
        val toBeTested = mixedClauses.map { (head, body) -> Clause.of(head, body) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun clauseOfWithOnlyNonNullHeadProvidedReturnsAFact() {
        val correctInstances = FactUtils.mixedFacts.map { Fact.of(it) }
        val toBeTested = FactUtils.mixedFacts.map { Clause.of(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun clauseOfNonNullHeadAndTrueBodyProvidedReturnsAFact() {
        val correctInstances = FactUtils.mixedFacts.map { Fact.of(it) }
        val toBeTested = FactUtils.mixedFacts.map { Clause.of(it, Truth.TRUE) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun clauseOfNullHeadAndBodyVarargConstructsTupleForMultipleArguments() {
        val directiveFirstPart = Atom.of("hello")
        val directiveSecondPart = Atom.of("world")

        val correctInstance = Directive.of(Tuple.wrapIfNeeded(directiveFirstPart, directiveSecondPart))
        val toBeTested = Clause.of(null, directiveFirstPart, directiveSecondPart)

        assertEqualities(correctInstance, toBeTested)
    }

    @Test
    fun clauseOfNullHeadAndNoVarargsComplains() {
        assertFailsWith<IllegalArgumentException> { Clause.of() }
    }

    @Test
    fun notableFunctorCorrect() {
        assertEquals(listOf(",", ";", "->"), Clause.notableFunctors.toList())
    }

    @Test
    fun bodyWellFormedVisitorWorksAsExpected() {
        wellFormedClauseInstances.forEach {
            assertTrue("${it.body} bodyWellFormedVisitor should return true") {
                it.body.accept(Clause.bodyWellFormedVisitor)
            }
        }
        nonWellFormedClauseInstances.forEach {
            assertFalse("${it.body} bodyWellFormedVisitor should return false") {
                it.body.accept(Clause.bodyWellFormedVisitor)
            }
        }
    }

    @Test
    fun defaultPreparationForExecutionVisitorWorksAsExpected() {
        val aVar = Var.of("A")
        val aFactWithVarInHead = Fact.of(Tuple.of(aVar, aVar))
        val aRuleWithVarInHead = Rule.of(Tuple.of(aVar, aVar), Tuple.of(aVar, aVar))
        val aRuleWithVarInHeadAfterPreparation =
            Rule.of(Tuple.of(aVar, aVar), Tuple.of(Struct.of("call", aVar), Struct.of("call", aVar)))

        val toBeTested =
            (
                correctInstances.filter { it.isWellFormed } +
                    listOf(
                        aFactWithVarInHead,
                        aRuleWithVarInHead,
                        aRuleWithVarInHeadAfterPreparation,
                    )
            ).map { it.accept(Clause.defaultPreparationForExecutionVisitor) }

        val correct =
            wellFormedClausesCorrectlyPreparedForExecution +
                listOf(
                    aFactWithVarInHead,
                    aRuleWithVarInHeadAfterPreparation,
                    aRuleWithVarInHeadAfterPreparation,
                )

        assertEquals(correct, toBeTested)
    }

    @Test
    fun prepareForExecutionWorksAsExpected() {
        val toBeTested = correctInstances.filter { it.isWellFormed }.map { it.prepareForExecution() }

        onCorrespondingItems(wellFormedClausesCorrectlyPreparedForExecution, toBeTested) { expected, actual ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun prepareForExecutionIsIdempotent() {
        val correct = correctInstances.filter { it.isWellFormed }.map { it.prepareForExecution() }
        val toBeTested =
            correctInstances.filter { it.isWellFormed }.map { it.prepareForExecution().prepareForExecution() }

        onCorrespondingItems(correct, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }
}
