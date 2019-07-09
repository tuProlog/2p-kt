package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.*

operator fun Any.div(obj: Any): Substitution {
    return when {
        this is Var -> Substitution.of(this, obj.toTerm())
        this is String -> Substitution.of(Var.of(this), obj.toTerm())
        else -> throw IllegalArgumentException("${obj::class} cannot be converted into ${Var::class}")
    }
}

operator fun String.invoke(term: Any, vararg terms: Any): Term {
    return Struct.of(this, (sequenceOf(term) + sequenceOf(*terms)).map { it.toTerm() })
}

//fun substitutionOf(v1: Any, t1: Term): Substitution {
//    return v1 / t1
//}

operator fun Term.plus(other: Term): Struct {
    return Struct.of("+", this, other)
}

operator fun Term.minus(other: Term): Struct {
    return Struct.of("-", this, other)
}

operator fun Term.times(other: Term): Struct {
    return Struct.of("*", this, other)
}

operator fun Term.div(other: Term): Struct {
    return Struct.of("/", this, other)
}

infix fun Term.greaterThan(other: Term): Struct {
    return Struct.of(">", this, other)
}

infix fun Term.greaterThanOrEqualsTo(other: Term): Struct {
    return Struct.of(">=", this, other)
}

infix fun Term.nonLowerThan(other: Term): Struct {
    return this greaterThanOrEqualsTo other
}

infix fun Term.lowerThan(other: Term): Struct {
    return Struct.of("<", this, other)
}

infix fun Term.lowerThanOrEqualsTo(other: Term): Struct {
    return Struct.of("=<", this, other)
}

infix fun Term.nonGreaterThan(other: Term): Struct {
    return this lowerThanOrEqualsTo other
}

infix fun Term.intDiv(other: Term): Struct {
    return Struct.of("//", this, other)
}

operator fun Term.rem(other: Term): Struct {
    return Struct.of("rem", this, other)
}

infix fun Term.pow(other: Term): Struct {
    return Struct.of("**", this, other)
}

infix fun Term.sup(other: Term): Struct {
    return Struct.of("^", this, other)
}

infix fun Struct.impliedBy(other: Term): Rule = Rule.of(this, other)

fun Struct.impliedBy(vararg other: Term): Rule = this impliedBy Tuple.wrapIfNeeded(*other)