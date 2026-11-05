package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Integer

/**
 * An implementation-specific flag setting the number of significant digits reals are rounded to, whenever
 * [RealPrecisionMode] calls for rounding. Defaults to `16`, as IEEE 754 decimal64.
 */
object RealPrecisionDigits : NotableFlag {
    override val help: String =
        """
        `flag(real_precision_digits)`

        Sets the number of significant digits reals are rounded to, whenever `real_precision_mode` calls for rounding.

        - Admissible values: any positive integer
        - Default: `16`, as IEEE 754 decimal64
        - Editable: yes

        **Examples**

        ```prolog
        ?- X is 1 / 3.
        X = 0.3333333333333333.

        ?- set_prolog_flag(real_precision_digits, 4), X is 1 / 3.
        X = 0.3333.
        ```
        """.trimIndent()

    override val name: String = "real_precision_digits"

    override val defaultValue: Integer = Integer.of(DEFAULT_DIGITS)

    override val admissibleValues = FlagDomain.IntRange(1, Int.MAX_VALUE)
}

private const val DEFAULT_DIGITS = 16
