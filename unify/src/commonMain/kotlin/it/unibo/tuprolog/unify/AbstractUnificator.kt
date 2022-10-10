package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Substitution.Companion.empty
import it.unibo.tuprolog.core.Substitution.Companion.failed
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.jvm.JvmOverloads

abstract class AbstractUnificator @JvmOverloads constructor(override val context: Substitution = empty()) : Unificator {

    /** The context converted to equivalent equations */
    private val contextEquations: Iterable<Equation> by lazy { context.toEquations() }

    /** Checks provided [Term]s for equality */
    protected abstract fun checkTermsEquality(first: Term, second: Term): Boolean

    /** Implements the so called occur-check; checks if the [variable] is present in [term] */
    private fun occurrenceCheck(variable: Var, term: Term): Boolean =
        when {
            term.isVar -> checkTermsEquality(variable, term)
            term.isStruct -> term.variables.any { occurrenceCheck(variable, it) }
            else -> false
        }

    /** Returns the sequence of equations resulting from the comparison of given [Term]s */
    private fun equationsFor(term1: Term, term2: Term): Sequence<Equation> =
        Equation.allOf(term1, term2, this::checkTermsEquality)

    private fun equationsFor(substitution1: Substitution, substitution2: Substitution): Sequence<Equation> =
        Equation.from(
            (substitution1.asSequence() + substitution2.asSequence()).map { it.toPair() }
        )

    /** A function to apply given [substitution] to [equations], skipping the equation at given [exceptIndex] */
    private fun applySubstitutionToEquations(
        substitution: Substitution,
        equations: MutableList<Equation>,
        exceptIndex: Int
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

    private fun mgu(equations: MutableList<Equation>, occurCheckEnabled: Boolean): Substitution {
        var changed = true

        while (changed) {
            changed = false
            val eqIterator = equations.listIterator()

            while (eqIterator.hasNext()) {
                val eq = eqIterator.next()
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
                        if (occurCheckEnabled && occurrenceCheck(assignment.lhs, eq.rhs)) {
                            return failed()
                        } else {
                            changed = changed || applySubstitutionToEquations(
                                assignment.toSubstitution(),
                                equations,
                                eqIterator.previousIndex()
                            )
                        }
                    }
                    eq.isComparison -> {
                        eqIterator.remove()
                        insertion@ for (it in equationsFor(eq.lhs, eq.rhs)) {
                            when {
                                it.isIdentity -> continue@insertion
                                it.isContradiction -> return failed()
                                else -> eqIterator.add(it)
                            }
                        }
                        changed = true
                    }
                }
            }
        }

        return equations.filter { it.isAssignment }.toSubstitution()
    }

    override fun mgu(term1: Term, term2: Term, occurCheckEnabled: Boolean): Substitution {
        if (context.isFailed) return failed()
        val equations = newDeque(contextEquations.asSequence() + equationsFor(term1, term2))
        return mgu(equations, occurCheckEnabled)
    }

    override fun merge(
        substitution1: Substitution,
        substitution2: Substitution,
        occurCheckEnabled: Boolean
    ): Substitution {
        if (context.isFailed || substitution1.isFailed || substitution2.isFailed) return failed()
        if (!occurCheckEnabled) {
            val quickMerge = context + substitution1 + substitution2
            if (quickMerge.isSuccess) {
                return quickMerge
            }
        }
        val equations = newDeque(contextEquations.asSequence() + equationsFor(substitution1, substitution2))
        return mgu(equations, occurCheckEnabled)
    }

    private fun <T> newDeque(items: Sequence<T>): MutableList<T> = items.toCollection(arrayListOf())
}
