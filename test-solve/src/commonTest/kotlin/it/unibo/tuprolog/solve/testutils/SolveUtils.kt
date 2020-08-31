package it.unibo.tuprolog.solve.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertNotEquals
import kotlin.collections.List as KtList

/**
 * Utils singleton to help testing [Solve]
 *
 * @author Enrico
 */
internal object SolveUtils {

    internal val someLibraries = Libraries()
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
        Libraries(Library.aliased(alias = "test")).also { assertNotEquals(it, someLibraries) }
    }
    internal val differentFlags by lazy {
        mapOf<String, Term>(Truth.TRUE.value to Truth.FAIL).also { assertNotEquals(it, someFlags) }
    }
    internal val differentStaticKB by lazy {
        Theory.indexedOf(Fact.of(Truth.TRUE)).also { assertNotEquals(it, aStaticKB) }
    }
    internal val differentDynamicKB by lazy {
        Theory.indexedOf(Fact.of(Truth.TRUE)).also { assertNotEquals(it, aDynamicKB) }
    }

    internal val solutionSubstitution = Substitution.of("A", Truth.TRUE)
    internal val solutionException = TuPrologRuntimeException(context = DummyInstances.executionContext)

    // Response parameters
    internal val aSolution = Solution.No(Truth.FAIL)
    internal val aSideEffectManager = object : SideEffectManager {
        override fun cut() = throw NotImplementedError()
    }
    internal val someSideEffects = listOf<SideEffect>(SideEffect.ResetDynamicKb())

    /** The success response to default values request */
    internal val defaultRequestSuccessResponse by lazy {
        Solve.Response(
            Solution.Yes(aSignature, anArgumentList, solutionSubstitution),
            aSideEffectManager,
            SideEffect.ResetLibraries(differentLibraries),
            SideEffect.ResetFlags(differentFlags),
            SideEffect.ResetStaticKb(differentStaticKB),
            SideEffect.ResetDynamicKb(differentDynamicKB)
        )
    }

    /** The failed response to default values request */
    internal val defaultRequestFailedResponse by lazy {
        Solve.Response(
            Solution.No(aSignature, anArgumentList),
            aSideEffectManager,
            SideEffect.ResetLibraries(differentLibraries),
            SideEffect.ResetFlags(differentFlags),
            SideEffect.ResetStaticKb(differentStaticKB),
            SideEffect.ResetDynamicKb(differentDynamicKB)
        )
    }

    /** The halt response to default values request */
    internal val defaultRequestHaltedResponse by lazy {
        Solve.Response(
            Solution.Halt(aSignature, anArgumentList, solutionException),
            aSideEffectManager,
            SideEffect.ResetLibraries(differentLibraries),
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
