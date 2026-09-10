package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Integer

/**
 * The ISO Prolog `max_arity` flag, reporting the maximum arity a [it.unibo.tuprolog.solve.Signature] may have.
 * Not [isEditable]: 2P-Kt reports [Int.MAX_VALUE] as its only admissible value, i.e. no practical limit is enforced.
 */
@Suppress("MemberVisibilityCanBePrivate")
object MaxArity : NotableFlag {
    override val name: String = "max_arity"

    override val defaultValue: Integer = Integer.of(Int.MAX_VALUE)

    override val admissibleValues: Sequence<Integer> = sequenceOf(defaultValue)

    override val isEditable: Boolean
        get() = false
}
