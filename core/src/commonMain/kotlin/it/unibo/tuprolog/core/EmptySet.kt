package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.EmptySetImpl
import it.unibo.tuprolog.core.Set as LogicSet

interface EmptySet : Empty, LogicSet {

    override val isEmptySet: Boolean
        get() = true

    override val args: Array<Term>
        get() = super.args

    override val value: String
        get() = super.value

    companion object {
        operator fun invoke(): EmptySet = EmptySetImpl
    }
}
