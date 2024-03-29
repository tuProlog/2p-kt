package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider
import it.unibo.tuprolog.dsl.Termificator
import it.unibo.tuprolog.theory.TheoryFactory
import it.unibo.tuprolog.unify.Unificator

class LogicProgrammingScopeImpl private constructor(
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

    private lateinit var termificatorFactory: (Scope) -> Termificator
    private lateinit var variablesProviderFactory: (Scope) -> VariablesProvider
    private lateinit var theoryFactoryFactory: (Unificator) -> TheoryFactory

    constructor(
        scope: Scope,
        termificatorFactory: (Scope) -> Termificator,
        variablesProviderFactory: (Scope) -> VariablesProvider,
        unificator: Unificator,
        theoryFactoryFactory: (Unificator) -> TheoryFactory,
    ) : this(
        scope,
        termificatorFactory(scope),
        variablesProviderFactory(scope),
        unificator,
        theoryFactoryFactory(unificator),
    ) {
        this.termificatorFactory = termificatorFactory
        this.variablesProviderFactory = variablesProviderFactory
        this.theoryFactoryFactory = theoryFactoryFactory
    }

    override fun newScope(): LogicProgrammingScope =
        Scope.empty().let {
            LogicProgrammingScopeImpl(
                it,
                termificatorFactory(it),
                variablesProviderFactory(it),
                unificator,
                theoryFactoryFactory(unificator),
            )
        }
}
