package it.unibo.tuprolog.unify

import Equation
import eq
import it.unibo.tuprolog.core.*
import org.gciatto.kt.math.BigDecimal

abstract class AbstractUnifier(private val _context: Iterable<Equation<Var, Term>>) : Unifier {

    constructor() : this(emptyList())

    override val context: Substitution by lazy {
        _context.toMap()
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
                val newFirst = first[this@applyToAll]
                val newSecond = second[this@applyToAll]
                changed = changed || first !== newFirst || second !== newSecond
                if (changed) {
                    equations[i] = Pair(newFirst, newSecond)
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
            term1 is Var || term2 is Var -> sequenceOf(term1 eq term2)
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

    override fun mgu(term1: Term, term2: Term, occurCheck: Boolean): Substitution? {
        val equations: MutableList<Equation<Term, Term>?> = context.entries
                .map { it.toPair() }
                .toMutableList()

        for (eq in equationsFor(term1, term2)) {
            if (eq === null) {
                return null
            } else {
                equations.add(eq)
            }
        }

        var changed = true

        while (changed) {
            changed = false
            for (i in equations.indices) {
                if (equations[i] === null) continue

                with(equations[i]!!) {
                    when {
                        first is Var && second is Var -> {
                            if ((first as Var).isEqualTo(second as Var)) {
                                changed = true
                                equations[i] = null
                            } else {
                                changed = substitutionOf(first as Any, second).applyToAll(equations, i)
                            }
                        }
                        second is Var -> {
                            equations[i] = second eq first
                            changed = true
                        }
                        first is Var -> {
                            if (occurCheck && (first as Var).occursInTerm(second)) {
                                return null
                            } else {
                                changed = substitutionOf(first as Any, second).applyToAll(equations, i)
                            }
                        }
                        else -> {
                            changed = true
                            equations.addAll(equationsFor(first, second))
                        }
                    }
                }
            }
        }

        return equations.filterNotNull()
                .filterIsInstance<Equation<Var, Term>>()
                .toMap()
    }
}