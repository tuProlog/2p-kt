package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider
import it.unibo.tuprolog.dsl.Termificator
import it.unibo.tuprolog.theory.TheoryFactory
import it.unibo.tuprolog.unify.Unificator

/**
 * Default, stateless implementation of [LogicProgrammingScope]: forwards [VariablesProvider], [Unificator] and
 * [TheoryFactory] operations to [variablesProvider], [unificator] and [theoryFactory] respectively (via Kotlin
 * delegation), and every other scope operation up its supertypes' default implementations. Obtained through
 * [LogicProgrammingScope.of]/[LogicProgrammingScope.empty] rather than constructed directly in typical usage.
 *
 * @throws IllegalArgumentException if [scope] is not the same object as both [termificator]'s and
 * [variablesProvider]'s scope, or if [unificator] is not equal to [theoryFactory]'s.
 */
class LogicProgrammingScopeImpl(
    override val scope: Scope,
    override val termificator: Termificator,
    override val variablesProvider: VariablesProvider,
    override val unificator: Unificator,
    override val theoryFactory: TheoryFactory,
) : LogicProgrammingScope,
    VariablesProvider by variablesProvider,
    Unificator by unificator,
    TheoryFactory by theoryFactory {
    init {
        require(scope === variablesProvider.scope && scope === termificator.scope) {
            "The provided Scope should be the same object for both Termificator and VariablesProvider"
        }
        require(unificator == theoryFactory.unificator) {
            "The provided Unificator should be the same object for both Unificator and TheoryFactory"
        }
    }

    /** Copies this scope onto [scope], propagating it to [termificator] and [variablesProvider] as well. */
    override fun copy(scope: Scope): LogicProgrammingScope =
        LogicProgrammingScopeImpl(
            scope,
            termificator.copy(scope),
            variablesProvider.copy(scope),
            unificator,
            theoryFactory,
        )

    /** Copies this scope onto [unificator], propagating it to [theoryFactory] as well. */
    override fun copy(unificator: Unificator): LogicProgrammingScope =
        LogicProgrammingScopeImpl(
            scope,
            termificator,
            variablesProvider,
            unificator,
            theoryFactory.copy(unificator),
        )

    /** Creates a fresh [LogicProgrammingScope], backed by a brand-new, empty [Scope]; see [copy]. */
    override fun newScope(): LogicProgrammingScope = copy(Scope.empty())
}
