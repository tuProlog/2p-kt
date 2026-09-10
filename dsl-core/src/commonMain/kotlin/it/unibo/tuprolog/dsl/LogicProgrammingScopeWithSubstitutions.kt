package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.js.JsName

/**
 * Adds builders and accessors for [Substitution]s, so a `Var to value` pair can be written directly (`"X" to 1`,
 * mirroring Kotlin's own [kotlin.to] for `Pair`s) and a built [Substitution] can be queried by a plain [Var]-like
 * value instead of a variable name/[Var] pair:
 * ```kotlin
 * logicProgramming {
 *     val subst = "X" to 1        // Substitution.Unifier {X = 1}
 *     subst["X"]                  // 1, looked up by variable name instead of by Var instance
 *     "X" in subst                // true
 * }
 * ```
 */
interface LogicProgrammingScopeWithSubstitutions<S : LogicProgrammingScopeWithSubstitutions<S>> :
    BaseLogicProgrammingScope<S> {
    /** Builds the [Substitution.Unifier] binding this [Var] to [termObject] (auto-[toTerm]-ed). */
    @JsName("varTo")
    infix fun Var.to(termObject: Any): Substitution.Unifier = Substitution.of(this, termObject.toTerm())

    /** Builds the [Substitution.Unifier] binding the [Var] named by this [String] (via [varOf]) to [termObject]. */
    @JsName("stringTo")
    infix fun String.to(termObject: Any): Substitution.Unifier = Substitution.of(varOf(this), termObject.toTerm())

    /**
     * Looks up the [Term] this [Substitution] binds [term] to, `null` if unbound.
     * @throws IllegalArgumentException if [term] does not [toTerm] into a [Var].
     */
    @JsName("substitutionGet")
    operator fun Substitution.get(term: Any): Term? =
        when (val t = term.toTerm()) {
            is Var -> this[t]
            else -> term.raiseErrorConvertingTo(Var::class)
        }

    /**
     * Checks whether this [Substitution] binds [term].
     * @throws IllegalArgumentException if [term] does not [toTerm] into a [Var].
     */
    @JsName("substitutionContainsKey")
    fun Substitution.containsKey(term: Any): Boolean =
        when (val t = term.toTerm()) {
            is Var -> this.containsKey(t)
            else -> term.raiseErrorConvertingTo(Var::class)
        }

    /**
     * Alias of [containsKey].
     * @throws IllegalArgumentException if [term] does not [toTerm] into a [Var].
     */
    @JsName("substitutionContains")
    operator fun Substitution.contains(term: Any): Boolean = containsKey(term)

    /** Checks whether [term] (auto-[toTerm]-ed) occurs as a bound value in this [Substitution]. */
    @JsName("substitutionContainsValue")
    fun Substitution.containsValue(term: Any): Boolean = this.containsValue(term.toTerm())
}
