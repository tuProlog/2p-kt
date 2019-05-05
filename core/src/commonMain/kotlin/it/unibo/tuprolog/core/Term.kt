package it.unibo.tuprolog.core

import kotlin.collections.List

interface Term {

    fun <T : Term> castTo(): T {
        return this as T
    }

    infix fun structurallyEquals(other: Term): Boolean

    val isVariable: Boolean
        get() = false

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

}