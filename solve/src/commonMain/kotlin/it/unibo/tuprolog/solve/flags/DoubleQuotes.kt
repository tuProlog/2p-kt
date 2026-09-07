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
    /** The (only) admissible/default value: double-quoted text is parsed as an [Atom]. */
    @JvmField
    val ATOM = Atom.of("atom")

    override val name: String = "double_quotes"

    override val defaultValue: Term
        get() = ATOM

    override val admissibleValues: Sequence<Term> = sequenceOf(ATOM)
}
