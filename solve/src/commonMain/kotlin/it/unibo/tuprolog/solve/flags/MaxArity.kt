package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Integer

/**
 * The ISO Prolog `max_arity` flag, reporting the maximum arity a [it.unibo.tuprolog.solve.Signature] may have.
 * Not [isEditable]: 2P-Kt reports [Int.MAX_VALUE] as its only admissible value, i.e. no practical limit is enforced.
 */
@Suppress("MemberVisibilityCanBePrivate")
object MaxArity : NotableFlag {
    override val help: String =
        """
        `flag(max_arity)`
        
        Reports the maximum predicate/function arity supported by this implementation.
        
        - Value: `2147483647` (`Int.MAX_VALUE`)
        - Default: the same value
        - Editable: no
        
        The value effectively means that 2P-Kt imposes no practical arity limit below the host integer bound.

        **Examples**

        ```prolog
        ?- current_prolog_flag(max_arity, V).
        V = 2147483647.

        ?- set_prolog_flag(max_arity, 10).
        throws error(permission_error(modify, flag, max_arity), _).
        ```
        """.trimIndent()

    override val name: String = "max_arity"

    override val defaultValue: Integer = Integer.of(Int.MAX_VALUE)

    override val admissibleValues = FlagDomain.Singleton(defaultValue)

    override val isEditable: Boolean
        get() = false
}
