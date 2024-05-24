package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.Termificator
import it.unibo.tuprolog.solve.libs.oop.OOPContext
import it.unibo.tuprolog.solve.libs.oop.Objectifier
import it.unibo.tuprolog.solve.libs.oop.TypeFactory

internal open class BaseOOPContext(
    override val termificator: Termificator,
    override val objectifier: Objectifier,
    override val typeFactory: TypeFactory
) : OOPContext
