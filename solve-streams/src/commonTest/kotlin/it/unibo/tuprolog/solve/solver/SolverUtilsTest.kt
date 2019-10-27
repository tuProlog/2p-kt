package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.prologerror.SystemError
import it.unibo.tuprolog.libraries.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.solver.SolverUtils.moreThanOne
import it.unibo.tuprolog.solve.solver.SolverUtils.newSolveRequest
import it.unibo.tuprolog.solve.solver.SolverUtils.newThrowSolveRequest
import it.unibo.tuprolog.solve.solver.SolverUtils.responseBy
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.*

/**
 * Test class for [SolverUtils]
 *
 * @author Enrico
 */
internal class SolverUtilsTest {

    @Test
    fun orderWithStrategyWithEmptyElementsDoesNothing() {
        assertEquals(
                emptySequence<Nothing>().toList(),
                SolverUtils.orderedWithStrategy(emptySequence<Nothing>(), DummyInstances.executionContext) { seq, _ -> seq.first() }.toList()
        )
    }

    @Test
    fun orderWithStrategyPassesCorrectlyParametersToSelectionFunction() {
        SolverUtils.orderedWithStrategy(emptySequence<Unit>(), DummyInstances.executionContext) { sequence, context ->
            assertEquals(emptySequence(), sequence)
            assertEquals(DummyInstances.executionContext, context)
        }
    }

    @Test
    fun orderWithStrategyAppliesCorrectlySelectionStrategy() {
        val testSequence = sequenceOf(1, 5, 2, 9, 3, 0, 55)
        val toBeTested = SolverUtils.orderedWithStrategy(testSequence, DummyInstances.executionContext) { seq, _ -> seq.min()!! }

        assertEquals(testSequence.sorted().toList(), toBeTested.toList())
    }

    @Test
    fun orderWithStrategyDoesntRemoveDuplicatedItems() {
        val testSequence = sequenceOf(1, 5, 2, 9, 3, 0, 5)
        val toBeTested = SolverUtils.orderedWithStrategy(testSequence, DummyInstances.executionContext) { seq, _ -> seq.max()!! }

        assertEquals(testSequence.sortedDescending().toList(), toBeTested.toList())
    }

    @Test
    fun orderWithStrategyWorksLazily() {
        val testSequence = sequenceOf({ 1 }, { 5 }, { 2 }, { 9 }, { 3 }, { 0 }, { 5 }, { throw IllegalStateException() })
        val testSeqElemCount = testSequence.count()

        val toBeTested = SolverUtils.orderedWithStrategy(testSequence, DummyInstances.executionContext) { seq, _ ->
            seq.first().also { it() }
        }
        val testSeqIterator = testSequence.iterator()
        val toBeTestedIterator = toBeTested.iterator()

        repeat(testSeqElemCount - 1) { assertSame(testSeqIterator.next(), toBeTestedIterator.next()) }

        assertFailsWith<IllegalStateException> { toBeTestedIterator.next() }
    }

    @Test
    fun isWellFormedWorksAsExpected() {
        val wellFormedGoal = Struct.of("a", Integer.of(2))
        val nonWellFormedGoal = Tuple.of(Atom.of("a"), Integer.of(3))

        assertEquals(SolverUtils.isWellFormed(wellFormedGoal), wellFormedGoal.accept(Clause.bodyWellFormedVisitor))
        assertEquals(SolverUtils.isWellFormed(nonWellFormedGoal), nonWellFormedGoal.accept(Clause.bodyWellFormedVisitor))
    }

    @Test
    fun prepareForExecutionWorksAsExpected() {
        val aVar = Var.of("A")
        val bVar = Var.of("B")
        val correct = Tuple.of(Struct.of("call", aVar), Struct.of("call", bVar))

        val toBeTested1 = SolverUtils.prepareForExecution(Tuple.of(aVar, bVar))
        val toBeTested2 = SolverUtils.prepareForExecution(SolverUtils.prepareForExecution(Tuple.of(aVar, bVar)))

        assertEquals(correct, toBeTested1)
        assertEquals(correct, toBeTested2)
    }

    @Test
    fun reduceAndFilterSubstitution() {
        Scope.empty().where {
            val startSubstitution = Substitution.of(
                    varOf("A") to varOf("C"),
                    varOf("C") to atomOf("c"),
                    varOf("D") to atomOf("d")
            )
            val toBeTested = SolverUtils.reduceAndFilterSubstitution(startSubstitution, sequenceOf(varOf("A")))

            assertEquals(Substitution.of(varOf("A"), atomOf("c")), toBeTested)
        }
    }

    @Test
    fun reduceAndFilterSubstitutionWithNullVariableSequenceReturnsAllSubstitution() {
        Scope.empty().where {
            val startSubstitution = Substitution.of(
                    varOf("A") to varOf("C"),
                    varOf("C") to Atom.of("c"),
                    varOf("D") to Atom.of("d")
            )
            val toBeTested = SolverUtils.reduceAndFilterSubstitution(startSubstitution)

            val correct = Substitution.of(
                    varOf("A") to atomOf("c"),
                    varOf("C") to atomOf("c"),
                    varOf("D") to atomOf("d")
            )
            assertEquals(correct, toBeTested)
        }
    }

    @Test
    fun newSolveRequestInjectsPassedParametersInOldRequest() {
        val newGoal = Struct.of("a", Atom.of("ciao"))
        val currentTime = 100L
        val newSubstitution = Substitution.of("A", Atom.of("a"))

        val toBeTested = DummyInstances.solveRequest.newSolveRequest(newGoal, currentTime = currentTime)
        val toBeTestedSubstitution = DummyInstances.solveRequest.newSolveRequest(newGoal, newSubstitution, currentTime = currentTime)

        assertEquals(newGoal.extractSignature(), toBeTested.signature)
        assertEquals(newGoal.argsList, toBeTested.arguments)
//        assertEquals(listOf(DummyInstances.executionContext), toBeTested.context.clauseScopedParents.toList())
//        assertEquals(listOf(DummyInstances.solveRequest), toBeTested.context.logicalParentRequests.toList())
        assertEquals(Substitution.empty(), toBeTested.context.substitution)

        assertEquals(newSubstitution, toBeTestedSubstitution.context.substitution)
    }

//    @Test
//    fun newSolveRequestContextParentsAreOrderedAsLastInFirstOut() {
//        val intermediateRequest = DummyInstances.solveRequest.newSolveRequest(Truth.fail())
//        val toBeTested = intermediateRequest.newSolveRequest(Truth.`true`())
//
//        // precondition
//        assertEquals(2, toBeTested.context.clauseScopedParents.count())
//
//        assertEquals(intermediateRequest.context, toBeTested.context.clauseScopedParents.first())
//        assertEquals(DummyInstances.solveRequest.context, toBeTested.context.clauseScopedParents.last())
//    }

//    @Test
//    fun newSolveRequestSolveRequestParentsAreOrderedAsLastInFirstOut() {
//        val intermediateRequest = DummyInstances.solveRequest.newSolveRequest(Truth.fail())
//        val toBeTested = intermediateRequest.newSolveRequest(Truth.`true`())
//
//        // precondition
//        assertEquals(2, toBeTested.context.logicalParentRequests.count())
//
//        assertEquals(intermediateRequest, toBeTested.context.logicalParentRequests.first())
//        assertEquals(DummyInstances.solveRequest, toBeTested.context.logicalParentRequests.last())
//    }

//    @Test
//    fun newSolveRequestSolveRequestLogicalParentsCannotBeDuplicated() {
//        val intermediateRequest = DummyInstances.solveRequest.newSolveRequest(Truth.fail())
//        val toBeTested = intermediateRequest.newSolveRequest(Truth.`true`(), logicalParentRequest = DummyInstances.solveRequest)
//
//        assertEquals(1, toBeTested.context.logicalParentRequests.count())
//        assertEquals(DummyInstances.solveRequest, toBeTested.context.logicalParentRequests.single())
//    }

//    @Test
//    fun newSolveRequestSolveRequestLogicalParentsAreTruncatedIfNeeded() {
//        val intermediateRequest = DummyInstances.solveRequest.newSolveRequest(Truth.fail()).newSolveRequest(Truth.fail())
//        val toBeTested = intermediateRequest.newSolveRequest(Truth.`true`(), logicalParentRequest = DummyInstances.solveRequest)
//
//        assertEquals(1, toBeTested.context.logicalParentRequests.count())
//        assertEquals(DummyInstances.solveRequest, toBeTested.context.logicalParentRequests.single())
//    }

    @Test
    fun newSolveRequestAdjustsCurrentTimeoutIfNotMaxValue() {
        val initialStartTime = 40L
        val initialTimeout = 200L
        val startSolveRequest = with(DummyInstances.solveRequest) {
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
        val toBeTested = DummyInstances.solveRequest.copy(executionMaxDuration = TimeDuration.MAX_VALUE)
                .newSolveRequest(Atom.of("a"), currentTime = 100L)

        assertEquals(TimeDuration.MAX_VALUE, toBeTested.executionMaxDuration)
    }

//    @Test
//    fun newSolveRequestSetsNewContextIsChoicePointFieldToPassedValueOrFalseByDefault() {
//        assertTrue {
//            DummyInstances.solveRequest.newSolveRequest(Atom.of("a"), isChoicePointChild = true)
//                    .context.isChoicePointChild
//        }
//        assertFalse { DummyInstances.solveRequest.newSolveRequest(Atom.of("a")).context.isChoicePointChild }
//
//        val startsTrueChoicePointChildField = with(DummyInstances.solveRequest) { copy(context = context.copy(isChoicePointChild = true)) }
//        assertFalse { startsTrueChoicePointChildField.newSolveRequest(Atom.of("a")).context.isChoicePointChild }
//    }

//    @Test
//    fun newSolveRequestIsBasedOnProvidedContextIfPassed() {
//        val myContext = DummyInstances.executionContext.copy(substitution = Substitution.of("HHH", Truth.fail()))
//        val toBeTested = DummyInstances.solveRequest.newSolveRequest(Atom.of("a"), baseContext = myContext)
//
//        assertSame(myContext, toBeTested.context.clauseScopedParents.first())
//        assertEquals(myContext.substitution, toBeTested.context.substitution)
//    }

    @Test
    fun newThrowSolveRequestBehavesLikeNewSolveRequestAddingThrowGoal() {
        val testError = SystemError(context = DummyInstances.executionContext)

        val correct = DummyInstances.solveRequest.newSolveRequest(Struct.of(Throw.functor, testError.errorStruct))
        val toBeTested = DummyInstances.solveRequest.newThrowSolveRequest(testError)

        assertEquals(correct.signature, toBeTested.signature)
        assertEquals(correct.arguments, toBeTested.arguments)

        val correctWithExtra = DummyInstances.solveRequest.newSolveRequest(Struct.of(Throw.functor, testError.errorStruct))
        val toBeTestedWithExtra = DummyInstances.solveRequest.newThrowSolveRequest(testError)

        assertEquals(correctWithExtra.signature, toBeTestedWithExtra.signature)
        assertEquals(correctWithExtra.arguments, toBeTestedWithExtra.arguments)
    }

    @Test
    fun responseBySelectCorrectlyTheTypeOfResponseToApply() {
        val finalSubstitution = Substitution.of("A", Atom.of("a"))
        val finalSideEffectManager = SideEffectManagerImpl()
        val finalException = HaltException(context = DummyInstances.executionContext)

        val aYesResponse = Solve.Response(Solution.Yes(
                Signature("ciao", 0),
                emptyList(),
                finalSubstitution
        ), sideEffectManager = finalSideEffectManager)
        val aNoResponse = Solve.Response(Solution.No(Signature("ciao", 0), emptyList()), sideEffectManager = finalSideEffectManager)
        val anHaltResponse = Solve.Response(Solution.Halt(Signature("ciao", 0), emptyList(), finalException), sideEffectManager = finalSideEffectManager)

        val underTestResponses = listOf(aYesResponse, aNoResponse, anHaltResponse)

        val correct = listOf(
                with(DummyInstances.solveRequest) {
                    Solve.Response(Solution.Yes(signature, arguments, finalSubstitution), sideEffectManager = finalSideEffectManager)
                },
                with(DummyInstances.solveRequest) {
                    Solve.Response(Solution.No(signature, arguments), sideEffectManager = finalSideEffectManager)
                },
                with(DummyInstances.solveRequest) {
                    Solve.Response(Solution.Halt(signature, arguments, finalException), sideEffectManager = finalSideEffectManager)
                }
        )
        val toBeTested = underTestResponses.map { DummyInstances.solveRequest.responseBy(it) }

        correct.zip(toBeTested).forEach { (expected, actual) ->
            assertEquals(expected.solution, actual.solution)
            assertEquals(expected.sideEffectManager, actual.sideEffectManager)
        }
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
}
