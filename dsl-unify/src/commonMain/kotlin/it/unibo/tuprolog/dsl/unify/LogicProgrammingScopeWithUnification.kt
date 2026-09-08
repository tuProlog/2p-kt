package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

/**
 * Adds unification-related DSL sugar to [LogicProgrammingScopeWithUnificator]: every [Unificator] operation gets an
 * overload accepting plain `Any` operands (auto-[toTerm]-ed, exactly like the rest of the `:dsl-core` DSL), plus
 * infix aliases mirroring [it.unibo.tuprolog.unify.Unificator.Companion]'s own `Term`-only infix functions
 * ([it.unibo.tuprolog.unify.Unificator.Companion.mguWith], [it.unibo.tuprolog.unify.Unificator.Companion.matches],
 * [it.unibo.tuprolog.unify.Unificator.Companion.unifyWith]) but resolved against *this scope's* [unificator]
 * instead of always [Unificator.default]:
 * ```kotlin
 * logicProgramming {
 *     val x = varOf("X")
 *     x matches "a"                 // true  -- equivalent to unificator.match(x, atomOf("a"))
 *     val substitution = x mguWith "a" // {X = a}, an [Substitution.Unifier]
 *     x unifyWith "a"                // a, the [Term] resulting from applying the mgu to `x`
 * }
 * ```
 * All three infix functions, as well as the `Any`-accepting [mgu]/[match]/[unify] overloads, perform unification
 * with occurs-check enabled; call [mgu]/[match]/[unify] directly (optionally passing `occurCheckEnabled = false`)
 * for finer control.
 *
 * @param S the concrete, self-referential scope type (see [it.unibo.tuprolog.dsl.BaseLogicProgrammingScope]).
 */
interface LogicProgrammingScopeWithUnification<S : LogicProgrammingScopeWithUnification<S>> :
    LogicProgrammingScopeWithUnificator<S>,
    Unificator {
    /**
     * Infix alias of [mgu] for `Any` operands: computes the Most General Unifier of `this` and [other] (both
     * auto-[toTerm]-ed), with occurs-check enabled, using this scope's [unificator].
     * @throws IllegalArgumentException if `this` or [other] cannot be converted into a [Term] (see [toTerm]).
     */
    @JsName("anyMguWith")
    infix fun Any.mguWith(other: Any): Substitution =
        this@LogicProgrammingScopeWithUnification.mgu(this.toTerm(), other.toTerm())

    /**
     * Infix alias of [match] for `Any` operands: tells whether `this` and [other] (both auto-[toTerm]-ed) unify,
     * with occurs-check enabled, using this scope's [unificator].
     * @throws IllegalArgumentException if `this` or [other] cannot be converted into a [Term] (see [toTerm]).
     */
    @JsName("anyMatches")
    infix fun Any.matches(other: Any): Boolean =
        this@LogicProgrammingScopeWithUnification.match(this.toTerm(), other.toTerm())

    /**
     * Infix alias of [unify] for `Any` operands: unifies `this` and [other] (both auto-[toTerm]-ed), with
     * occurs-check enabled, using this scope's [unificator].
     * @return the result of applying the computed MGU to `this` (retaining its orientation), or `null` if `this`
     * and [other] do not unify.
     * @throws IllegalArgumentException if `this` or [other] cannot be converted into a [Term] (see [toTerm]).
     */
    @JsName("anyUnifyWith")
    infix fun Any.unifyWith(other: Any): Term? =
        this@LogicProgrammingScopeWithUnification.unify(this.toTerm(), other.toTerm())

    /**
     * Overload of [Unificator.mgu] accepting plain values instead of [Term]s: [term1] and [term2] are auto-[toTerm]-ed
     * before delegating to this scope's [unificator], so e.g. raw [String]s, numbers or already-built [Term]s can be
     * mixed freely.
     * @throws IllegalArgumentException if [term1] or [term2] cannot be converted into a [Term] (see [toTerm]).
     */
    @JsName("mguAny")
    fun mgu(
        term1: Any,
        term2: Any,
        occurCheckEnabled: Boolean = true,
    ): Substitution = mgu(term1.toTerm(), term2.toTerm(), occurCheckEnabled)

    /**
     * Overload of [Unificator.match] accepting plain values instead of [Term]s: [term1] and [term2] are
     * auto-[toTerm]-ed before delegating to this scope's [unificator].
     * @throws IllegalArgumentException if [term1] or [term2] cannot be converted into a [Term] (see [toTerm]).
     */
    @JsName("matchAny")
    fun match(
        term1: Any,
        term2: Any,
        occurCheckEnabled: Boolean = true,
    ): Boolean = match(term1.toTerm(), term2.toTerm(), occurCheckEnabled)

    /**
     * Overload of [Unificator.unify] accepting plain values instead of [Term]s: [term1] and [term2] are
     * auto-[toTerm]-ed before delegating to this scope's [unificator].
     * @throws IllegalArgumentException if [term1] or [term2] cannot be converted into a [Term] (see [toTerm]).
     */
    @JsName("unifyAny")
    fun unify(
        term1: Any,
        term2: Any,
        occurCheckEnabled: Boolean = true,
    ): Term? = unify(term1.toTerm(), term2.toTerm(), occurCheckEnabled)
}
