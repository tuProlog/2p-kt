package it.unibo.tuprolog.core

typealias Substitution = Map<Var, Term>

fun Substitution.ground(term: Term): Term {
    return term[this]
}

fun Array<Substitution>.ground(term: Term): Term {
    return term[substitutionOf(this[0], *this.sliceArray(1..lastIndex))]
}

fun substitutionOf(v1: Var, t1: Term): Substitution {
    return mapOf(Pair(v1, t1))
}

fun substitutionOf(v1: Var, t1: Term, v2: Var, t2: Term): Substitution {
    return mapOf(Pair(v1, t1), Pair(v2, t2))
}

fun substitutionOf(v1: String, t1: Term, v2: String, t2: Term): Substitution {
    return substitutionOf(varOf(v1), t1, varOf(v2), t2)
}

fun substitutionOf(substitution: Substitution, vararg substitutions: Substitution): Substitution {
    return substitutions.fold(substitution) { s1, s2 -> s1 + s2 }
}