@file:JvmName("DSL")

package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.theory.IndexedTheoryFactory
import it.unibo.tuprolog.theory.TheoryFactory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmName
import it.unibo.tuprolog.dsl.LogicProgrammingScope as CoreLogicProgrammingScope
import it.unibo.tuprolog.dsl.unify.LogicProgrammingScope as UnifyLogicProgrammingScope

/**
 * Entry point of `:dsl-theory`'s Prolog DSL: like `:dsl-unify`'s
 * [it.unibo.tuprolog.dsl.unify.logicProgramming], but the receiver is a [LogicProgrammingScope] — so
 * [it.unibo.tuprolog.theory.Theory]/[it.unibo.tuprolog.theory.MutableTheory]-building sugar
 * ([LogicProgrammingScopeWithTheories.theory], [LogicProgrammingScopeWithTheories.mutableTheory]) is available
 * inside [function] alongside the rest of the DSL — using [unificator] (an empty-context
 * [it.unibo.tuprolog.unify.Unificator.strict] strategy by default) both for unification and (via an
 * [IndexedTheoryFactory] built on it) for the theories it builds:
 * ```kotlin
 * val theory = logicProgramming { theory({ factOf(structOf("a")) }) }
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
 * Upgrades this `:dsl-core` scope into a `:dsl-theory` [LogicProgrammingScope] adding unification and
 * theory-building support to it, reusing its underlying [it.unibo.tuprolog.core.Scope],
 * [it.unibo.tuprolog.dsl.Termificator] and [it.unibo.tuprolog.core.VariablesProvider] (so
 * [it.unibo.tuprolog.core.Var]s and terms already built through this scope remain valid and usable in the returned
 * one) and equipping it with [unificator] ([LogicProgrammingScope.defaultUnificator] unless stated otherwise) as
 * its unification strategy and an [IndexedTheoryFactory] built on [unificator] as its [TheoryFactory]. Useful when
 * a plain `:dsl-core` scope is already at hand (e.g. received as a parameter) and unification/theory-building are
 * only needed from that point on.
 */
fun CoreLogicProgrammingScope.withTheories(unificator: Unificator = LogicProgrammingScope.defaultUnificator) =
    LogicProgrammingScope.of(unificator, IndexedTheoryFactory(unificator), scope, termificator, variablesProvider)

/**
 * Upgrades this `:dsl-unify` scope into a `:dsl-theory` [LogicProgrammingScope] adding theory-building support to
 * it, reusing its underlying [it.unibo.tuprolog.core.Scope], [it.unibo.tuprolog.dsl.Termificator] and
 * [it.unibo.tuprolog.core.VariablesProvider], and equipping it with [unificator] ([LogicProgrammingScope.defaultUnificator]
 * unless stated otherwise — *not* necessarily this scope's own [it.unibo.tuprolog.unify.UnificationAware.unificator],
 * so pass it explicitly to preserve it) and [theoryFactory] (an [IndexedTheoryFactory] over [unificator] by
 * default). Useful when a `:dsl-unify` scope is already at hand and theory-building is only needed from that point on.
 */
fun UnifyLogicProgrammingScope.withTheories(
    unificator: Unificator = LogicProgrammingScope.defaultUnificator,
    theoryFactory: TheoryFactory = IndexedTheoryFactory(unificator),
) = LogicProgrammingScope.of(unificator, theoryFactory, scope, termificator, variablesProvider)
