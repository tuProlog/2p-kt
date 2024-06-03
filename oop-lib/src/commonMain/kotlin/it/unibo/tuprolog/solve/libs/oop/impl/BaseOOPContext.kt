package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.Termificator
import it.unibo.tuprolog.solve.libs.oop.OOPContext
import it.unibo.tuprolog.solve.libs.oop.Objectifier
import it.unibo.tuprolog.solve.libs.oop.OopTermFactory
import it.unibo.tuprolog.solve.libs.oop.TypeFactory

internal open class BaseOOPContext(
    final override val termificator: Termificator,
    final override val objectifier: Objectifier,
    final override val typeFactory: TypeFactory,
) : OOPContext {
    init {
        require(objectifier.typeFactory == typeFactory)
    }

    override val termFactory: OopTermFactory by lazy {
        OopTermFactory.of(typeFactory)
    }
}
