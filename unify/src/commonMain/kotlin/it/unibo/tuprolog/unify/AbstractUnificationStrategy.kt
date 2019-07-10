package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Substitution.Companion.asUnifier
import it.unibo.tuprolog.core.Substitution.Companion.failed
import it.unibo.tuprolog.unify.Equation.*

abstract class AbstractUnificationStrategy(private val _context: Iterable<Equation<Var, Term>>) : Unification {

    // FIXME: if a failed context is passed in, from the constructor, the unification should immediately fail!

    constructor() : this(emptyList())

    override val context: Substitution by lazy {
        _context.map { it.toPair() }.toMap().asUnifier()
    }

    protected abstract fun checkTermsEquality(first: Term, second: Term): Boolean

    private val termsEqualityChecker: (Term, Term)->Boolean = { a, b -> checkTermsEquality(a, b) }

    protected fun occurrenceCheck(variable: Var, term: Term): Boolean {
        return when (term) {
            is Var -> checkTermsEquality(variable, term)
            is Struct -> term.args.any { occurrenceCheck(variable, it) }
            else -> false
        }
    }

    protected fun applySubstitutionToEquations(substitution: Substitution, equations: MutableList<Equation<Term, Term>>, exceptIndex: Int): Boolean {
        var changed = false

        for (index in equations.indices) {
            if (index == exceptIndex || equations[index] is Contradiction) continue

            with(equations[index]) {
                val newLhs = lhs!![substitution]
                val newRhs = rhs!![substitution]
                changed = changed || lhs !== newLhs || rhs !== newRhs
                if (changed) {
                    equations[index] = Equation.of(newLhs, newRhs, termsEqualityChecker)
                }
            }
        }

        return changed
    }

    protected fun equationsFor(term1: Term, term2: Term): Sequence<Equation<Term, Term>> =
        Equation.allOf(term1, term2, termsEqualityChecker)


    override fun mgu(term1: Term, term2: Term, occurCheck: Boolean): Substitution {
        val equations: MutableList<Equation<Term, Term>?> = context.entries
                .map { Equation.from(it.toPair()) }
                .toMutableList()

        for (eq in equationsFor(term1, term2)) {
            if (eq === null) {
                return Substitution.failed()
            } else {
                equations.add(eq)
            }
        }

        var changed = true

        while (changed) {
            changed = false
            for (i in equations.indices) {
                if (equations[i] === null) return failed()

                with(equations[i]!!) {
                    when {
                        lhs is Var && rhs is Var -> {
                            if ((lhs as Var).isEqualTo(rhs as Var)) {
                                changed = true
                                // TODO notice that this may be inefficient in case MutableList is an alias for ArrayList
                                // an optimization may enforce the employment of a LinkedList, e.g. through a template method
                                equations.removeAt(i)
                            } else {
                                changed = Substitution.of(lhs as Var, rhs).applyToAll(equations, i)
                            }
                        }
                        rhs is Var -> {
                            equations[i] = rhs `=` lhs
                            changed = true
                        }
                        lhs is Var -> {
                            if (occurCheck && (lhs as Var).occursInTerm(rhs)) {
                                return failed()
                            } else {
                                changed = Substitution.of(lhs as Var, rhs).applyToAll(equations, i)
                            }
                        }
                        else -> {
                            changed = true
                            equations.addAll(equationsFor(lhs, rhs))
                        }
                    }
                }
            }
        }

        return equations.filterNotNull()
                .filterIsInstance<Equation<Var, Term>>()
                .map { it.toPair() }
                .toMap().asUnifier()
    }
}