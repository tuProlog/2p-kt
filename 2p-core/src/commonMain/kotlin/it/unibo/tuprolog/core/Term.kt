package it.unibo.tuprolog.core

interface Term {

    fun <T : Term> cast(): T {
        return this as T
    }

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
}