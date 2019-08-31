package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverUtils.moreThanOne
import it.unibo.tuprolog.solve.solver.SolverUtils.newSolveRequest
import it.unibo.tuprolog.solve.solver.SolverUtils.noResponseBy
import it.unibo.tuprolog.solve.solver.SolverUtils.yesResponseBy
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
    fun orderWithStrategyRemovesDuplicatedItems() {
        val testSequence = sequenceOf(1, 5, 2, 9, 3, 0, 5)
        val toBeTested = SolverUtils.orderedWithStrategy(testSequence, DummyInstances.executionContext) { seq, _ -> seq.max()!! }

        assertEquals(testSequence.sortedDescending().distinct().toList(), toBeTested.toList())
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
    fun newSolveRequestInjectsPassedParametersInOldRequest() {
        val newGoal = Struct.of("a", Atom.of("ciao"))
        val currentTime = 100L
        val newSubstitution = Substitution.of("A", Atom.of("a"))

        val toBeTested = DummyInstances.solveRequest.newSolveRequest(newGoal, currentTime = currentTime)
        val toBeTestedSubstitution = DummyInstances.solveRequest.newSolveRequest(newGoal, newSubstitution, currentTime)

        assertEquals(Signature.fromIndicator(newGoal.indicator), toBeTested.signature)
        assertEquals(newGoal.argsList, toBeTested.arguments)
        assertEquals(listOf(DummyInstances.executionContext), toBeTested.context.parents.toList())
        assertEquals(Substitution.empty(), toBeTested.context.currentSubstitution)

        assertEquals(newSubstitution, toBeTestedSubstitution.context.currentSubstitution)
    }

    @Test
    fun newSolveRequestContextParentsAreOrderedAsLastInFirstOut() {
        val toBeTested = DummyInstances.solveRequest.newSolveRequest(Truth.fail()).newSolveRequest(Truth.`true`())

        // precondition
        assertEquals(2, toBeTested.context.parents.count())

        assertNotEquals(DummyInstances.solveRequest.context, toBeTested.context.parents.first())
        assertEquals(DummyInstances.solveRequest.context, toBeTested.context.parents.last())
    }

    @Test
    fun newSolveRequestAdjustsCurrentTimeoutIfNotMaxValue() {
        val initialStartTime = 40L
        val initialTimeout = 200L
        val startSolveRequest = with(DummyInstances.solveRequest) {
            copy(executionTimeout = initialTimeout, context = context.copy(computationStartTime = initialStartTime))
        }

        val currentTimeLow = 80L
        val toBeTested = startSolveRequest.newSolveRequest(Atom.of("a"), currentTime = currentTimeLow)

        assertEquals(initialTimeout - (currentTimeLow - initialStartTime), toBeTested.executionTimeout)

        val currentTimeHigh = 350L
        val toBeTestedZeroTimeout = startSolveRequest.newSolveRequest(Atom.of("a"), currentTime = currentTimeHigh)

        assertEquals(0L, toBeTestedZeroTimeout.executionTimeout)
    }

    @Test
    fun newSolveRequestDoesntAdjustTimeOutIfMaxValue() {
        val toBeTested = DummyInstances.solveRequest.copy(executionTimeout = Long.MAX_VALUE)
                .newSolveRequest(Atom.of("a"), currentTime = 100L)

        assertEquals(Long.MAX_VALUE, toBeTested.executionTimeout)
    }

    @Test
    fun newSolveRequestSetsNewContextIsChoicePointFieldToPassedValueOrFalseByDefault() {
        assertTrue {
            DummyInstances.solveRequest.newSolveRequest(Atom.of("a"), isChoicePointChild = true)
                    .context.isChoicePointChild
        }
        assertFalse { DummyInstances.solveRequest.newSolveRequest(Atom.of("a")).context.isChoicePointChild }

        val startsTrueChoicePointChildField = with(DummyInstances.solveRequest) { copy(context = context.copy(isChoicePointChild = true)) }
        assertFalse { startsTrueChoicePointChildField.newSolveRequest(Atom.of("a")).context.isChoicePointChild }
    }

    @Test
    fun yesResponseByCorrectSubstitutionResponseArgWorksAsExpected() {
        val finalSubstitution = Substitution.of("A", Atom.of("a"))
        val finalContext = DummyInstances.executionContext.copy(currentSubstitution = finalSubstitution)

        val responseToReuseContextAndSubstitution = Solve.Response(Solution.Yes(
                Signature("ciao", 0),
                emptyList(),
                finalSubstitution
        ), finalContext)

        val correct = with(DummyInstances.solveRequest) {
            Solve.Response(Solution.Yes(signature, arguments, finalSubstitution), finalContext)
        }
        val toBeTested = DummyInstances.solveRequest.yesResponseBy(responseToReuseContextAndSubstitution)

        assertEquals(correct.solution, toBeTested.solution)
        assertEquals(correct.context, toBeTested.context)
    }

    @Test
    fun yesResponseComplainsIFPassedResponseSubstitutionIsFailed() {
        val badResponse = with(DummyInstances.solveRequest) {
            Solve.Response(Solution.No(signature, arguments), DummyInstances.executionContext)
        }

        assertFailsWith<IllegalArgumentException> { DummyInstances.solveRequest.yesResponseBy(badResponse) }
    }

    @Test
    fun noResponseByWorksAsExpected() {
        val finalSubstitution = Substitution.of("A", Atom.of("a"))
        val finalContext = DummyInstances.executionContext.copy(currentSubstitution = finalSubstitution)

        val responseToReuseContextAndSubstitution = Solve.Response(Solution.Yes(
                Signature("ciao", 0),
                emptyList(),
                finalSubstitution
        ), finalContext)

        val correct = with(DummyInstances.solveRequest) {
            Solve.Response(Solution.No(signature, arguments), finalContext)
        }
        val toBeTested = DummyInstances.solveRequest.noResponseBy(responseToReuseContextAndSubstitution)

        assertEquals(correct.solution, toBeTested.solution)
        assertEquals(correct.context, toBeTested.context)
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
