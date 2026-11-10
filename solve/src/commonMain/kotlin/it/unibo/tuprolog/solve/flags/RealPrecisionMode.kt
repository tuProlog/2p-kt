package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField

/**
 * An implementation-specific flag controlling whether arithmetic on reals is exact or rounded, to the number of
 * digits [RealPrecisionDigits] sets and in the way [RealRoundingMode] sets. Defaults to [UNLIMITED_WITH_FALLBACK].
 */
@Suppress("MemberVisibilityCanBePrivate")
object RealPrecisionMode : NotableFlag {
    override val help: String =
        """
        `flag(real_precision_mode)`

        Controls whether arithmetic on reals is exact or rounded.

        - `unlimited_with_fallback`: results are exact when they can be, and rounded to `real_precision_digits` digits otherwise (e.g. `1 / 3`, `sqrt(2)`, `sin(1)`).
        - `limited`: results are always rounded to `real_precision_digits` digits, so the digits of a real never grow beyond that.
        - `unlimited`: results are always exact; an operation whose result can't be exact raises `evaluation_error(undefined)`.
        - In every mode, `sqrt/1` is rounded to `real_precision_digits` digits.
        - Default: `unlimited_with_fallback`
        - Editable: yes

        Exact sums and products keep every digit of their operands, so long chains of multiplications can grow without bound: use `limited` to cap them.

        **Examples**

        ```prolog
        ?- X is 1 / 4.
        X = 0.25.

        ?- set_prolog_flag(real_precision_digits, 3), X is 1.2345 * 2.
        X = 2.4690.

        ?- set_prolog_flag(real_precision_digits, 3), set_prolog_flag(real_precision_mode, limited), X is 1.2345 * 2.
        X = 2.47.

        ?- set_prolog_flag(real_precision_mode, unlimited), X is 1 / 3.
        throws error(evaluation_error(undefined), _).
        ```
        """.trimIndent()

    /** Results are always exact: an operation whose result can't be exact raises an evaluation error. */
    @JvmField
    val UNLIMITED = Atom.of("unlimited")

    /** Results are always rounded to [RealPrecisionDigits] digits. */
    @JvmField
    val LIMITED = Atom.of("limited")

    /** Results are exact when they can be, and rounded to [RealPrecisionDigits] digits otherwise. */
    @JvmField
    val UNLIMITED_WITH_FALLBACK = Atom.of("unlimited_with_fallback")

    override val name: String = "real_precision_mode"

    override val defaultValue: Term
        get() = UNLIMITED_WITH_FALLBACK

    override val admissibleValues = FlagDomain.Enumerated(UNLIMITED, LIMITED, UNLIMITED_WITH_FALLBACK)
}
