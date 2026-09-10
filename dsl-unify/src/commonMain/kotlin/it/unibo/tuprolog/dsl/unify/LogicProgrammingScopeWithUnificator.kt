package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.dsl.BaseLogicProgrammingScope
import it.unibo.tuprolog.unify.UnificationAware
import it.unibo.tuprolog.unify.Unificator

/**
 * Bare mixin wiring a [Unificator] into a `:dsl-core` scope: extends [BaseLogicProgrammingScope] with
 * [UnificationAware] (exposing the [unificator] backing this scope) and [Unificator] itself, so [Unificator.mgu],
 * [Unificator.match], [Unificator.unify], [Unificator.merge] and [Unificator.context] are callable directly on the
 * scope, without going through [unificator] explicitly. Implementations are expected to forward these to
 * [unificator] (e.g. via `Unificator by unificator`, as [LogicProgrammingScopeImpl] does) rather than implement
 * unification logic of their own.
 *
 * This interface only makes the *plain*, [it.unibo.tuprolog.core.Term]-based [Unificator] operations available;
 * [LogicProgrammingScopeWithUnification] builds the DSL's `Any`-accepting/infix sugar (`mguWith`, `matches`,
 * `unifyWith`, ...) on top of it.
 *
 * @param S the concrete, self-referential scope type (see [BaseLogicProgrammingScope]) that [copy] returns.
 */
interface LogicProgrammingScopeWithUnificator<S : LogicProgrammingScopeWithUnificator<S>> :
    BaseLogicProgrammingScope<S>,
    UnificationAware,
    Unificator {
    /** Creates a copy of this scope, backed by the same [it.unibo.tuprolog.core.Scope], using [unificator] instead of the current one. */
    fun copy(unificator: Unificator): S
}
