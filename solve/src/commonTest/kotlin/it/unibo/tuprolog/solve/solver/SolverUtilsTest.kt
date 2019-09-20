package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.prologerror.SystemError
import it.unibo.tuprolog.solve.primitiveimpl.Throw
import it.unibo.tuprolog.solve.solver.SolverUtils.haltResponseBy
import it.unibo.tuprolog.solve.solver.SolverUtils.importingContextFrom
import it.unibo.tuprolog.solve.solver.SolverUtils.moreThanOne
import it.unibo.tuprolog.solve.solver.SolverUtils.newSolveRequest
import it.unibo.tuprolog.solve.solver.SolverUtils.newThrowSolveRequest
import it.unibo.tuprolog.solve.solver.SolverUtils.noResponseBy
import it.unibo.tuprolog.solve.solver.SolverUtils.responseBy
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
    fun newSolveRequestInjectsPassedParametersInOldRequest() {
        val newGoal = Struct.of("a", Atom.of("ciao"))
        val currentTime = 100L
        val newSubstitution = Substitution.of("A", Atom.of("a"))

        val toBeTested = DummyInstances.solveRequest.newSolveRequest(newGoal, currentTime = currentTime)
        val toBeTestedSubstitution = DummyInstances.solveRequest.newSolveRequest(newGoal, newSubstitution, currentTime = currentTime)

        assertEquals(newGoal.extractSignature(), toBeTested.signature)
        assertEquals(newGoal.argsList, toBeTested.arguments)
        assertEquals(listOf(DummyInstances.executionContext), toBeTested.context.clauseScopedParents.toList())
        assertEquals(listOf(DummyInstances.solveRequest), toBeTested.context.logicalParentRequests.toList())
        assertEquals(Substitution.empty(), toBeTested.context.currentSubstitution)

        assertEquals(newSubstitution, toBeTestedSubstitution.context.currentSubstitution)
    }

    @Test
    fun newSolveRequestContextParentsAreOrderedAsLastInFirstOut() {
        val intermediateRequest = DummyInstances.solveRequest.newSolveRequest(Truth.fail())
        val toBeTested = intermediateRequest.newSolveRequest(Truth.`true`())

        // precondition
        assertEquals(2, toBeTested.context.clauseScopedParents.count())

        assertEquals(intermediateRequest.context, toBeTested.context.clauseScopedParents.first())
        assertEquals(DummyInstances.solveRequest.context, toBeTested.context.clauseScopedParents.last())
    }

    @Test
    fun newSolveRequestSolveRequestParentsAreOrderedAsLastInFirstOut() {
        val intermediateRequest = DummyInstances.solveRequest.newSolveRequest(Truth.fail())
        val toBeTested = intermediateRequest.newSolveRequest(Truth.`true`())

        // precondition
        assertEquals(2, toBeTested.context.logicalParentRequests.count())

        assertEquals(intermediateRequest, toBeTested.context.logicalParentRequests.first())
        assertEquals(DummyInstances.solveRequest, toBeTested.context.logicalParentRequests.last())
    }

    @Test
    fun newSolveRequestSolveRequestLogicalParentsCannotBeDuplicated() {
        val intermediateRequest = DummyInstances.solveRequest.newSolveRequest(Truth.fail())
        val toBeTested = intermediateRequest.newSolveRequest(Truth.`true`(), logicalParentRequest = DummyInstances.solveRequest)

        assertEquals(1, toBeTested.context.logicalParentRequests.count())
        assertEquals(DummyInstances.solveRequest, toBeTested.context.logicalParentRequests.single())
    }

    @Test
    fun newSolveRequestSolveRequestLogicalParentsAreTruncatedIfNeeded() {
        val intermediateRequest = DummyInstances.solveRequest.newSolveRequest(Truth.fail()).newSolveRequest(Truth.fail())
        val toBeTested = intermediateRequest.newSolveRequest(Truth.`true`(), logicalParentRequest = DummyInstances.solveRequest)

        assertEquals(1, toBeTested.context.logicalParentRequests.count())
        assertEquals(DummyInstances.solveRequest, toBeTested.context.logicalParentRequests.single())
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
    fun newSolveRequestIsBasedOnProvidedContextIfPassed() {
        val myContext = DummyInstances.executionContext.copy(currentSubstitution = Substitution.of("HHH", Truth.fail()))
        val toBeTested = DummyInstances.solveRequest.newSolveRequest(Atom.of("a"), baseContext = myContext)

        assertSame(myContext, toBeTested.context.clauseScopedParents.first())
        assertEquals(myContext.currentSubstitution, toBeTested.context.currentSubstitution)
    }

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
    fun importingContextFromShouldImportSubstitutionCorrectly() {
        Scope.empty().where {
            val firstSolveRequest = with(DummyInstances.solveRequest) {
                copy(context = context.copy(currentSubstitution = Substitution.of(
                        varOf("A") to atomOf("a"),
                        varOf("C") to varOf("D")
                ) as Substitution.Unifier))
            }
            val secondSolveRequest = with(DummyInstances.solveRequest) {
                copy(context = context.copy(currentSubstitution = Substitution.of(
                        varOf("D"), varOf("C")
                )))
            }

            with(firstSolveRequest.importingContextFrom(secondSolveRequest)) {
                assertEquals(Substitution.of(
                        varOf("A") to atomOf("a"),
                        varOf("C") to varOf("D"),
                        varOf("D") to varOf("C")
                ), this.context.currentSubstitution)
            }

            with(secondSolveRequest.importingContextFrom(firstSolveRequest)) {
                assertEquals(Substitution.of(
                        varOf("D") to varOf("C"),
                        varOf("A") to atomOf("a"),
                        varOf("C") to varOf("D")
                ), this.context.currentSubstitution)
            }
        }
    }

    @Test
    fun importingContextFromShouldImportParentsCorrectly() {
        val anAlreadyPresentContext = DummyInstances.executionContext.copy()
        val anotherAlreadyPresentContext = DummyInstances.executionContext.copy()
        val startRequest = with(DummyInstances.solveRequest) {
            copy(context = context.copy(clauseScopedParents = sequenceOf(anAlreadyPresentContext)))
        }
        val toImportSubRequest = with(DummyInstances.solveRequest) {
            copy(context = context.copy(clauseScopedParents = sequenceOf(anotherAlreadyPresentContext, anAlreadyPresentContext)))
        }

        val toBeTested = startRequest.importingContextFrom(toImportSubRequest).context.clauseScopedParents.toList()
        assertEquals(toImportSubRequest.context.clauseScopedParents.toList(), toBeTested.take(2))
        assertEquals(startRequest.context.clauseScopedParents.toList(), toBeTested.drop(2).toList())
    }

    @Test
    fun importingContextFromShouldImportToCutContextsCorrectly() {
        val previouslyPresentContext = DummyInstances.solveRequest.context.copy()
        val request = with(DummyInstances.solveRequest) { copy(context = context.copy(toCutContextsParent = sequenceOf(previouslyPresentContext))) }
        val toImportContext = DummyInstances.solveRequest.context.copy()
        val toImport = with(DummyInstances.solveRequest) { copy(context = context.copy(toCutContextsParent = sequenceOf(toImportContext))) }

        val toBeTested = request.importingContextFrom(toImport)
        with(toBeTested.context) {
            assertEquals(1, toCutContextsParent.count())
            assertSame(toImportContext, toCutContextsParent.single())
        }
    }

    @Test
    fun importingContextFromShouldImportThrowRelatedCut() {
        val previouslyPresentContext = DummyInstances.solveRequest.context.copy()
        val request = with(DummyInstances.solveRequest) { copy(context = context.copy(throwRelatedToCutContextsParent = previouslyPresentContext)) }
        val toImportContext = DummyInstances.solveRequest.context.copy()
        val toImport = with(DummyInstances.solveRequest) { copy(context = context.copy(throwRelatedToCutContextsParent = toImportContext)) }

        val toBeTested = request.importingContextFrom(toImport)
        with(toBeTested.context) {
            assertSame(toImportContext, throwRelatedToCutContextsParent)
        }
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
    fun haltResponseByWorksAsExpected() {
        val finalContext = DummyInstances.executionContext.copy()
        val finalException = HaltException(context = finalContext)

        val responseToReuseContextAndSubstitution = Solve.Response(Solution.Halt(
                Signature("ciao", 0),
                emptyList(),
                finalException
        ), finalContext)

        val correct = with(DummyInstances.solveRequest) {
            Solve.Response(Solution.Halt(signature, arguments, finalException), finalContext)
        }
        val toBeTested = DummyInstances.solveRequest.haltResponseBy(responseToReuseContextAndSubstitution)

        assertEquals(correct.solution, toBeTested.solution)
        assertEquals(correct.context, toBeTested.context)
    }

    @Test
    fun responseBySelectCorrectlyTheTypeOfResponseToApply() {
        val finalSubstitution = Substitution.of("A", Atom.of("a"))
        val finalContext = DummyInstances.executionContext.copy(currentSubstitution = finalSubstitution)
        val finalException = HaltException(context = finalContext)

        val aYesResponse = Solve.Response(Solution.Yes(
                Signature("ciao", 0),
                emptyList(),
                finalSubstitution
        ), finalContext)
        val aNoResponse = Solve.Response(Solution.No(Signature("ciao", 0), emptyList()), finalContext)
        val anHaltResponse = Solve.Response(Solution.Halt(Signature("ciao", 0), emptyList(), finalException), finalContext)

        val underTestResponses = listOf(aYesResponse, aNoResponse, anHaltResponse)

        val correct = listOf(
                with(DummyInstances.solveRequest) {
                    Solve.Response(Solution.Yes(signature, arguments, finalSubstitution), finalContext)
                },
                with(DummyInstances.solveRequest) {
                    Solve.Response(Solution.No(signature, arguments), finalContext)
                },
                with(DummyInstances.solveRequest) {
                    Solve.Response(Solution.Halt(signature, arguments, finalException), finalContext)
                }
        )
        val toBeTested = underTestResponses.map { DummyInstances.solveRequest.responseBy(it) }

        correct.zip(toBeTested).forEach { (expected, actual) ->
            assertEquals(expected.solution, actual.solution)
            assertEquals(expected.context, actual.context)
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
