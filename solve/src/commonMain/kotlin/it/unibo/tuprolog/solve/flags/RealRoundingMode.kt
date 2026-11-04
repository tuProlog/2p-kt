package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import org.gciatto.kt.math.RoundingMode
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * An implementation-specific flag setting how reals are rounded, whenever [RealPrecisionMode] calls for rounding.
 * Its values are the [RoundingMode]s, in lower case, except [RoundingMode.UNNECESSARY]: it would make any rounding
 * fail. Defaults to [HALF_EVEN], as IEEE 754.
 */
@Suppress("MemberVisibilityCanBePrivate")
object RealRoundingMode : NotableFlag {
    override val help: String =
        """
        `flag(real_rounding_mode)`

        Sets how reals are rounded, whenever `real_precision_mode` calls for rounding.

        - `up`, `down`: away from zero, towards zero.
        - `ceiling`, `floor`: towards positive infinity, towards negative infinity.
        - `half_up`, `half_down`, `half_even`: to the nearest neighbour, breaking ties away from zero, towards zero, or towards the even neighbour.
        - Default: `half_even`, as IEEE 754
        - Editable: yes

        This flag doesn't affect `round/1`, `floor/1`, `ceiling/1` and `truncate/1`, whose rounding ISO defines.

        **Examples**

        ```prolog
        ?- set_prolog_flag(real_precision_digits, 4), X is 2 / 3.
        X = 0.6667.

        ?- set_prolog_flag(real_precision_digits, 4), set_prolog_flag(real_rounding_mode, down), X is 2 / 3.
        X = 0.6666.
        ```
        """.trimIndent()

    @JvmField
    val UP = Atom.of("up")

    @JvmField
    val DOWN = Atom.of("down")

    @JvmField
    val CEILING = Atom.of("ceiling")

    @JvmField
    val FLOOR = Atom.of("floor")

    @JvmField
    val HALF_UP = Atom.of("half_up")

    @JvmField
    val HALF_DOWN = Atom.of("half_down")

    @JvmField
    val HALF_EVEN = Atom.of("half_even")

    override val name: String = "real_rounding_mode"

    override val defaultValue: Term
        get() = HALF_EVEN

    override val admissibleValues = FlagDomain.Enumerated(UP, DOWN, CEILING, FLOOR, HALF_UP, HALF_DOWN, HALF_EVEN)

    /** The [RoundingMode] [value] names, or `null` if [value] is not admissible. */
    @JvmStatic
    fun toRoundingMode(value: Term): RoundingMode? =
        value.takeIf { isAdmissibleValue(it) }?.let { RoundingMode.valueOf((it as Atom).value.uppercase()) }
}
