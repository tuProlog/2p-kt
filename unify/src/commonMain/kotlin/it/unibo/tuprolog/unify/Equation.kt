import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Substitution.Companion.asSuccessSubstitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.toTerm

typealias Equation<A, B> = Pair<A, B>

fun <A : Term, B : Term> equationOf(first: A, second: B): Equation<A, B> {
    return Pair(first, second)
}

fun equationOf(first: Any, second: Any): Equation<Term, Term> {
    return Pair(first.toTerm(), second.toTerm())
}

infix fun <A : Term, B : Term> A.eq(that: B): Equation<A, B> {
    return equationOf(this, that)
}

infix fun Any.eq(that: Any): Equation<Term, Term> {
    return equationOf(this.toTerm(), that.toTerm())
}

fun <A : Term, B : Term> Equation<A, B>.swap(): Equation<B, A> {
    return Pair(second, first)
}

fun <A : Var, B : Term> Equation<A, B>.toSubstitution(): Substitution {
    return mapOf<Var, Term>(this).asSuccessSubstitution()
}

fun Substitution.toEquations(): List<Equation<Var, Term>> {
    return this.entries.map { it.key eq it.value }
}