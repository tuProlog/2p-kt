package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term

internal expect class DefaultTermificator(
    scope: Scope,
    novel: Boolean = false,
) : AbstractTermificator {
    override fun handleNumberAsNumeric(value: Number): Term

    override fun copy(scope: Scope): Termificator
}
