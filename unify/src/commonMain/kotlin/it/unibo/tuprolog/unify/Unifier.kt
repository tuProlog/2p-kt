package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import toEquations

interface Unifier {

    val context: Substitution

    fun mgu(term1: Term, term2: Term, occurCheck: Boolean = true): Substitution?

    fun match(term1: Term, term2: Term, occurCheck: Boolean = true): Boolean {
        return mgu(term1, term2, occurCheck) !== null
    }

    fun unify(term1: Term, term2: Term, occurCheck: Boolean = true): Term? {
        val substitution = mgu(term1, term2, occurCheck)
        return if (substitution === null) null else term1[substitution]
    }
    
    companion object {

        val default by lazy { naive() }

        infix fun Term.mguWith(other: Term): Substitution? {
            return default.mgu(this, other)
        }

        infix fun Term.unifyWith(other: Term): Term? {
            return default.unify(this, other)
        }

        infix fun Term.matches(other: Term): Boolean {
            return default.match(this, other)
        }

        fun naive(context: Substitution = Substitution.empty()) : Unifier {
            return object : AbstractUnifier(context.toEquations()) {
                override fun Var.isEqualTo(other: Var): Boolean {
                    return name == other.name
                }
            }
        }

        fun strict(context: Substitution = Substitution.empty()) : Unifier {
            return object : AbstractUnifier(context.toEquations()) {
                override fun Var.isEqualTo(other: Var): Boolean {
                    return completeName == other.completeName
                }
            }
        }
    }
}