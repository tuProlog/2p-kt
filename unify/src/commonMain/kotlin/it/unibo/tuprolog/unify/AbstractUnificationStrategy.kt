package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Substitution.Companion.empty
import it.unibo.tuprolog.core.Substitution.Companion.failed
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Equation.*

abstract class AbstractUnificationStrategy(override val context: Substitution = empty()) : Unification {

    /** The context converted to equivalent equations */
    private val contextEquations: Iterable<Equation<Var, Term>> by lazy { context.toEquations() }

    /** Checks provided [Term]s for equality */
    protected abstract fun checkTermsEquality(first: Term, second: Term): Boolean

    /** Implements the so called occur-check; checks if the [variable] is present in [term] */
    private fun occurrenceCheck(variable: Var, term: Term): Boolean =
            when (term) {
                is Var -> checkTermsEquality(variable, term)
                is Struct -> term.args.any { occurrenceCheck(variable, it) }
                else -> false
            }

    /** Returns the sequence of equations resulting from the comparison of given [Term]s */
    private fun equationsFor(term1: Term, term2: Term): Sequence<Equation<Term, Term>> =
            Equation.allOf(term1, term2, this::checkTermsEquality)

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

    override fun mgu(term1: Term, term2: Term, occurCheckEnabled: Boolean): Substitution {
        if (context.isFailed) return failed()

        val equations = (contextEquations + equationsFor(term1, term2)).toMutableList()
        var changed = true

        while (changed) {
            changed = false
            val eqIterator = equations.listIterator()

            while (eqIterator.hasNext()) {

                eqIterator.next().also { eq ->
                    when (eq) {
                        is Contradiction -> return failed()
                        is Identity -> {
                            eqIterator.remove()
                            changed = true
                        }
                        is Assignment -> {
                            if (occurCheckEnabled && occurrenceCheck(eq.lhs as Var, eq.rhs))
                                return failed()
                            else
                                changed = applySubstitutionToEquations(Substitution.of(eq.lhs as Var, eq.rhs), equations, eqIterator.previousIndex())
                        }
                        is Comparison -> {
                            eqIterator.remove()
                            equationsFor(eq.lhs, eq.rhs).forEach(eqIterator::add)
                            changed = true
                        }
                    }
                }
            }
        }

        return equations.filterIsInstance<Assignment<Var, Term>>().toSubstitution()
    }
}