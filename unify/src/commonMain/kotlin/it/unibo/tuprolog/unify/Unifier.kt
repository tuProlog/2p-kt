package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import toEquations

interface Unifier {

    val context: Substitution

    fun unify(term1: Term, term2: Term): Substitution

    companion object {

        val default by lazy { naive() }

        infix fun Term.unifiesWith(other: Term): Substitution {
            return default.unify(this, other)
        }

        fun naive(context: Substitution = emptyMap()) : Unifier {
            return object : AbstractUnifier(context.toEquations()) {
                override fun Var.isEqualTo(other: Var): Boolean {
                    return name == other.name
                }
            }
        }
    }
}