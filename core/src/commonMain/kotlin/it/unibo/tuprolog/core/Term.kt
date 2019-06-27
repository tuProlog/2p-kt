package it.unibo.tuprolog.core

interface Term {

    fun <T : Term> castTo(): T = this as T

    infix fun structurallyEquals(other: Term): Boolean
    infix fun strictlyEquals(other: Term): Boolean

    val isVariable: Boolean
        get() = isVar

    val isVar: Boolean
        get() = false

    val isBound: Boolean
        get() = false

    val isGround: Boolean
        get() = true

    val isStruct: Boolean
        get() = false

    val isAtom: Boolean
        get() = false

    val isNumber: Boolean
        get() = false

    val isInt: Boolean
        get() = false

    val isReal: Boolean
        get() = false

    val isList: Boolean
        get() = false

    val isEmptySet: Boolean
        get() = false

    val isSet: Boolean
        get() = false

    val isClause: Boolean
        get() = false

    val isRule: Boolean
        get() = false

    val isFact: Boolean
        get() = false

    val isDirective: Boolean
        get() = false

    val isCouple: Boolean
        get() = false

    val isEmptyList: Boolean
        get() = false

    val isTrue: Boolean
        get() = false

    val isFail: Boolean
        get() = false

    fun clone(): Term = this

    operator fun plus(other: Term): Struct = Struct.of("+", this, other)

    operator fun minus(other: Term): Struct = Struct.of("-", this, other)

    operator fun times(other: Term): Struct = Struct.of("*", this, other)

    operator fun div(other: Term): Struct = Struct.of("/", this, other)

    infix fun greaterThan(other: Term): Struct = Struct.of(">", this, other)

    infix fun greaterThanOrEqualsTo(other: Term): Struct = Struct.of(">=", this, other)

    infix fun nonLowerThan(other: Term): Struct = this greaterThanOrEqualsTo other

    infix fun lowerThan(other: Term): Struct = Struct.of("<", this, other)

    infix fun lowerThanOrEqualsTo(other: Term): Struct = Struct.of("=<", this, other)

    infix fun nonGreaterThan(other: Term): Struct = this lowerThanOrEqualsTo other

    infix fun intDiv(other: Term): Struct = Struct.of("//", this, other)

    operator fun rem(other: Term): Struct = Struct.of("rem", this, other)

    infix fun pow(other: Term): Struct = Struct.of("**", this, other)

    infix fun sup(other: Term): Struct = Struct.of("^", this, other)
}