package it.unibo.tuprolog.solve.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
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
    internal val anArgumentList = listOf(Atom.of("a"), Truth.ofTrue())
    internal val anExecutionContext = DummyInstances.executionContext
    internal const val aRequestIssuingInstant = 0L
    internal const val anExecutionMaxDuration = 300L

    internal val aVarargSignature = Signature("ciao", 2, true)
    internal val varargArgumentList = anArgumentList + Truth.ofTrue()

    internal val differentLibraries by lazy {
        Libraries(Library.of(alias = "test")).also { assertNotEquals(it, someLibraries) }
    }
    internal val differentFlags by lazy {
        mapOf<Atom, Term>(Truth.ofTrue() to Truth.ofFalse()).also { assertNotEquals(it, someFlags) }
    }
    internal val differentStaticKB by lazy {
        ClauseDatabase.of(Fact.of(Truth.ofTrue())).also { assertNotEquals(it, aStaticKB) }
    }
    internal val differentDynamicKB by lazy {
        ClauseDatabase.of(Fact.of(Truth.ofTrue())).also { assertNotEquals(it, aDynamicKB) }
    }

    internal val solutionSubstitution = Substitution.of("A", Truth.ofTrue())
    internal val solutionException = TuPrologRuntimeException(context = DummyInstances.executionContext)

    // Response parameters
    internal val aSolution = Solution.No(Truth.ofFalse())
    internal val aSideEffectManager = object : SideEffectManager {
        override fun cut() = throw NotImplementedError()
    }

    /** The success response to default values request */
    internal val defaultRequestSuccessResponse by lazy {
        Solve.Response(
            Solution.Yes(aSignature, anArgumentList, solutionSubstitution),
            differentLibraries,
            differentFlags,
            differentStaticKB,
            differentDynamicKB,
            aSideEffectManager
        )
    }

    /** The failed response to default values request */
    internal val defaultRequestFailedResponse by lazy {
        Solve.Response(
            Solution.No(aSignature, anArgumentList),
            differentLibraries,
            differentFlags,
            differentStaticKB,
            differentDynamicKB,
            aSideEffectManager
        )
    }

    /** The halt response to default values request */
    internal val defaultRequestHaltedResponse by lazy {
        Solve.Response(
            Solution.Halt(aSignature, anArgumentList, solutionException),
            differentLibraries,
            differentFlags,
            differentStaticKB,
            differentDynamicKB,
            aSideEffectManager
        )
    }

    /** The various responses to default request */
    internal val defaultRequestResponses by lazy {
        listOf(defaultRequestSuccessResponse, defaultRequestFailedResponse, defaultRequestHaltedResponse)
    }

    /** Utility function to create a request with some default values */
    internal fun createRequest(
        signature: Signature = aSignature,
        arguments: KtList<Term> = anArgumentList,
        executionContext: ExecutionContext = anExecutionContext,
        requestIssuingInstant: TimeInstant = aRequestIssuingInstant,
        executionMaxDuration: TimeDuration = anExecutionMaxDuration
    ) = Solve.Request(signature, arguments, executionContext, requestIssuingInstant, executionMaxDuration)

}
