package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Substitution.Companion.empty
import it.unibo.tuprolog.core.Substitution.Companion.failed
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

abstract class AbstractUnificator(
    override val context: Substitution,
) : Unificator {
    constructor() : this(empty())

    protected sealed interface Request {
        data class Mgu(
            val term1: Term,
            val term2: Term,
            val occurCheckEnabled: Boolean,
        ) : Request

        data class Merge(
            val substitution1: Substitution,
            val substitution2: Substitution,
            val occurCheckEnabled: Boolean,
        ) : Request
    }

    /** The context converted to equivalent equations */
    private val contextEquations: Iterable<Equation> by lazy { context.toEquations() }

    /** Checks provided [Term]s for equality */
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

    protected open fun handleResult(
        request: Request,
        result: Substitution,
    ): Substitution = result

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
