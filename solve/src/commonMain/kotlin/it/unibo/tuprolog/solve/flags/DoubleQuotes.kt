package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField

/**
 * The ISO Prolog `double_quotes` flag, controlling how double-quoted text is parsed.
 *
 * 2P-Kt currently only supports the [ATOM] behaviour (double-quoted text is read as a single atom); the standard
 * `chars`/`codes` variants are not offered as [admissibleValues].
 */
@Suppress("MemberVisibilityCanBePrivate")
object DoubleQuotes : NotableFlag {
    override val help: String =
        """
        `flag(double_quotes)`
        
        Controls how double-quoted text is parsed.
        
        - Admissible value: `atom`
        - Default: `atom`
        - Editable: yes
        
        2P-Kt currently supports only atom semantics for double-quoted text; the ISO `chars` and `codes` alternatives are not available.

        **Examples**

        ```prolog
        ?- current_prolog_flag(double_quotes, V).
        V = atom.

        ?- X = "hello", atom(X).
        yes.

        ?- set_prolog_flag(double_quotes, codes).
        throws error(domain_error(_, codes), _).
        ```
        """.trimIndent()

    /** The (only) admissible/default value: double-quoted text is parsed as an [Atom]. */
    @JvmField
    val ATOM = Atom.of("atom")

    override val name: String = "double_quotes"

    override val defaultValue: Term
        get() = ATOM

    override val admissibleValues = FlagDomain.Singleton(ATOM)
}
