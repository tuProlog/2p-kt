package it.unibo.tuprolog.solve.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.statemachine.TimeDuration
import it.unibo.tuprolog.solve.solver.statemachine.TimeInstant
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.assertNotEquals
import kotlin.collections.List as KtList

/**
 * Utils singleton to help testing [Solve]
 *
 * @author Enrico
 */
internal object SolveUtils {

    internal val someLibraries = Libraries()
    internal val someFlags = emptyMap<Atom, Term>()
    internal val aStaticKB = ClauseDatabase.empty()
    internal val aDynamicKB = ClauseDatabase.empty()

    // Request parameters
    internal val aSignature = Signature("ciao", 2)
    internal val anArgumentList = listOf(Atom.of("a"), Truth.`true`())
    internal val anInitialUserQuery = Struct.of("ciao", Var.anonymous())
    internal val anExecutionContext = ExecutionContextImpl(someLibraries, someFlags, aStaticKB, aDynamicKB)
    internal const val aRequestIssuingInstant = 0L
    internal const val anExecutionMaxDuration = 300L

    internal val aVarargSignature = Signature("ciao", 2, true)
    internal val varargArgumentList = anArgumentList + Truth.`true`()

    internal val differentSignature by lazy { aSignature.copy(name = "myName").also { assertNotEquals(it, aSignature) } }
    internal val differentArgumentList by lazy { (anArgumentList.dropLast(1) + Truth.fail()).also { assertNotEquals(it, anArgumentList) } }
    internal val differentInitialUserQuery by lazy { Truth.`true`().also { assertNotEquals(it, anInitialUserQuery) } }
    internal val differentLibraries by lazy { Libraries(Library.of(alias = "test")).also { assertNotEquals(it, someLibraries) } }
    internal val differentFlags by lazy { mapOf<Atom, Term>(Truth.`true`() to Truth.fail()).also { assertNotEquals(it, someFlags) } }
    internal val differentStaticKB by lazy { ClauseDatabase.of(Fact.of(anInitialUserQuery)).also { assertNotEquals(it, aStaticKB) } }
    internal val differentDynamicKB by lazy { ClauseDatabase.of(Fact.of(anInitialUserQuery)).also { assertNotEquals(it, aDynamicKB) } }
    internal val differentExecutionContext by lazy { ExecutionContextImpl(differentLibraries).also { assertNotEquals(it, anExecutionContext) } }
    internal val differentIssuingInstant by lazy { 10L.also { assertNotEquals(it, aRequestIssuingInstant) } }
    internal val differentMaxDuration by lazy { 20L.also { assertNotEquals(it, anExecutionMaxDuration) } }

    internal val solutionSubstitution = Substitution.of("A", Truth.`true`())
    internal val solutionException = HaltException(context = DummyInstances.executionContext)

    // Response parameters
    internal val aSolution = Solution.No(Truth.fail())

    /** The success response to default values request */
    internal val defaultRequestSuccessResponse by lazy {
        Solve.Response(Solution.Yes(aSignature, anArgumentList, solutionSubstitution), differentLibraries, differentFlags, differentStaticKB, differentDynamicKB)
    }

    /** The failed response to default values request */
    internal val defaultRequestFailedResponse by lazy {
        Solve.Response(Solution.No(aSignature, anArgumentList), differentLibraries, differentFlags, differentStaticKB, differentDynamicKB)
    }

    /** The halt response to default values request */
    internal val defaultRequestHaltedResponse by lazy {
        Solve.Response(Solution.Halt(aSignature, anArgumentList, solutionException), differentLibraries, differentFlags, differentStaticKB, differentDynamicKB)
    }

    /** The various responses to default request */
    internal val defaultRequestResponses by lazy {
        listOf(defaultRequestSuccessResponse, defaultRequestFailedResponse, defaultRequestHaltedResponse)
    }

    /** Utility function to create a request with some default values */
    internal fun createRequest(signature: Signature = aSignature, arguments: KtList<Term> = anArgumentList, initialUserQuery: Struct = anInitialUserQuery, executionContext: ExecutionContext = anExecutionContext, requestIssuingInstant: TimeInstant = aRequestIssuingInstant, executionMaxDuration: TimeDuration = anExecutionMaxDuration) =
            Solve.Request(signature, arguments, initialUserQuery, executionContext, requestIssuingInstant, executionMaxDuration)

}
