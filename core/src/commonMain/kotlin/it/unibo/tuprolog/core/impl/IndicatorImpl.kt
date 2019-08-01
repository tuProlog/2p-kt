package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Term

/**
 * [Indicator] default implementation
 *
 * @author Enrico
 */
internal class IndicatorImpl(override val indicatedName: Term, override val indicatedArity: Term)
    : StructImpl(Indicator.FUNCTOR, arrayOf(indicatedName, indicatedArity)), Indicator {

    override val functor: String = Indicator.FUNCTOR

    override val args: Array<Term> by lazy { super<StructImpl>.args }

    override fun toString(): String = "$indicatedName${Indicator.FUNCTOR}$indicatedArity"
}
