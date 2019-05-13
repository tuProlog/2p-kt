package it.unibo.tuprolog.core

import kotlin.collections.List

interface Term {

    fun <T : Term> castTo(): T {
        return this as T
    }

    infix fun structurallyEquals(other: Term): Boolean
    infix fun strictlyEquals(other: Term): Boolean

    val isVariable: Boolean
        get() = isVar

    val isVar: Boolean
        get() {
            return false
        }

    val isBound: Boolean
        get() {
            return false
        }

    val isGround: Boolean
        get() {
            return true
        }

    val isStruct: Boolean
        get() {
            return false
        }

    val isAtom: Boolean
        get() {
            return false
        }

    val isNumber: Boolean
        get() {
            return false
        }

    val isInt: Boolean
        get() {
            return false
        }

    val isReal: Boolean
        get() {
            return false
        }

    val isList: Boolean
        get() {
            return false
        }

    val isEmptySet: Boolean
        get() {
            return false
        }

    val isSet: Boolean
        get() {
            return false
        }

    val isClause: Boolean
        get() {
            return false
        }

    val isRule: Boolean
        get() {
            return false
        }

    val isFact: Boolean
        get() {
            return false
        }

    val isDirective: Boolean
        get() {
            return false
        }

    val isCouple: Boolean
        get() {
            return false
        }

    val isEmptyList: Boolean
        get() {
            return false
        }

    val isTrue: Boolean
        get() {
            return false
        }

    val isFail: Boolean
        get() {
            return false
        }

    fun clone(): Term {
        return this
    }

    operator fun plus(other: Term): Struct {
        return Struct.of("+", this, other)
    }

    operator fun minus(other: Term): Struct {
        return Struct.of("-", this, other)
    }

    operator fun times(other: Term): Struct {
        return Struct.of("*", this, other)
    }

    operator fun div(other: Term): Struct {
        return Struct.of("/", this, other)
    }

    infix fun greaterThan(other: Term): Struct {
        return Struct.of(">", this, other)
    }

    infix fun greaterThanOrEqualsTo(other: Term): Struct {
        return Struct.of(">=", this, other)
    }

    infix fun nonLowerThan(other: Term): Struct {
        return this greaterThanOrEqualsTo other
    }

    infix fun lowerThan(other: Term): Struct {
        return Struct.of("<", this, other)
    }

    infix fun lowerThanOrEqualsTo(other: Term): Struct {
        return Struct.of("=<", this, other)
    }

    infix fun nonGreaterThan(other: Term): Struct {
        return this lowerThanOrEqualsTo other
    }

    infix fun intDiv(other: Term): Struct {
        return Struct.of("//", this, other)
    }

    operator fun rem(other: Term): Struct {
        return Struct.of("rem", this, other)
    }

    infix fun pow(other: Term): Struct {
        return Struct.of("**", this, other)
    }

    infix fun sup(other: Term): Struct {
        return Struct.of("^", this, other)
    }
}