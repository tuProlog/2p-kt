package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField

/**
 * A deprecated, implementation-specific flag that no longer has any effect: the solver always keeps every binding a
 * resolution still needs, whatever this flag says (#1078). It is not part of [FlagStore.DEFAULT] anymore, and it is
 * retained only for backward compatibility, so that programs and code setting it keep working. Do not use it.
 */
@Deprecated(
    "track_variables has no effect: the solver always keeps the bindings it needs. It is retained only for " +
        "backward compatibility, so stop setting it.",
)
@Suppress("MemberVisibilityCanBePrivate")
object TrackVariables : NotableFlag {
    override val help: String =
        """
        `flag(track_variables)`
        
        **Deprecated, and useless: do not use it.** It has no effect on resolution: the solver always keeps every
        variable binding it still needs, whether this flag is `on` or `off`. It is retained only for backward
        compatibility, so that programs setting it keep working, and it is no longer among the default flags.

        - `on`, `off`: both accepted, neither changes anything.
        - Default: not set (its nominal default is `off`)
        - Editable: yes

        **Examples**

        ```prolog
        ?- set_prolog_flag(track_variables, on), current_prolog_flag(track_variables, V).
        V = on.
        ```
        """.trimIndent()

    /** Variable tracking is enabled. */
    @JvmField
    val ON = Atom.of("on")

    /** Variable tracking is disabled. */
    @JvmField
    val OFF = Atom.of("off")

    override val name: String = "track_variables"

    override val defaultValue: Term
        get() = OFF

    override val admissibleValues = FlagDomain.Enumerated(ON, OFF)
}
