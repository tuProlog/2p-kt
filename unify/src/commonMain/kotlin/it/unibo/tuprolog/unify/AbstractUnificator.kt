package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Substitution.Companion.empty
import it.unibo.tuprolog.core.Substitution.Companion.failed
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

/**
 * Skeletal [Unificator] implementation, providing a complete, equation-based unification algorithm (in the style of
 * Martelli & Montanari's): both [mgu] and [merge] repeatedly decompose their operands into [Equation]s and simplify
 * them (structurally comparing [Term]s, applying resulting variable assignments to the remaining equations) until
 * either a contradiction is found (failure) or no equation can be simplified further (success).
 *
 * The only decision left to subclasses is [checkTermsEquality], namely *how* two non-variable [Term]s are compared
 * while building equations — this is exactly what distinguishes [Unificator.strict] from [Unificator.naive].
 * Subclasses that additionally need to observe or alter the equation-solving process itself (e.g. to reject some
 * equations based on custom criteria, or to inspect the final result) may also override [handleEquation] and/or
 * [handleResult]; both default to the identity function and thus have no effect unless overridden.
 *
 * ```kotlin
 * val caseInsensitive =
 *     object : AbstractUnificator() {
 *         override fun checkTermsEquality(first: Term, second: Term): Boolean = when {
 *             first.isAtom && second.isAtom ->
 *                 first.castToAtom().value.equals(second.castToAtom().value, ignoreCase = true)
 *             else -> first == second
 *         }
 *     }
 * caseInsensitive.match(Atom.of("Foo"), Atom.of("foo")) // true
 * ```
 *
 * @param context the starting bindings assumed by this [Unificator]; see [Unificator.context]
 */
abstract class AbstractUnificator(
    override val context: Substitution,
) : Unificator {
    /** Creates an [AbstractUnificator] with an empty starting [context]. */
    constructor() : this(empty())

    /**
     * The unification request currently being resolved, passed to [handleEquation] and [handleResult] so that
     * overrides can tell which top-level [Unificator] operation ([Unificator.mgu] or [Unificator.merge]) — and with
     * which original arguments — produced the [Equation] or result being handled.
     */
    protected sealed interface Request {
        /** A request originated from a [Unificator.mgu] call on [term1] and [term2]. */
        data class Mgu(
            val term1: Term,
            val term2: Term,
            val occurCheckEnabled: Boolean,
        ) : Request

        /** A request originated from a [Unificator.merge] call on [substitution1] and [substitution2]. */
        data class Merge(
            val substitution1: Substitution,
            val substitution2: Substitution,
            val occurCheckEnabled: Boolean,
        ) : Request
    }

    /** The context converted to equivalent equations */
    private val contextEquations: Iterable<Equation> by lazy { context.toEquations() }

    /**
     * Decides whether [first] and [second] — two [Term]s that are not variables reducible to one another — are to
     * be considered equal while building unification [Equation]s. This is the single extension point that gives
     * strategies like [Unificator.strict] (plain [Term.equals]) and [Unificator.naive] (value-based comparison of
     * numeric terms) their distinct behavior.
     */
    protected abstract fun checkTermsEquality(
        first: Term,
        second: Term,
    ): Boolean

    /** Implements the so called occur-check; checks if the [variable] is present in [term] */
    private fun occurrenceCheck(
        variable: Var,
        term: Term,
    ): Boolean =
        when {
            term.isVar -> checkTermsEquality(variable, term)
            term.isStruct -> term.variables.any { occurrenceCheck(variable, it) }
            else -> false
        }

    /** Returns the sequence of equations resulting from the comparison of given [Term]s */
    private fun equationsFor(
        term1: Term,
        term2: Term,
    ): Sequence<Equation> = Equation.allOf(term1, term2, this::checkTermsEquality)

    private fun equationsFor(
        substitution1: Substitution,
        substitution2: Substitution,
    ): Sequence<Equation> =
        Equation.from(
            (substitution1.asSequence() + substitution2.asSequence()).map { it.toPair() },
        )

    /** A function to apply given [substitution] to [equations], skipping the equation at given [exceptIndex] */
    private fun applySubstitutionToEquations(
        substitution: Substitution,
        equations: MutableList<Equation>,
        exceptIndex: Int,
    ): Boolean {
        var changed = false

        fun handleIndex(i: Int) {
            if (equations[i].isContradiction || equations[i].isIdentity) return

            val currentEq = equations[i]
            val (newLhs, newRhs) = currentEq.apply(substitution).toPair()

            if (currentEq.lhs != newLhs || currentEq.rhs != newRhs) {
                equations[i] = Equation.of(newLhs, newRhs, this::checkTermsEquality)
                changed = true
            }
        }

        for (i in 0 until exceptIndex) handleIndex(i)
        for (i in (exceptIndex + 1) until equations.size) handleIndex(i)

        return changed
    }

    /**
     * Post-processes the [Substitution] computed for [request], right before it is returned by [mgu] or [merge].
     * The default implementation returns [result] unchanged; override to inspect or transform the final outcome
     * of a unification/merge (e.g. for logging, or to enforce additional invariants).
     */
    protected open fun handleResult(
        request: Request,
        result: Substitution,
    ): Substitution = result

    /**
     * Inspects, and optionally transforms, each [Equation] as it is processed while solving [request]. The default
     * implementation returns [equation] unchanged. Overriding it allows a subclass to reject an otherwise valid
     * equation — by returning [Equation.toContradiction] instead — based on criteria other than term structure and
     * value (e.g. metadata carried by the compared [Term]s), effectively vetoing a would-be successful unification.
     */
    protected open fun handleEquation(
        request: Request,
        equation: Equation,
    ): Equation = equation

    private fun mgu(
        request: Request,
        equations: MutableList<Equation>,
        occurCheckEnabled: Boolean,
    ): Substitution {
        var changed = true

        while (changed) {
            changed = false
            val eqIterator = equations.listIterator()

            while (eqIterator.hasNext()) {
                val eq = handleEquation(request, eqIterator.next())
                when {
                    eq.isContradiction -> {
                        return failed() // short circuit
                    }

                    eq.isIdentity -> {
                        eqIterator.remove()
                        changed = true
                    }

                    eq.isAssignment -> {
                        val assignment = eq.castToAssignment()
                        if (occurCheckEnabled && occurrenceCheck(assignment.variable, assignment.term)) {
                            return failed()
                        } else {
                            changed = changed ||
                                applySubstitutionToEquations(
                                    assignment.toSubstitution(),
                                    equations,
                                    eqIterator.previousIndex(),
                                )
                        }
                    }

                    eq.isComparison -> {
                        eqIterator.remove()
                        for (it in equationsFor(eq.lhs, eq.rhs)) {
                            val subEq = if (it.isContradiction || it.isIdentity) handleEquation(request, it) else it
                            // comparisons and assignments are added to the of the list,
                            // so their handleEquation callback is called in the next iteration of the outer loop
                            when {
                                subEq.isIdentity -> continue
                                subEq.isContradiction -> return failed()
                                else -> eqIterator.add(subEq)
                            }
                        }
                        changed = true
                    }
                }
            }
        }

        return handleResult(request, equations.filter { it.isAssignment }.toSubstitution())
    }

    override fun mgu(
        term1: Term,
        term2: Term,
        occurCheckEnabled: Boolean,
    ): Substitution {
        if (context.isFailed) return failed()
        val equations = newDeque(contextEquations.asSequence() + equationsFor(term1, term2))
        return mgu(Request.Mgu(term1, term2, occurCheckEnabled), equations, occurCheckEnabled)
    }

    /**
     * When [occurCheckEnabled] is `false`, this first attempts a quick [Substitution.plus]-based union of [context],
     * [substitution1] and [substitution2]; only if that union is contradictory does it fall back to the full,
     * equation-based merge (which also re-unifies, rather than merely composes, bindings shared by both operands).
     */
    override fun merge(
        substitution1: Substitution,
        substitution2: Substitution,
        occurCheckEnabled: Boolean,
    ): Substitution {
        if (context.isFailed || substitution1.isFailed || substitution2.isFailed) return failed()
        if (!occurCheckEnabled) {
            val quickMerge = context + substitution1 + substitution2
            if (quickMerge.isSuccess) {
                return quickMerge
            }
        }
        val equations = newDeque(contextEquations.asSequence() + equationsFor(substitution1, substitution2))
        return mgu(Request.Merge(substitution1, substitution2, occurCheckEnabled), equations, occurCheckEnabled)
    }

    private fun <T> newDeque(items: Sequence<T>): MutableList<T> = items.toCollection(arrayListOf())
}
