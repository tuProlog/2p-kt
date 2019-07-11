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

    protected fun applySubstitutionToEquations(substitution: Substitution,
                                               equations: Sequence<Equation<Term, Term>>,
                                               exceptIndex: Int): Sequence<Pair<Boolean, Equation<Term, Term>>> {

        return equations.mapIndexed { i, eq ->
            if (i == exceptIndex) {
                Pair(false, eq)
            } else {
                val newLhs = eq.lhs!![substitution]
                val newRhs = eq.rhs!![substitution]
                val changed = eq.lhs !== newLhs || eq.rhs !== newRhs
                Pair(changed, Equation.of(newLhs, newRhs, termsEqualityChecker))
            }

        }
    }

    protected fun equationsFor(term1: Term, term2: Term): Sequence<Equation<Term, Term>> =
        Equation.allOf(term1, term2, termsEqualityChecker)


    override fun mgu(term1: Term, term2: Term, occurCheckEnabled: Boolean): Substitution {
        var equations = _context.asSequence() + equationsFor(term1, term2)

        var changed = true

        while (changed) {
            changed = false

            val rewriting: Sequence<Equation<Term, Term>> = emptySequence()

            for (eq in equations) {
                when {
                    eq is Contradiction -> {
                        return Substitution.failed()
                    }
                    eq is Identity -> {
                        /* just skip it */
                    }
                    eq is Assignment -> {
                        if (occurCheckEnabled && occurrenceCheck(eq.lhs as Var, eq.rhs))
                            return Substitution.failed()
                        else

                    }
                }
            }


//            for (i in equations.indices) {
//                if (equations[i] === null) return failed()
//
//                with(equations[i]!!) {
//                    when {
//                        lhs is Var && rhs is Var -> {
//                            if ((lhs as Var).isEqualTo(rhs as Var)) {
//                                changed = true
//                                // TODO notice that this may be inefficient in case MutableList is an alias for ArrayList
//                                // an optimization may enforce the employment of a LinkedList, e.g. through a template method
//                                equations.removeAt(i)
//                            } else {
//                                changed = Substitution.of(lhs as Var, rhs).applyToAll(equations, i)
//                            }
//                        }
//                        rhs is Var -> {
//                            equations[i] = rhs `=` lhs
//                            changed = true
//                        }
//                        lhs is Var -> {
//                            if (occurCheckEnabled && (lhs as Var).occursInTerm(rhs)) {
//                                return failed()
//                            } else {
//                                changed = Substitution.of(lhs as Var, rhs).applyToAll(equations, i)
//                            }
//                        }
//                        else -> {
//                            changed = true
//                            equations.addAll(equationsFor(lhs, rhs))
//                        }
//                    }
//                }
//            }
//        }
    }
}