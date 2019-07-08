package it.unibo.tuprolog.core

interface Term {

    @Suppress("UNCHECKED_CAST")
    fun <T : Term> castTo(): T = this as T

    infix fun structurallyEquals(other: Term): Boolean
    infix fun strictlyEquals(other: Term): Boolean

    val isVariable: Boolean get() = false
    val isBound: Boolean get() = false
    val isGround: Boolean get() = true
    val isStruct: Boolean get() = false
    val isAtom: Boolean get() = false
    val isNumber: Boolean get() = false
    val isInt: Boolean get() = false
    val isReal: Boolean get() = false
    val isList: Boolean get() = false
    val isTuple: Boolean get() = false
    val isEmptySet: Boolean get() = false
    val isSet: Boolean get() = false
    val isClause: Boolean get() = false
    val isRule: Boolean get() = false
    val isFact: Boolean get() = false
    val isDirective: Boolean get() = false
    val isCouple: Boolean get() = false
    val isEmptyList: Boolean get() = false
    val isTrue: Boolean get() = false
    val isFail: Boolean get() = false

    /**
     * Returns a fresh copy of this Term, that is, an instance of Term which is equal to the currenct one in any aspect,
     * except variables directly or indirectly contained into this Term, which are refreshed.
     * This means that it could return itself, if no variable is present (ground term), or a new Term with freshly
     * generated variables.
     *
     * Variables are refreshed consistently, meaning that, if more variables exists within this Term having the same
     * name, all fresh copies of such variables will have the same complete name.
     *
     * Example: "f(X, g(X))".freshCopy() returns something like "f(X_1, g(X_1))" instead of "f(X_1, g(X_2))"
     */
    fun freshCopy(): Term = freshCopy(Scope.empty())

    /**
     * Returns a fresh copy of this Term, similarly to `freshCopy`, possibly reusing variables from the provided scope,
     * if any
     *
     * @see freshCopy
     * @param scope the Scope containing variables to be used in copying
     *
     */
    fun freshCopy(scope: Scope): Term = this

    fun groundTo(substitution: Substitution): Term = when {
        this.isGround -> this
        this is Var -> substitution[this] ?: this
        this is Struct -> Struct.of(this.functor, this.argsList.map { it.groundTo(substitution) })
        else -> this
    }

    fun groundTo(substitution: Substitution, vararg substitutions: Substitution): Term =
            groundTo(Substitution.of(substitution, *substitutions))

    operator fun get(substitution: Substitution, vararg substitutions: Substitution): Term =
            groundTo(substitution, *substitutions)
}