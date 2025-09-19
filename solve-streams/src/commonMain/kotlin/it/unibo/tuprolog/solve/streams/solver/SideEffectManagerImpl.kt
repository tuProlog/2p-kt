package it.unibo.tuprolog.solve.streams.solver

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffectManager
import it.unibo.tuprolog.solve.streams.StreamsSolver
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Call
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Catch
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Cut
import it.unibo.tuprolog.unify.Unificator.Companion.matches

/**
 * Specific implementation of [SideEffectManager] for [StreamsSolver]
 *
 * @author Enrico
 */
internal data class SideEffectManagerImpl(
    /** The sequence of parent execution contexts before this, limited to a "clause scope" */
    private val clauseScopedParents: List<StreamsExecutionContext> = emptyList(),
    /** Valued when this execution context is child of a choicePoint context, indicating a point where to cut */
    private val isChoicePointChild: Boolean = false,
    /** Filled when cut execution happens, this indicates which are the parent contexts whose unexplored children should be cut */
    private val toCutContextsParent: Sequence<StreamsExecutionContext> = emptySequence(),
    /** The sequence of parent [Solve.Request]s from this execution context till the resolution root */
    internal val logicalParentRequests: List<Solve.Request<StreamsExecutionContext>> = emptyList(),
    /** The sequence of no more selectable parent requests, because already used */
    private val throwNonSelectableParentContexts: Sequence<StreamsExecutionContext> = emptySequence(),
    /** The execution context where a `catch` was found and till which other unexplored sub-trees should be cut */
    private val throwRelatedToCutContextsParent: StreamsExecutionContext? = null,
) : SideEffectManager {
    override fun cut(): SideEffectManager =
        copy(
            // only second value could be enough, but more testing needed
            toCutContextsParent = toCutContextsParent + getFirstChoicePointContext(),
        )

    /** Initializes isChoicePointChild to `false` whatever it was, and adds given [currentContext] to clauseScopedParents */
    internal fun stateInitInitialize(currentContext: StreamsExecutionContext): SideEffectManagerImpl =
        copy(
            clauseScopedParents = clauseScopedParents.toMutableList().apply { add(0, currentContext) },
            isChoicePointChild = false,
        )

    /**
     * Method that updates internal structures when creating a new solve request
     *
     * @param currentContext the context current before new solve request creation
     * @param isChoicePointChild whether this solve request is child of a ChoicePoint
     * @param logicalParentRequest the Solve.Request to be considered logically the parent of this (to correctly implement `catch/throw` interaction)
     */
    internal fun creatingNewRequest(
        currentContext: StreamsExecutionContext,
        isChoicePointChild: Boolean,
        logicalParentRequest: Solve.Request<StreamsExecutionContext>,
    ) = copy(
        clauseScopedParents = clauseScopedParents.toMutableList().apply { add(0, currentContext) },
        isChoicePointChild = isChoicePointChild,
        logicalParentRequests =
            when (logicalParentRequest) {
                in logicalParentRequests -> logicalParentRequests.dropWhile { it != logicalParentRequest }
                else -> logicalParentRequests.toMutableList().apply { add(0, logicalParentRequest) }
            },
    )

    /** Method that updates sideEffects manager to not consider parents older than current first, because entering new "rule-scope" */
    internal fun enterRuleSubScope() =
        copy(
            clauseScopedParents = listOf(clauseScopedParents.first()),
        )

    /** Method that updates clauseScopedParent to include upper scope parents; this is needed to maintain Cut functionality through Response chain */
    internal fun extendParentScopeWith(upperScopeSideEffectsManager: SideEffectManagerImpl) =
        copy(
            clauseScopedParents =
                clauseScopedParents
                    .toMutableList()
                    .apply { addAll(upperScopeSideEffectsManager.clauseScopedParents) },
        )

    /**
     * Method that queries if a Cut should be executed in StateRuleSelection
     *
     * A cut should execute:
     * - if throw was called, and not already caught
     * - if cut was called, and the first "scoped" choice point context is to be cut
     */
    internal fun shouldCutExecuteInRuleSelection() =
        clauseScopedParents.lastOrNull() in toCutContextsParent ||
            shouldExecuteThrowCut()

    /** A method to retrieve the first ancestor catch request that has its second argument that unifies with [toUnifyArgument] */
    internal fun retrieveAncestorCatchRequest(toUnifyArgument: Term): Solve.Request<StreamsExecutionContext>? {
        val ancestorCatchesRequests =
            logicalParentRequests
                .filter { it.signature == Catch.signature }
                .filterNot { it.context in throwNonSelectableParentContexts } // exclude already used catch requests

        return ancestorCatchesRequests.find { it.arguments[1].matches(toUnifyArgument) }
    }

    /** Method to set the catch request whose not explored children should be cut from search tree */
    internal fun throwCut(ancestorCatchContext: StreamsExecutionContext) =
        copy(
            throwRelatedToCutContextsParent = ancestorCatchContext,
        )

    /** Method to query if provided context was that selected by [throwCut] invocation */
    internal fun isSelectedThrowCatch(contextImpl: StreamsExecutionContext) =
        throwRelatedToCutContextsParent == contextImpl

    /** Method to remove catch request with that [contextImpl], ensuring that's no more selectable */
    internal fun ensureNoMoreSelectableCatch(contextImpl: StreamsExecutionContext) =
        copy(
            throwNonSelectableParentContexts = throwNonSelectableParentContexts + contextImpl,
        )

    /**
     * Utility method to reset [Cut] changed data structures to initial value before exiting [Call] scope
     *
     * That implements expected ISO behaviour, for which *call/1 is said to be opaque (or not transparent) to cut.*
     */
    internal fun resetCutWorkChanges(toRecoverSituation: SideEffectManagerImpl) =
        // opaque behaviour of call/1 w.r.t cut/0, results in cancellation of sub-goal work onto "cut"'s data structures
        copy(toCutContextsParent = toRecoverSituation.toCutContextsParent)

    /**
     * Method to query if the throw Cut should execute; the throw cut can exit clause Scope, so it uses different data structures
     */
    internal fun shouldExecuteThrowCut() = logicalParentRequests.any { it.context == throwRelatedToCutContextsParent }

    /** Internal function to retrieve the first choice point to be Cut, if present */
    private fun getFirstChoicePointContext() =
        clauseScopedParents
            .filter { it.sideEffectManager.isChoicePointChild }
            .mapNotNull { it.sideEffectManager.clauseScopedParents.firstOrNull() }
}

/** Bridge method to reach [SideEffectManagerImpl.shouldCutExecuteInRuleSelection] homonym method from a [SideEffectManager] */
internal fun SideEffectManager?.shouldCutExecuteInRuleSelection(): Boolean =
    (this as? SideEffectManagerImpl)?.shouldCutExecuteInRuleSelection() ?: false

/** Bridge method to reach [SideEffectManagerImpl.extendParentScopeWith] homonym method from a [SideEffectManager] */
internal fun SideEffectManager?.extendParentScopeWith(
    upperScopeSideEffectsManager: SideEffectManagerImpl,
): SideEffectManagerImpl? = (this as? SideEffectManagerImpl)?.extendParentScopeWith(upperScopeSideEffectsManager)

/** Bridge method to reach [SideEffectManagerImpl.resetCutWorkChanges] homonym method from a [SideEffectManager] */
internal fun SideEffectManager?.resetCutWorkChanges(toRecoverSituation: SideEffectManagerImpl): SideEffectManagerImpl? =
    (this as? SideEffectManagerImpl)?.resetCutWorkChanges(toRecoverSituation)

/** Bridge method to reach [SideEffectManagerImpl.isSelectedThrowCatch] homonym method from a [SideEffectManager] */
internal fun SideEffectManager?.isSelectedThrowCatch(context: StreamsExecutionContext): Boolean =
    (this as? SideEffectManagerImpl)?.isSelectedThrowCatch(context) ?: false

/** Bridge method to reach [SideEffectManagerImpl.shouldExecuteThrowCut] homonym method from a [SideEffectManager] */
internal fun SideEffectManager?.shouldExecuteThrowCut(): Boolean =
    (this as? SideEffectManagerImpl)?.shouldExecuteThrowCut() ?: false
