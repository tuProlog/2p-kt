package it.unibo.tuprolog.unify


import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Substitution.Companion.asUnifier
import it.unibo.tuprolog.core.Substitution.Companion.failed
import org.gciatto.kt.math.BigDecimal

abstract class AbstractUnificationStrategy(private val _context: Iterable<Equation<Var, Term>>) : Unification {

    // FIXME: if a failed context is passed in, from the constructor, the unification should immediately fail!

    constructor() : this(emptyList())

    override val context: Substitution by lazy {
        _context.map { it.toPair() }.toMap().asUnifier()
    }

    protected abstract fun Var.isEqualTo(other: Var): Boolean

    protected fun Var.occursInTerm(term: Term): Boolean {
        return when {
            term is Var -> this@occursInTerm.isEqualTo(term)
            term is Struct -> term.args.any { this@occursInTerm.occursInTerm(it) }
            else -> false
        }
    }

    protected fun Substitution.applyToAll(equations: MutableList<Equation<Term, Term>?>, except: Int): Boolean {
        var changed = false

        for (i in equations.indices) {
            if (i == except || equations[i] == null) continue

            with(equations[i]!!) {
                val newLhs = lhs[this@applyToAll]
                val newRhs = rhs[this@applyToAll]
                changed = changed || lhs !== newLhs || rhs !== newRhs
                if (changed) {
                    equations[i] = Equation(newLhs, newRhs)
                }
            }
        }

        return changed
    }

    protected fun equationFor(number: Numeric, atom: Atom): Sequence<Equation<Term, Term>?> {
        try {
            if (number.decimalValue.compareTo(BigDecimal.of(atom.value)) != 0) {
                return sequenceOf(null)
            }
            return emptySequence()
        } catch (e: NumberFormatException) {
            return sequenceOf(null)
        }
    }

    protected fun equationsFor(term1: Term, term2: Term): Sequence<Equation<Term, Term>?> {

        return when {
            term1 is Var || term2 is Var -> sequenceOf(term1 `=` term2)
            term1 is Struct && term2 is Struct -> {
                if (term1.functor != term2.functor || term1.arity != term2.arity) {
                    sequenceOf(null)
                } else {
                    (0 until term1.arity).asSequence().flatMap {
                        equationsFor(term1[it], term2[it])
                    }
                }
            }
            term1 is Numeric && term2 is Atom -> equationFor(term1, term2)
            term1 is Atom && term2 is Numeric -> equationFor(term2, term1)
            else -> {
                if (term1 == term2) {
                    emptySequence()
                } else {
                    sequenceOf(null)
                }
            }
        }
    }

    override fun mgu(term1: Term, term2: Term, occurCheck: Boolean): Substitution {
        val equations: MutableList<Equation<Term, Term>?> = context.entries
                .map { Equation.from(it.toPair()) }
                .toMutableList()

        for (eq in equationsFor(term1, term2)) {
            if (eq === null) {
                return failed()
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