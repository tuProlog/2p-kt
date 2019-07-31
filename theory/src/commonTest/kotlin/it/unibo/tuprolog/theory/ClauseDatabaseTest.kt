package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.testutils.ClauseDatabaseUtils
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [ClauseDatabase.Companion]
 *
 * @author Enrico
 */
internal class ClauseDatabaseTest {

    private val correctInstance = ClauseDatabaseImpl(ClauseDatabaseUtils.wellFormedClauses)
    private val wellFormedClausesCorrectlyPreparedForExecution =
            ClauseDatabaseUtils.wellFormedClauses.map { replaceCorrectVarWithCalls(it) as Clause }

    /**
     * A function replacing correctly variables with call structure where needed
     *
     * For example, the [Clause] `product(A) :- A, A` is stored in the database, after preparation for execution,
     * as the Term: `product(A) :- call(A), call(A)`
     */
    private fun replaceCorrectVarWithCalls(term: Term): Term = when (term) {
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

    @Test
    fun ofVarargClauseCreatesCorrectInstance() {
        val toBeTested = ClauseDatabase.of(*ClauseDatabaseUtils.wellFormedClauses.toTypedArray())

        assertEquals(correctInstance, toBeTested)
    }

    @Test
    fun ofIterableClauseCreatesCorrectInstance() {
        val toBeTested = ClauseDatabase.of(ClauseDatabaseUtils.wellFormedClauses)

        assertEquals(correctInstance, toBeTested)
    }

    @Test
    fun ofSequenceClauseCreatesCorrectInstance() {
        val toBeTested = ClauseDatabase.of(ClauseDatabaseUtils.wellFormedClauses.asSequence())

        assertEquals(correctInstance, toBeTested)
    }

    @Test
    fun defaultPreparationForExecutionVisitorWorksAsExpected() {
        val aVar = Var.of("A")
        val aFactWithVarInHead = Fact.of(Tuple.of(aVar, aVar))
        val aRuleWithVarInHead = Rule.of(Tuple.of(aVar, aVar), Tuple.of(aVar, aVar))
        val aRuleWithVarInHeadAfterPreparation = Rule.of(Tuple.of(aVar, aVar), Tuple.of(Struct.of("call", aVar), Struct.of("call", aVar)))

        val toBeTested = (ClauseDatabaseUtils.wellFormedClauses + listOf(
                aFactWithVarInHead,
                aRuleWithVarInHead,
                aRuleWithVarInHeadAfterPreparation
        )).map { it.accept(ClauseDatabase.defaultPreparationForExecutionVisitor) }

        val correct = wellFormedClausesCorrectlyPreparedForExecution + listOf(
                aFactWithVarInHead,
                aRuleWithVarInHeadAfterPreparation,
                aRuleWithVarInHeadAfterPreparation
        )

        assertEquals(correct, toBeTested)
    }

    @Test
    fun prepareForExecutionWorksAsExpected() {
        val toBeTested = correctInstance.prepareForExecution()

        assertEquals(ClauseDatabaseImpl(wellFormedClausesCorrectlyPreparedForExecution), toBeTested)
    }

}
