package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Substitution.Companion.asUnifier
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.toTerm
import kotlin.js.JsName

typealias Equation<A, B> = Pair<A, B>

fun <A : Term, B : Term> equationOf(first: A, second: B): Equation<A, B> {
    return Pair(first, second)
}

fun equationOf(first: Any, second: Any): Equation<Term, Term> {
    return Pair(first.toTerm(), second.toTerm())
}

@JsName("termEq")
infix fun <A : Term, B : Term> A.`=`(that: B): Equation<A, B> {
    return equationOf(this, that)
}

@JsName("anyEq")
infix fun Any.`=`(that: Any): Equation<Term, Term> {
    return equationOf(this.toTerm(), that.toTerm())
}

fun <A : Term, B : Term> Equation<A, B>.swap(): Equation<B, A> {
    return Pair(second, first)
}

fun <A : Var, B : Term> Equation<A, B>.toSubstitution(): Substitution {
    return mapOf<Var, Term>(this).asUnifier()
}

fun Substitution.toEquations(): List<Equation<Var, Term>> {
    return this.entries.map { it.key `=` it.value }
}