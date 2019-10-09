package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.dsl.Prolog
import it.unibo.tuprolog.unify.Unification

interface PrologWithUnification : Prolog, Unification {

    infix fun Any.mguWith(other: Any): Substitution =
            this@PrologWithUnification.mgu(this.toTerm(), other.toTerm())

    infix fun Any.matches(other: Any): Boolean =
            this@PrologWithUnification.match(this.toTerm(), other.toTerm())

    infix fun Any.unifyWith(other: Any): Term? =
            this@PrologWithUnification.unify(this.toTerm(), other.toTerm())

    fun mgu(term1: Any, term2: Any, occurCheckEnabled: Boolean = true): Substitution =
            mgu(term1.toTerm(), term2.toTerm(), occurCheckEnabled)

    fun match(term1: Any, term2: Any, occurCheckEnabled: Boolean = true): Boolean =
            match(term1.toTerm(), term2.toTerm(), occurCheckEnabled)

    fun unify(term1: Any, term2: Any, occurCheckEnabled: Boolean = true): Term? =
            unify(term1.toTerm(), term2.toTerm(), occurCheckEnabled)

    companion object {
        fun empty(): PrologWithUnification = PrologWithUnificationImpl()

        fun of(unification: Unification): PrologWithUnification = PrologWithUnificationImpl(unification)
    }
}

fun <R> PrologWithUnification.scope(function: PrologWithUnification.() -> R): R {
    return PrologWithUnification.empty().function()
}

fun PrologWithUnification.rule(function: PrologWithUnification.() -> Term): Rule {
    return PrologWithUnification.empty().function() as Rule
}

fun PrologWithUnification.fact(function: PrologWithUnification.() -> Term): Fact {
    return factOf(PrologWithUnification.empty().function() as Struct)
}

fun <R> prolog(function: PrologWithUnification.() -> R): R {
    return PrologWithUnification.empty().function()
}