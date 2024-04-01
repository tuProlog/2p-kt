package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.exception.SubstitutionApplicationException
import kotlin.js.JsName

interface Applicable<T : Applicable<T>> {
    /**
     * Returns a fresh copy of this object, that is, an instance of [T] which is equal to the
     * current one in any aspect, except for variables directly or indirectly contained into this object, which are refreshed.
     * This means the method could return this object itself, if no variable is present, or a new object with freshly
     * generated variables.
     *
     * Variables are refreshed consistently, meaning that, if more variables exists within this object having the same
     * name, all fresh copies of such variables will have the same complete name.
     *
     * Example: "f(X, g(X))".freshCopy() returns something like "f(X_1, g(X_1))" instead of "f(X_1, g(X_2))"
     *
     * Notice that, if the current object is ground, the same object may be returned as a result by this method.
     *
     * @return a fresh copy of the current object which is different because variables are consistently renamed
     */
    @JsName("freshCopy")
    fun freshCopy(): T

    /**
     * Returns a fresh copy of this object, similarly to `freshCopy()`, possibly reusing variables from the provided
     * scope, if any
     *
     * @see freshCopy
     * @param scope the [Scope] containing variables to be used in copying
     * @return a fresh copy of the current object which is different because variables are consistently renamed
     */
    @JsName("freshCopyFromScope")
    fun freshCopy(scope: Scope): T

    /**
     * Applies a [Substitution] to the current object, producing a new instance of [T] which differs from the current
     * object because variables are replaced by their values, according to the binding carried by [substitution].
     *
     * Notice that, if the current object is ground, or the provided substitution is empty,
     * the same object may be returned as a result by this method.
     *
     * @param substitution is the [Substitution] to be applied to the current object
     * @return an instance of [T] where variables in [substitution] are replaced by their values
     * @throws [SubstitutionApplicationException] if the provided substitution is of type [Substitution.Fail]
     */
    @JsName("applySubstitution")
    fun apply(substitution: Substitution): T

    /**
     * Applies one or more [Substitution]s to the current object, producing a new instance of [T] which differs
     * from the current one because variables are replaced by their values, according to the binding carried by
     * the provided substitutions.
     *
     * This method behaves like `apply(Substitution)`, assuming that the provided substitutions have been merged
     * by means of [Substitution.merge].
     *
     * @param substitution is the first [Substitution] to be applied to the current object
     * @param substitutions is the vararg argument representing the 2nd, 3rd, etc., [Substitution]s to be applied
     * @return an instance of [T] where variables in [substitution] are replaced by their values
     * @throws [SubstitutionApplicationException] if the composition of the provided substitutions is of type [Substitution.Fail]
     *
     * @see apply
     * @see Substitution.merge
     */
    @JsName("apply")
    fun apply(
        substitution: Substitution,
        vararg substitutions: Substitution,
    ): T = apply(Substitution.merge(substitution, *substitutions))

    /**
     * This is an alias for [apply] aimed at supporting a square-brackets syntax for substitutions applications in
     * Kotlin programs.
     * It lets programmers write `object[substitution]` instead of `object.apply(substitution)`.
     * It applies one or more [Substitution]s to the current object, producing a new [Term] which differs from the current
     * one because variables are replaced by their values, according to the binding carried by the provided substitutions.
     *
     * This method behaves like [apply], assuming that the provided substitutions have been merged
     * by means of [Substitution.merge].
     *
     * @param substitution is the first [Substitution] to be applied to the current object
     * @param substitutions is the vararg argument representing the 2nd, 3rd, etc., [Substitution]s to be applied
     * @return an instance of [T] where variables in [substitution] are replaced by their values
     * @throws [SubstitutionApplicationException] if the composition of the provided substitutions is of type [Substitution.Fail]
     *
     * @see apply
     * @see Substitution.merge
     */
    @JsName("getSubstituted")
    operator fun get(
        substitution: Substitution,
        vararg substitutions: Substitution,
    ): T = apply(substitution, *substitutions)
}
