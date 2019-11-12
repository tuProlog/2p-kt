package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.exception.HaltException
import kotlin.test.*

/**
 * Test class for SolverUtils.kt
 *
 * @author Enrico
 */
internal class SolverUtilsTest {

    private val aContext = ExecutionContextImpl()

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
        val toBeTested = testSequence.orderWithStrategy(aContext) { seq, _ -> seq.min()!! }

        assertEquals(testSequence.sorted().toList(), toBeTested.toList())
    }

    @Test
    fun orderWithStrategyDoesntRemoveDuplicatedItems() {
        val testSequence = sequenceOf(1, 5, 2, 9, 3, 0, 5)
        val toBeTested = testSequence.orderWithStrategy(aContext) { seq, _ -> seq.max()!! }

        assertEquals(testSequence.sortedDescending().toList(), toBeTested.toList())
    }

    @Test
    fun orderWithStrategyWorksLazily() {
        val testSequence = sequenceOf({ 1 }, { 5 }, { 2 }, { 9 }, { 3 }, { 0 }, { 5 }, { throw IllegalStateException() })
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
            moreThanOne(sequence {
                yield(1)
                yield(1)
                fail("Should not evaluate entire sequence")
            })
        }
    }

    @Test
    fun newSolveRequestSetsCorrectlyNewGoal() {
        val newGoal = Scope.empty { structOf("a", atomOf("ciao")) }

        val toBeTested = solveRequest.newSolveRequest(newGoal)

        assertEquals(newGoal.extractSignature(), toBeTested.signature)
        assertEquals(newGoal.argsList, toBeTested.arguments)
    }

    @Test
    fun newSolveRequestAddsCorrectlySubstitution() {
        val newSubstitution = Substitution.of("A", Atom.of("a"))

        val toBeTested = solveRequest.newSolveRequest(solveRequest.query, newSubstitution)

        assertEquals(newSubstitution, toBeTested.context.substitution)
    }

    @Test
    fun newSolveRequestSetsCorrectlyRequestIssuingInstantRequestIssuingInstant() {
        val myCurrentTime = currentTimeInstant()
        val toBeTested = solveRequest.newSolveRequest(Truth.fail(), currentTime = myCurrentTime)

        assertEquals(myCurrentTime, toBeTested.requestIssuingInstant)
    }

    @Test
    fun newSolveRequestAdjustsCurrentTimeoutIfNotMaxValue() {
        val initialStartTime = 40L
        val initialTimeout = 200L
        val startSolveRequest = with(solveRequest) {
            copy(executionMaxDuration = initialTimeout, requestIssuingInstant = initialStartTime)
        }

        val currentTimeLow = 80L
        val toBeTested = startSolveRequest.newSolveRequest(Atom.of("a"), currentTime = currentTimeLow)

        assertEquals(initialTimeout - (currentTimeLow - initialStartTime), toBeTested.executionMaxDuration)

        val currentTimeHigh = 350L
        val toBeTestedZeroTimeout = startSolveRequest.newSolveRequest(Atom.of("a"), currentTime = currentTimeHigh)

        assertEquals(0L, toBeTestedZeroTimeout.executionMaxDuration)
    }

    @Test
    fun newSolveRequestDoesntAdjustTimeOutIfMaxValue() {
        val toBeTested = solveRequest.copy(executionMaxDuration = TimeDuration.MAX_VALUE)
                .newSolveRequest(Atom.of("a"), currentTime = 100L)

        assertEquals(TimeDuration.MAX_VALUE, toBeTested.executionMaxDuration)
    }

    @Test
    fun replyWithSelectsCorrectlyTheTypeOfResponseToApply() {
        val expectedSubstitution = Substitution.of("A", Atom.of("a"))
        val expectedSideEffectsManager = SideEffectManagerImpl()
        val expectedException = HaltException(context = DummyInstances.executionContext)

        val aQuery = Atom.of("ciao")

        val aYesResponse = Solve.Response(aQuery.yes(expectedSubstitution), sideEffectManager = expectedSideEffectsManager)
        val aNoResponse = Solve.Response(aQuery.no(), sideEffectManager = expectedSideEffectsManager)
        val anHaltResponse = Solve.Response(aQuery.halt(expectedException), sideEffectManager = expectedSideEffectsManager)

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
