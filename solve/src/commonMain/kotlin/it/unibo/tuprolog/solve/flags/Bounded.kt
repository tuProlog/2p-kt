package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Truth

/**
 * The ISO Prolog `bounded` flag, reporting whether integers are bounded. Not [isEditable]: 2P-Kt's integers are
 * arbitrary-precision, so its only admissible value is `false` (and, as ISO allows in that case, there are no
 * `max_integer`/`min_integer` flags).
 */
@Suppress("MemberVisibilityCanBePrivate")
object Bounded : NotableFlag {
    override val help: String =
        """
        `flag(bounded)`
        
        Reports whether integers are bounded.
        
        - Value: `false`, since integers have arbitrary precision
        - Default: the same value
        - Editable: no

        **Examples**

        ```prolog
        ?- current_prolog_flag(bounded, V).
        V = false.

        ?- set_prolog_flag(bounded, true).
        throws error(permission_error(modify, flag, bounded), _).
        ```
        """.trimIndent()

    override val name: String = "bounded"

    override val defaultValue: Truth = Truth.FALSE

    override val admissibleValues = FlagDomain.Singleton(defaultValue)

    override val isEditable: Boolean
        get() = false
}
