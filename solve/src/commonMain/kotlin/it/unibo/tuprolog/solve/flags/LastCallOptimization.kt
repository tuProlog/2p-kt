package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField

/**
 * An implementation-specific flag controlling whether the solver performs last-call (tail-call) optimization, i.e.
 * avoids growing its internal execution-context stack on genuine tail calls. Defaults to [ON].
 */
@Suppress("MemberVisibilityCanBePrivate")
object LastCallOptimization : NotableFlag {
    override val help: String =
        """
        `flag(last_call_optimization)`
        
        Controls last-call (tail-call) optimization in the solver.
        
        - `on`: eligible tail calls avoid growing the execution-context stack.
        - `off`: tail calls use ordinary context chaining.
        - Default: `on`
        - Editable: yes

        **Examples**

        ```prolog
        ?- current_prolog_flag(last_call_optimization, V).
        V = on.

        ?- set_prolog_flag(last_call_optimization, off), current_prolog_flag(last_call_optimization, V).
        V = off.

        ?- set_prolog_flag(last_call_optimization, maybe).
        throws error(domain_error(_, maybe), _).
        ```
        """.trimIndent()

    /** Tail-call optimization is enabled. */
    @JvmField
    val ON = Atom.of("on")

    /** Tail-call optimization is disabled. */
    @JvmField
    val OFF = Atom.of("off")

    override val name: String = "last_call_optimization"

    override val defaultValue: Term
        get() = ON

    override val admissibleValues = FlagDomain.Enumerated(ON, OFF)
}
