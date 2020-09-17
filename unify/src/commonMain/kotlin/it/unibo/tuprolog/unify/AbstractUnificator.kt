package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Substitution.Companion.empty
import it.unibo.tuprolog.core.Substitution.Companion.failed
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Equation.Assignment
import it.unibo.tuprolog.unify.Equation.Comparison
import it.unibo.tuprolog.unify.Equation.Contradiction
import it.unibo.tuprolog.unify.Equation.Identity
import it.unibo.tuprolog.utils.dequeOf
import kotlin.jvm.JvmOverloads

abstract class AbstractUnificator @JvmOverloads constructor(override val context: Substitution = empty()) : Unificator {

    /** The context converted to equivalent equations */
    private val contextEquations: Iterable<Equation<Var, Term>> by lazy { context.toEquations() }

    /** Checks provided [Term]s for equality */
    protected abstract fun checkTermsEquality(first: Term, second: Term): Boolean

    /** Implements the so called occur-check; checks if the [variable] is present in [term] */
    private fun occurrenceCheck(variable: Var, term: Term): Boolean =
        when (term) {
            is Var -> checkTermsEquality(variable, term)
            is Struct -> term.variables.any { occurrenceCheck(variable, it) }
            else -> false
        }

    /** Returns the sequence of equations resulting from the comparison of given [Term]s */
    private fun equationsFor(term1: Term, term2: Term): Sequence<Equation<Term, Term>> =
        Equation.allOf(term1, term2, this::checkTermsEquality)

    private fun equationsFor(substitution1: Substitution, substitution2: Substitution): Sequence<Equation<Term, Term>> =
        Equation.from(
            (substitution1.asSequence() + substitution2.asSequence()).map { it.toPair() }
        )

    /** A function to apply given [substitution] to [equations], skipping the equation at given [exceptIndex] */
    private fun applySubstitutionToEquations(
        substitution: Substitution,
        equations: MutableList<Equation<Term, Term>>,
        exceptIndex: Int
    ): Boolean {
        var changed = false

        for (i in equations.indices) {
            if (i == exceptIndex || equations[i] is Contradiction || equations[i] is Identity) continue

            val currentEq = equations[i]
            val (newLhs, newRhs) = currentEq.apply(substitution).toPair()

            if (currentEq.lhs != newLhs || currentEq.rhs != newRhs) {
                equations[i] = Equation.of(newLhs, newRhs, this::checkTermsEquality)
                changed = true
            }
        }

        return changed
    }

    private fun mgu(equations: MutableList<Equation<Term, Term>>, occurCheckEnabled: Boolean): Substitution {
        var changed = true

        while (changed) {
            changed = false
            val eqIterator = equations.listIterator()

            while (eqIterator.hasNext()) {
                when (val eq = eqIterator.next()) {
                    is Contradiction -> {
                        return failed()
                    }
                    is Identity -> {
                        eqIterator.remove()
                        changed = true
                    }
                    is Assignment -> {
                        if (occurCheckEnabled && occurrenceCheck(eq.lhs as Var, eq.rhs)) {
                            return failed()
                        } else {
                            changed = changed || applySubstitutionToEquations(
                                Substitution.of(eq.lhs as Var, eq.rhs),
                                equations,
                                eqIterator.previousIndex()
                            )
                        }
                    }
                    is Comparison -> {
                        eqIterator.remove()
                        insertion@ for (it in equationsFor(eq.lhs, eq.rhs)) {
                            when (it) {
                                is Identity -> continue@insertion
                                is Contradiction -> return failed()
                                else -> eqIterator.add(it)
                            }
                        }
                        changed = true
                    }
                }
            }
        }

        return equations.filterIsInstance<Assignment<Var, Term>>().toSubstitution()
    }

    override fun mgu(term1: Term, term2: Term, occurCheckEnabled: Boolean): Substitution {
        if (context.isFailed) return failed()
        val equations = dequeOf(contextEquations + equationsFor(term1, term2))
        return mgu(equations, occurCheckEnabled)
    }

    override fun merge(
        substitution1: Substitution,
        substitution2: Substitution,
        occurCheckEnabled: Boolean
    ): Substitution {
        if (context.isFailed) return failed()

        val equations = dequeOf(contextEquations + equationsFor(substitution1, substitution2))
        return mgu(equations, occurCheckEnabled)
    }
}
