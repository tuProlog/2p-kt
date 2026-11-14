package it.unibo.tuprolog.unify.impl

import it.unibo.tuprolog.core.Compound
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.UnificationRequest
import it.unibo.tuprolog.unify.forEachPatternFunctor
import it.unibo.tuprolog.unify.functorVariables

/**
 * The mutable state of one [it.unibo.tuprolog.unify.AbstractWorklistUnificator] call: a stack of pairs of terms still
 * to be unified, and the [Bindings] established so far. Pairs are popped, dereferenced, offered to
 * [UnificationHooks.handleComparison], then either bind a variable or get decomposed into the pairs of their
 * arguments. Bindings are never propagated to pending pairs: variables are dereferenced when popped.
 *
 * Without occurs-check, bindings may be cyclic: then, compound terms reached through bindings are compared once
 * per pair of instances, which bounds the work on rational terms.
 *
 * Functor variables, i.e. variables standing for the functor of a [Pattern], can only be bound to atoms or variables,
 * also across runs. They are tracked in the [bindings] as representatives of their classes of aliased variables:
 * the functors of the patterns in the terms to unify (or in the values of substitutions to merge) become such when the
 * run starts, and binding a functor variable to another variable makes the latter (the new representative) one too.
 * So, it suffices to check the representative when a class gets bound to a term. While no pattern exists at all (see
 * [Pattern.anyCreated]), none of this is done.
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
     * Pushes [lhs] and [rhs], the terms to unify, after making the functors of their patterns functor variables.
     * Returns `false` if some of them is already bound to something else than an atom.
     */
    fun start(
        lhs: Term,
        rhs: Term,
    ): Boolean {
        if (!constrainFunctorsIn(lhs) || !constrainFunctorsIn(rhs)) return false
        push(lhs, rhs)
        return true
    }

    private fun constrainFunctorsIn(term: Term): Boolean {
        forEachPatternFunctor(term) { if (!constrainFunctor(it)) return false }
        return true
    }

    /** Makes the class of [variable] one of functor variables, unless it is bound to something else than an atom. */
    private fun constrainFunctor(variable: Var): Boolean {
        val representative = bindings.representative(variable)
        if (representative !is Var) return representative.isAtom
        val value = bindings[representative] ?: return true.also { bindings.constrainFunctor(representative) }
        return value.isAtom
    }

    /**
     * Adds the bindings of [substitution]: a variable not bound yet gets bound right away, whereas a variable already
     * bound gets its two values pushed as a pair to unify. Returns `false` if occurs-check rejects a binding.
     */
    fun merge(substitution: Substitution): Boolean {
        for (functor in substitution.functorVariables) if (!constrainFunctor(functor)) return false
        for (term in substitution.values) if (!constrainFunctorsIn(term)) return false
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
            lhs is Struct && rhs is Struct -> decompose(lhs, rhs, lhsRepresentative, rhsRepresentative)
            else -> decomposePatterns(lhs.castToCompound(), rhs.castToCompound(), lhsRepresentative, rhsRepresentative)
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

    /** Decomposes two compound terms, at least one being a [Pattern], functors included. */
    private fun decomposePatterns(
        lhs: Compound,
        rhs: Compound,
        lhsRepresentative: Term,
        rhsRepresentative: Term,
    ): Boolean {
        if (lhs.arity != rhs.arity) return false
        // patterns reached through bindings were met when their bindings were made, but those of a base may not be
        if (lhs is Pattern && !constrainFunctor(lhs.functor)) return false
        if (rhs is Pattern && !constrainFunctor(rhs.functor)) return false
        if (!occursCheck && (lhsRepresentative is Var || rhsRepresentative is Var) && !firstComparison(lhs, rhs)) {
            return true
        }
        for (i in lhs.arity - 1 downTo 0) push(lhs[i], rhs[i])
        // pushed last, so functors are unified first
        push(lhs.functorTerm, rhs.functorTerm)
        return true
    }

    private fun firstComparison(
        lhs: Compound,
        rhs: Compound,
    ): Boolean = (compared ?: HashSet<ComparedPair>().also { compared = it }).add(ComparedPair(lhs, rhs))

    /** Binds the unbound representative [variable] to [term], unless it is a functor variable and [term] cannot be. */
    private fun bind(
        variable: Var,
        term: Term,
    ): Boolean {
        if (Pattern.anyCreated && bindings.isFunctorVariable(variable)) {
            if (term is Var) {
                bindings.constrainFunctor(term)
            } else if (!term.isAtom) {
                return false
            }
        }
        if (occursCheck && term !is Var && bindings.occurs(variable, term)) return false
        bindings[variable] = term
        return true
    }
}
