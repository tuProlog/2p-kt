package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.stdlib.primitive.Call
import it.unibo.tuprolog.libraries.stdlib.primitive.Catch
import it.unibo.tuprolog.libraries.stdlib.primitive.Cut
import it.unibo.tuprolog.solve.SideEffectManager
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.SolverSLD
import it.unibo.tuprolog.unify.Unification.Companion.matches

/**
 * Specific implementation of [SideEffectManager] for [SolverSLD]
 *
 * @author Enrico
 */
internal data class SideEffectManagerImpl(

        // CUT side effect fields

        /** The sequence of parent execution contexts before this, limited to a "clause scope" */
        private val clauseScopedParents: Lazy<Sequence<ExecutionContextImpl>> = lazyOf(emptySequence()),
        /** Valued when this execution context is child of a choicePoint context, indicating a point where to cut */
        private val isChoicePointChild: Boolean = false,
        /** A filed to indicate in which scope level is this sideEffectManager */
        private val ruleScopeLevel: Int = 0,
        /** Filled when cut execution happens, this indicates which are the parent contexts whose unexplored children should be cut */
        private val toCutContextsParent: Lazy<Sequence<ExecutionContextImpl>> = lazyOf(emptySequence()),
        /** Filled when cut execution happens, this indicates which scope level should be cut */
        private val toCutScopeLevel: Int? = null,

        // Catch / Throw side effects fields

        /** The sequence of parent [Solve.Request]s from this execution context till the resolution root */
        internal val logicalParentRequests: Lazy<Sequence<Solve.Request<ExecutionContextImpl>>> = lazyOf(emptySequence()),
        /** The sequence of no more selectable parent requests, because already used */
        private val throwNonSelectableParentContexts: Lazy<Sequence<ExecutionContextImpl>> = lazyOf(emptySequence()),
        /** The execution context where a `catch` was found and till which other unexplored sub-trees should be cut */
        private val throwRelatedToCutContextsParent: ExecutionContextImpl? = null

) : SideEffectManager {

    override fun cut(): SideEffectManager = copy(
            toCutContextsParent = lazy { toCutContextsParent.value + getFirstChoicePointContext() }, // only second value could be enough, but more testing needed
            toCutScopeLevel = ruleScopeLevel
    )

    /**
     * Method that queries if a Cut should be executed in a Primitive implementation body
     *
     * A cut should execute:
     * - if throw was called, and not already caught
     * - if cut was called, and there's some clauseScopedParent to be cut in correct ruleScopeLevel
     */
    override fun shouldCutExecuteInPrimitive(): Boolean =
            clauseScopedParents.value.any { it in toCutContextsParent.value } && ruleScopeLevel == toCutScopeLevel
                    || shouldExecuteThrowCut()


    /** Initializes isChoicePointChild to `false` whatever it was, and adds given [currentContext] to clauseScopedParents */
    internal fun stateInitInitialize(currentContext: ExecutionContextImpl): SideEffectManagerImpl = copy(
            clauseScopedParents = lazy { sequenceOf(currentContext) + clauseScopedParents.value },
            isChoicePointChild = false
    )

    /**
     * Method that updates internal structures when creating a new solve request
     *
     * @param currentContext the context current before new solve request creation
     * @param isChoicePointChild whether this solve request is child of a ChoicePoint
     * @param logicalParentRequest the Solve.Request to be considered logically the parent of this (to correctly implement `catch/throw` interaction)
     */
    internal fun creatingNewRequest(
            currentContext: ExecutionContextImpl,
            isChoicePointChild: Boolean,
            logicalParentRequest: Solve.Request<ExecutionContextImpl>
    ) = copy(
            clauseScopedParents = lazy { sequenceOf(currentContext) + clauseScopedParents.value },
            isChoicePointChild = isChoicePointChild,
            logicalParentRequests = when (logicalParentRequest) {
                in logicalParentRequests.value -> lazy { logicalParentRequests.value.dropWhile { it != logicalParentRequest } }
                else -> lazy { sequenceOf(logicalParentRequest) + logicalParentRequests.value }
            }

    )

    /** Method that updates sideEffects manager to not consider parents older than current first, because entering new "rule-scope" */
    internal fun enterRuleSubScope() = copy(
            clauseScopedParents = lazy { sequenceOf(clauseScopedParents.value.first()) },
            ruleScopeLevel = ruleScopeLevel.inc()
    )

    /** Method that updates clauseScopedParent to include upper scope parents; this is needed to maintain Cut functionality through Response chain */
    internal fun extendParentScopeWith(upperScopeSideEffectsManager: SideEffectManagerImpl) = copy(
            clauseScopedParents = lazy { clauseScopedParents.value + upperScopeSideEffectsManager.clauseScopedParents.value },
            ruleScopeLevel = ruleScopeLevel.dec()
    )

    /**
     * Method that queries if a Cut should be executed in StateRuleSelection
     *
     * A cut should execute:
     * - if throw was called, and not already caught
     * - if cut was called, and the first "scoped" choice point context is to be cut
     */
    internal fun shouldCutExecuteInRuleSelection() =
            clauseScopedParents.value.lastOrNull() in toCutContextsParent.value
                    || shouldExecuteThrowCut()

    /** A method to retrieve the first ancestor catch request that has its second argument that unifies with [toUnifyArgument] */
    internal fun retrieveAncestorCatchRequest(toUnifyArgument: Term): Solve.Request<ExecutionContextImpl>? {
        val ancestorCatchesRequests = logicalParentRequests.value.filter { it.signature == Catch.signature }
                .filterNot { it.context in throwNonSelectableParentContexts.value } // exclude already used catch requests

        return ancestorCatchesRequests.find { it.arguments[1].matches(toUnifyArgument) }
    }

    /** Method to set the catch request whose not explored children should be cut from search tree */
    internal fun throwCut(ancestorCatchContext: ExecutionContextImpl) = copy(
            throwRelatedToCutContextsParent = ancestorCatchContext
    )

    /** Method to query if provided context was that selected by [throwCut] invocation */
    internal fun isSelectedThrowCatch(contextImpl: ExecutionContextImpl) = throwRelatedToCutContextsParent == contextImpl

    /** Method to remove catch request with that [contextImpl], ensuring that's no more selectable */
    internal fun ensureNoMoreSelectableCatch(contextImpl: ExecutionContextImpl) = copy(
            throwNonSelectableParentContexts = lazy { throwNonSelectableParentContexts.value + contextImpl }
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
    private fun shouldExecuteThrowCut() = logicalParentRequests.value.any { it.context == throwRelatedToCutContextsParent }

    /** Internal function to retrieve the first choice point to be Cut, if present */
    private fun getFirstChoicePointContext() = clauseScopedParents.value
            .filter { it.sideEffectManager.isChoicePointChild }
            .mapNotNull { it.sideEffectManager.clauseScopedParents.value.firstOrNull() }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SideEffectManagerImpl

        if (clauseScopedParents.value != other.clauseScopedParents.value) return false
        if (isChoicePointChild != other.isChoicePointChild) return false
        if (ruleScopeLevel != other.ruleScopeLevel) return false
        if (toCutContextsParent.value != other.toCutContextsParent.value) return false
        if (toCutScopeLevel != other.toCutScopeLevel) return false
        if (logicalParentRequests.value != other.logicalParentRequests.value) return false
        if (throwNonSelectableParentContexts.value != other.throwNonSelectableParentContexts.value) return false
        if (throwRelatedToCutContextsParent != other.throwRelatedToCutContextsParent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clauseScopedParents.value.hashCode()
        result = 31 * result + isChoicePointChild.hashCode()
        result = 31 * result + ruleScopeLevel
        result = 31 * result + toCutContextsParent.value.hashCode()
        result = 31 * result + (toCutScopeLevel ?: 0)
        result = 31 * result + logicalParentRequests.value.hashCode()
        result = 31 * result + throwNonSelectableParentContexts.value.hashCode()
        result = 31 * result + (throwRelatedToCutContextsParent?.hashCode() ?: 0)
        return result
    }
}

/** Bridge method to reach [SideEffectManagerImpl.shouldCutExecuteInRuleSelection] homonym method from a [SideEffectManager] */
internal fun SideEffectManager?.shouldCutExecuteInRuleSelection(): Boolean =
        (this as? SideEffectManagerImpl)?.shouldCutExecuteInRuleSelection() ?: false

/** Bridge method to reach [SideEffectManagerImpl.extendParentScopeWith] homonym method from a [SideEffectManager] */
internal fun SideEffectManager?.extendParentScopeWith(upperScopeSideEffectsManager: SideEffectManagerImpl): SideEffectManagerImpl? =
        (this as? SideEffectManagerImpl)?.extendParentScopeWith(upperScopeSideEffectsManager)

/** Bridge method to reach [SideEffectManagerImpl.resetCutWorkChanges] homonym method from a [SideEffectManager] */
internal fun SideEffectManager?.resetCutWorkChanges(toRecoverSituation: SideEffectManagerImpl): SideEffectManagerImpl? =
        (this as? SideEffectManagerImpl)?.resetCutWorkChanges(toRecoverSituation)

/** Bridge method to reach [SideEffectManagerImpl.isSelectedThrowCatch] homonym method from a [SideEffectManager] */
internal fun SideEffectManager?.isSelectedThrowCatch(context: ExecutionContextImpl): Boolean =
        (this as? SideEffectManagerImpl)?.isSelectedThrowCatch(context) ?: false
