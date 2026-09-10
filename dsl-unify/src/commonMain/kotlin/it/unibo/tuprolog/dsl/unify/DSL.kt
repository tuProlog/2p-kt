@file:JvmName("DSL")

package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmName
import it.unibo.tuprolog.dsl.LogicProgrammingScope as CoreLogicProgrammingScope

/**
 * Entry point of `:dsl-unify`'s Prolog DSL: like `:dsl-core`'s [it.unibo.tuprolog.dsl.logicProgramming], but the
 * receiver is a [LogicProgrammingScope] — so unification ([it.unibo.tuprolog.unify.Unificator.mgu],
 * [it.unibo.tuprolog.unify.Unificator.match], [it.unibo.tuprolog.unify.Unificator.unify], or the
 * `mguWith`/`matches`/`unifyWith` sugar) is available inside [function] alongside the rest of the DSL — using
 * [unificator] (an empty-context [it.unibo.tuprolog.unify.Unificator.strict] strategy by default) as its unification
 * strategy:
 * ```kotlin
 * val substitution = logicProgramming { varOf("X") mguWith "a" } // {X = a}
 * ```
 */
@JsName("logicProgramming")
fun <R> logicProgramming(
    unificator: Unificator = LogicProgrammingScope.defaultUnificator,
    function: LogicProgrammingScope.() -> R,
): R = LogicProgrammingScope.of(unificator).function()

/** Shorthand for [logicProgramming]. */
@JsName("lp")
fun <R> lp(
    unificator: Unificator = LogicProgrammingScope.defaultUnificator,
    function: LogicProgrammingScope.() -> R,
): R = logicProgramming(unificator, function)

/** Deprecated alias of [logicProgramming]/[lp]; kept only for backwards compatibility. */
@Deprecated("Use `lp` or `logicProgramming` instead", ReplaceWith("lp(function)"))
@JsName("prolog")
fun <R> prolog(
    unificator: Unificator = LogicProgrammingScope.defaultUnificator,
    function: LogicProgrammingScope.() -> R,
): R = logicProgramming(unificator, function)

/**
 * Upgrades this `:dsl-core` scope into a `:dsl-unify` [LogicProgrammingScope] adding unification support to it,
 * reusing its underlying [it.unibo.tuprolog.core.Scope], [it.unibo.tuprolog.dsl.Termificator] and
 * [it.unibo.tuprolog.core.VariablesProvider] (so [it.unibo.tuprolog.core.Var]s and terms already built through this
 * scope remain valid and usable in the returned one) and equipping it with [unificator]
 * ([LogicProgrammingScope.defaultUnificator] unless stated otherwise) as its unification strategy. Useful when a
 * plain `:dsl-core` scope is already at hand (e.g. received as a parameter) and unification is only needed from
 * that point on.
 */
fun CoreLogicProgrammingScope.withUnification(unificator: Unificator = LogicProgrammingScope.defaultUnificator) =
    LogicProgrammingScope.of(unificator, scope, termificator, variablesProvider)
