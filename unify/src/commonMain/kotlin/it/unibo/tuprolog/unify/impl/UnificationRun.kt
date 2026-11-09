package it.unibo.tuprolog.unify.impl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.UnificationRequest

/**
 * The mutable state of one [it.unibo.tuprolog.unify.AbstractWorklistUnificator] call: a stack of pairs of terms still
 * to be unified, and the [Bindings] established so far. Pairs are popped, dereferenced, offered to
 * [UnificationHooks.handleComparison], then either bind a variable or get decomposed into the pairs of their
 * arguments. Bindings are never propagated to pending pairs: variables are dereferenced when popped.
 *
 * Without occurs-check, bindings may be cyclic: then, compound terms reached through bindings are compared once
 * per pair of instances, which bounds the work on rational terms.
 */
internal class UnificationRun(
    private val hooks: UnificationHooks,
    private val bindings: Bindings,
    private val occursCheck: Boolean,
    request: UnificationRequest,
) {
    /** Pairs, flattened: each left term is followed by its right term. */
    private val pending = ArrayList<Term>()

    private val step = MutableUnificationStep(request)

    /** Compound terms reached through bindings and already compared: only tracked without occurs-check. */
    private var compared: MutableSet<ComparedPair>? = null

    fun push(
        lhs: Term,
        rhs: Term,
    ) {
        pending.add(lhs)
        pending.add(rhs)
    }

    /**
     * Adds the bindings of [substitution]: a variable not bound yet gets bound right away, whereas a variable already
     * bound gets its two values pushed as a pair to unify. Returns `false` if occurs-check rejects a binding.
     */
    fun merge(substitution: Substitution): Boolean {
        for ((variable, term) in substitution) {
            val representative = bindings.representative(variable)
            if (representative is Var && bindings[representative] == null) {
                val value = bindings.dereference(term)
                if (value != representative && !bind(representative, value)) return false
            } else {
                push(variable, term)
            }
        }
        return true
    }

    /** Unifies all pending pairs, then builds the resulting [Substitution]. */
    fun solve(): Substitution =
        if (unifyPending()) SubstitutionMaterializer(bindings).materialize() else Substitution.failed()

    /** Unifies all pending pairs, leaving the bindings in [bindings]; `false` at the first failure. */
    fun unifyPending(): Boolean {
        while (pending.isNotEmpty()) {
            val rhs = pending.removeAt(pending.lastIndex)
            val lhs = pending.removeAt(pending.lastIndex)
            if (!unify(lhs, rhs)) return false
        }
        return true
    }

    private fun unify(
        originalLhs: Term,
        originalRhs: Term,
    ): Boolean {
        var lhsRepresentative = bindings.representative(originalLhs)
        var rhsRepresentative = bindings.representative(originalRhs)
        var lhs = bindings.valueOf(lhsRepresentative)
        var rhs = bindings.valueOf(rhsRepresentative)
        step.reset(lhs, rhs)
        hooks.handleComparison(step)
        when (step.verdict) {
            StepVerdict.ACCEPTED -> return true
            StepVerdict.REJECTED -> return false
            StepVerdict.REPLACED -> {
                lhsRepresentative = bindings.representative(step.lhs)
                rhsRepresentative = bindings.representative(step.rhs)
                lhs = bindings.valueOf(lhsRepresentative)
                rhs = bindings.valueOf(rhsRepresentative)
            }
            StepVerdict.PROCEED -> Unit
        }
        return when {
            lhs === rhs -> true
            lhs is Var -> (rhs is Var && hooks.checkTermsEquality(lhs, rhs)) || bind(lhs, rhs)
            rhs is Var -> bind(rhs, lhs)
            lhs.isConstant || rhs.isConstant -> lhs.isConstant && rhs.isConstant && hooks.checkTermsEquality(lhs, rhs)
            else -> decompose(lhs.castToStruct(), rhs.castToStruct(), lhsRepresentative, rhsRepresentative)
        }
    }

    private fun decompose(
        lhs: Struct,
        rhs: Struct,
        lhsRepresentative: Term,
        rhsRepresentative: Term,
    ): Boolean {
        if (lhs.arity != rhs.arity || lhs.functor != rhs.functor) return false
        if (!occursCheck && (lhsRepresentative is Var || rhsRepresentative is Var) && !firstComparison(lhs, rhs)) {
            // cyclic bindings can lead back to the same pair: comparing it again would loop forever
            return true
        }
        // last argument pushed first, so that list spines are processed with a constant amount of pending pairs
        for (i in lhs.arity - 1 downTo 0) push(lhs[i], rhs[i])
        return true
    }

    private fun firstComparison(
        lhs: Struct,
        rhs: Struct,
    ): Boolean = (compared ?: HashSet<ComparedPair>().also { compared = it }).add(ComparedPair(lhs, rhs))

    private fun bind(
        variable: Var,
        term: Term,
    ): Boolean {
        if (occursCheck && term !is Var && bindings.occurs(variable, term)) return false
        bindings[variable] = term
        return true
    }
}
