package it.unibo.tuprolog.solve.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.TimeInstant
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.solve.sideffects.SideEffectManager
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertNotEquals
import kotlin.collections.List as KtList

/**
 * Utils singleton to help testing [Solve]
 *
 * @author Enrico
 */
internal object SolveUtils {

    internal val someLibraries = Runtime.empty()
    internal val someFlags = emptyMap<String, Term>()
    internal val aStaticKB = Theory.empty()
    internal val aDynamicKB = Theory.empty()

    // Request parameters
    internal val aSignature = Signature("ciao", 2)
    internal val anArgumentList = listOf(Atom.of("a"), Truth.TRUE)
    internal val anExecutionContext = DummyInstances.executionContext
    internal const val aRequestIssuingInstant = 0L
    internal const val anExecutionMaxDuration = 300L

    internal val aVarargSignature = Signature("ciao", 2, true)
    internal val varargArgumentList = anArgumentList + Truth.TRUE

    internal val differentLibraries by lazy {
        Runtime.of(Library.of(alias = "test")).also { assertNotEquals(it, someLibraries) }
    }
    internal val differentFlags by lazy {
        mapOf<String, Term>(Truth.TRUE.value to Truth.FAIL).also { assertNotEquals(it, someFlags) }
    }
    internal val differentStaticKB by lazy {
        Theory.of(Fact.of(Truth.TRUE)).also { assertNotEquals(it, aStaticKB) }
    }
    internal val differentDynamicKB by lazy {
        Theory.of(Fact.of(Truth.TRUE)).also { assertNotEquals(it, aDynamicKB) }
    }

    internal val solutionSubstitution = Substitution.of("A", Truth.TRUE)
    internal val solutionException = ResolutionException(context = DummyInstances.executionContext)

    // Response parameters
    internal val aSolution = Solution.no(Truth.FAIL)
    internal val aSideEffectManager = object : SideEffectManager {
        override fun cut() = throw NotImplementedError()
    }
    internal val someSideEffects = listOf<SideEffect>(SideEffect.ResetDynamicKb())

    /** The success response to default values request */
    internal val defaultRequestSuccessResponse by lazy {
        Solve.Response(
            Solution.yes(aSignature, anArgumentList, solutionSubstitution),
            aSideEffectManager,
            SideEffect.ResetRuntime(differentLibraries),
            SideEffect.ResetFlags(differentFlags),
            SideEffect.ResetStaticKb(differentStaticKB),
            SideEffect.ResetDynamicKb(differentDynamicKB)
        )
    }

    /** The failed response to default values request */
    internal val defaultRequestFailedResponse by lazy {
        Solve.Response(
            Solution.no(aSignature, anArgumentList),
            aSideEffectManager,
            SideEffect.ResetRuntime(differentLibraries),
            SideEffect.ResetFlags(differentFlags),
            SideEffect.ResetStaticKb(differentStaticKB),
            SideEffect.ResetDynamicKb(differentDynamicKB)
        )
    }

    /** The halt response to default values request */
    internal val defaultRequestHaltedResponse by lazy {
        Solve.Response(
            Solution.halt(aSignature, anArgumentList, solutionException),
            aSideEffectManager,
            SideEffect.ResetRuntime(differentLibraries),
            SideEffect.ResetFlags(differentFlags),
            SideEffect.ResetStaticKb(differentStaticKB),
            SideEffect.ResetDynamicKb(differentDynamicKB)
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
