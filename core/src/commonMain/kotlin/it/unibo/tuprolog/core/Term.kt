package it.unibo.tuprolog.core

interface Term {

    fun <T : Term> castTo(): T = this as T

    infix fun structurallyEquals(other: Term): Boolean
    infix fun strictlyEquals(other: Term): Boolean

    val isVariable: Boolean
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

    /**
     * Returns a fresh copy of this Term.
     *
     * This means that it could return itself, if no variable is present (ground term), or a new Term with freshly generated variables.
     *
     * Example: "f(X, g(X))".freshCopy() returns something like "f(X_1, g(X_1))"
     */
    fun freshCopy(): Term = this
}