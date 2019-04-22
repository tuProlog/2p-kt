package it.unibo.tuprolog.core

interface EmptySet : Empty, Set {



    override val args: Array<Term>
        get() = super.args

    override val value: String
        get() = super.value

    companion object {
        operator fun invoke(): EmptySet {
            return EmptySetImpl
        }
    }
}

