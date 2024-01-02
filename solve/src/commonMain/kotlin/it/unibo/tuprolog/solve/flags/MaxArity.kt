package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Integer

@Suppress("MemberVisibilityCanBePrivate")
object MaxArity : NotableFlag {
    override val name: String = "max_arity"

    override val defaultValue: Integer = Integer.of(Int.MAX_VALUE)

    override val admissibleValues: Sequence<Integer> = sequenceOf(defaultValue)

    override val isEditable: Boolean
        get() = false
}
