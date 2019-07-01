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

    fun groundTo(substitution: Substitution): Term {
        return when {
            this.isGround -> this
            this is Var -> substitution[this] ?: this
            this is Struct -> Struct.of(this.functor, this.argsList.map { it.groundTo(substitution) })
            else -> this
        }
    }

    fun groundTo(substitution: Substitution, vararg substitutions: Substitution): Term {
        return this.groundTo(substitutionOf(substitution, *substitutions))
    }

    operator fun get(substitution: Substitution, vararg substitutions: Substitution): Term {
        return this.groundTo(substitution, *substitutions)
    }

}