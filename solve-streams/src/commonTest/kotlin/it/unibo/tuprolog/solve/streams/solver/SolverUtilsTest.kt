package it.unibo.tuprolog.solve.streams.solver

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.theory.Theory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Test class for SolverUtils.kt
 *
 * @author Enrico
 */
internal class SolverUtilsTest {

    private val aContext = StreamsExecutionContext(
        dynamicKb = Theory.of({ factOf(atomOf("a")) }),
        staticKb = Theory.of({ factOf(atomOf("a")) }),
        flags = FlagStore.EMPTY
    )

    /** A "true" solveRequest */
    private val solveRequest = Solve.Request(Signature("true", 0), emptyList(), aContext)

    @Test
    fun isWellFormedWorksAsExpected() {
        val wellFormedGoal = Struct.of("a", Integer.of(2))
        val nonWellFormedGoal = Tuple.of(Atom.of("a"), Integer.of(3))

        assertEquals(wellFormedGoal.isWellFormed(), wellFormedGoal.accept(Clause.bodyWellFormedVisitor))
        assertEquals(nonWellFormedGoal.isWellFormed(), nonWellFormedGoal.accept(Clause.bodyWellFormedVisitor))
    }

    @Test
    fun prepareForExecutionAsGoalWorksAsExpected() {
        val aVar = Var.of("A")
        val bVar = Var.of("B")
        val correct = Tuple.of(Struct.of("call", aVar), Struct.of("call", bVar))

        val toBeTested1 = Tuple.of(aVar, bVar).prepareForExecutionAsGoal()
        val toBeTested2 = Tuple.of(aVar, bVar).prepareForExecutionAsGoal().prepareForExecutionAsGoal()

        assertEquals(correct, toBeTested1)
        assertEquals(correct, toBeTested2)
    }

    @Test
    fun orderWithStrategyWithEmptyElementsDoesNothing() {
        assertEquals(
            emptySequence<Nothing>().toList(),
            emptySequence<Nothing>().orderWithStrategy(aContext) { seq, _ -> seq.first() }.toList()
        )
    }

    @Test
    fun orderWithStrategyPassesCorrectlyParametersToSelectionFunction() {
        emptySequence<Unit>().orderWithStrategy(aContext) { sequence, context ->
            assertEquals(emptySequence(), sequence)
            assertEquals(DummyInstances.executionContext, context)
        }
    }

    @Test
    fun orderWithStrategyAppliesCorrectlySelectionStrategy() {
        val testSequence = sequenceOf(1, 5, 2, 9, 3, 0, 55)
        val toBeTested = testSequence.orderWithStrategy(aContext) { seq, _ -> seq.minOrNull()!! }

        assertEquals(testSequence.sorted().toList(), toBeTested.toList())
    }

    @Test
    fun orderWithStrategyDoesntRemoveDuplicatedItems() {
        val testSequence = sequenceOf(1, 5, 2, 9, 3, 0, 5)
        val toBeTested = testSequence.orderWithStrategy(aContext) { seq, _ -> seq.maxOrNull()!! }

        assertEquals(testSequence.sortedDescending().toList(), toBeTested.toList())
    }

    @Test
    fun orderWithStrategyWorksLazily() {
        val testSequence =
            sequenceOf({ 1 }, { 5 }, { 2 }, { 9 }, { 3 }, { 0 }, { 5 }, { throw IllegalStateException() })
        val testSeqElemCount = testSequence.count()

        val toBeTested = testSequence.orderWithStrategy(aContext) { seq, _ -> seq.first().also { it() } }
        val testSeqIterator = testSequence.iterator()
        val toBeTestedIterator = toBeTested.iterator()

        repeat(testSeqElemCount - 1) { assertSame(testSeqIterator.next(), toBeTestedIterator.next()) }

        assertFailsWith<IllegalStateException> { toBeTestedIterator.next() }
    }

    @Test
    fun moreThanOneWorksAsExpected() {
        assertFalse { moreThanOne(emptySequence<Nothing>()) }
        assertFalse { moreThanOne(sequenceOf(1)) }
        assertTrue { moreThanOne(sequenceOf(1, 2)) }
        assertTrue { moreThanOne(sequenceOf(1, 2, 3)) }
    }

    @Test
    fun moreThanOneWorksLazily() {
        assertTrue {
            moreThanOne(
                sequence {
                    yield(1)
                    yield(1)
                    fail("Should not evaluate entire sequence")
                }
            )
        }
    }

    @Test
    fun newSolveRequestSetsCorrectlyNewGoal() {
        val newGoal = Scope.empty { structOf("a", atomOf("ciao")) }

        val toBeTested = solveRequest.newSolveRequest(newGoal)

        assertEquals(newGoal.extractSignature(), toBeTested.signature)
        assertEquals(newGoal.args, toBeTested.arguments)
    }

    @Test
    fun newSolveRequestAddsCorrectlySubstitution() {
        val newSubstitution = Substitution.of("A", Atom.of("a"))

        val toBeTested = solveRequest.newSolveRequest(solveRequest.query, newSubstitution)

        assertEquals(newSubstitution, toBeTested.context.substitution)
    }

    @Test
    fun newSolveRequestPropagatesCorrectlyContextFields() {
        val aClause = Clause.of(Atom.of("ddd"), Atom.of("ccc"))
        val modifiedContext = aContext.copy(
            dynamicKb = Theory.empty(),
            staticKb = aContext.staticKb.assertA(aClause),
            flags = FlagStore.of("someFlag" to Atom.of("someFlagValue")),
            libraries = Runtime.empty()
        )

        val toBeTested = solveRequest.newSolveRequest(solveRequest.query, toPropagateContextData = modifiedContext)

        assertEquals(modifiedContext.dynamicKb, toBeTested.context.dynamicKb)
        assertEquals(modifiedContext.staticKb, toBeTested.context.staticKb)
        assertEquals(modifiedContext.flags, toBeTested.context.flags)
        assertEquals(modifiedContext.libraries, toBeTested.context.libraries)
    }

    @Test
    fun newSolveRequestSetsCorrectlyRequestIssuingInstantRequestIssuingInstant() {
        val myCurrentTime = currentTimeInstant()
        val toBeTested = solveRequest.newSolveRequest(Truth.FAIL, requestIssuingInstant = myCurrentTime)

        assertEquals(myCurrentTime, toBeTested.startTime)
    }

    @Test
    fun replyWithSelectsCorrectlyTheTypeOfResponseToApply() {
        val expectedSubstitution = Substitution.of("A", Atom.of("a"))
        val expectedSideEffectsManager = SideEffectManagerImpl()
        val expectedException = HaltException(context = DummyInstances.executionContext)

        val aQuery = Atom.of("ciao")

        val aYesResponse =
            Solve.Response(aQuery.yes(expectedSubstitution), sideEffectManager = expectedSideEffectsManager)
        val aNoResponse = Solve.Response(aQuery.no(), sideEffectManager = expectedSideEffectsManager)
        val anHaltResponse =
            Solve.Response(aQuery.halt(expectedException), sideEffectManager = expectedSideEffectsManager)

        val underTestResponses = listOf(aYesResponse, aNoResponse, anHaltResponse)

        val correct = with(solveRequest) {
            listOf(
                replySuccess(expectedSubstitution, sideEffectManager = expectedSideEffectsManager),
                replyFail(sideEffectManager = expectedSideEffectsManager),
                replyException(expectedException, sideEffectManager = expectedSideEffectsManager)
            )
        }
        val toBeTested = underTestResponses.map { solveRequest.replyWith(it) }

        correct.zip(toBeTested).forEach { (expected, actual) ->
            assertEquals(expected.solution, actual.solution)
            assertEquals(expected.sideEffectManager, actual.sideEffectManager)
        }
    }

    @Test
    fun replyWithIfNoSideEffectManagerPresentInProvidedForwardedResponseDefaultsToRequestsSideEffectManager() {
        val aNoResponseWithoutSideEffectManager = Solve.Response(Atom.of("ciao").no())

        val toBeTested = solveRequest.replyWith(aNoResponseWithoutSideEffectManager)

        assertEquals(solveRequest.context.sideEffectManager, toBeTested.sideEffectManager)
    }
}
