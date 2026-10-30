package it.unibo.tuprolog.core.operators.testutils

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier

/**
 * Utils singleton to help testing [OperatorSet]
 *
 * @author Enrico
 */
internal object OperatorSetUtils {
    /** A named [Operator] functor, specifier and priority triple, for parameterized tests */
    internal data class OperatorCase(
        val id: String,
        val functor: String,
        val specifier: Specifier,
        val priority: Int,
    )

    /** The reference "table" of [Operator]s to test if they are stored correctly */
    internal val defaultOperatorCases by lazy {
        listOf(
            OperatorCase("fy-plus", "+", Specifier.FY, 200),
            OperatorCase("fy-minus", "-", Specifier.FY, 200),
            OperatorCase("fy-bitwise-not", "\\", Specifier.FY, 200),
            OperatorCase("xfy-power", "^", Specifier.XFY, 200),
            OperatorCase("xfx-float-power", "**", Specifier.XFX, 200),
            OperatorCase("yfx-times", "*", Specifier.YFX, 400),
            OperatorCase("yfx-divide", "/", Specifier.YFX, 400),
            OperatorCase("yfx-int-divide", "//", Specifier.YFX, 400),
            OperatorCase("yfx-rem", "rem", Specifier.YFX, 400),
            OperatorCase("yfx-mod", "mod", Specifier.YFX, 400),
            OperatorCase("yfx-shift-left", "<<", Specifier.YFX, 400),
            OperatorCase("yfx-shift-right", ">>", Specifier.YFX, 400),
            OperatorCase("yfx-plus", "+", Specifier.YFX, 500),
            OperatorCase("yfx-minus", "-", Specifier.YFX, 500),
            OperatorCase("yfx-bitwise-or", "\\/", Specifier.YFX, 500),
            OperatorCase("yfx-bitwise-and", "/\\", Specifier.YFX, 500),
            OperatorCase("xfx-unify", "=", Specifier.XFX, 700),
            OperatorCase("xfx-not-unify", "\\=", Specifier.XFX, 700),
            OperatorCase("xfx-identical", "==", Specifier.XFX, 700),
            OperatorCase("xfx-not-identical", "\\==", Specifier.XFX, 700),
            OperatorCase("xfx-term-less", "@<", Specifier.XFX, 700),
            OperatorCase("xfx-term-less-or-equal", "@=<", Specifier.XFX, 700),
            OperatorCase("xfx-term-greater", "@>", Specifier.XFX, 700),
            OperatorCase("xfx-term-greater-or-equal", "@>=", Specifier.XFX, 700),
            OperatorCase("xfx-univ", "=..", Specifier.XFX, 700),
            OperatorCase("xfx-is", "is", Specifier.XFX, 700),
            OperatorCase("xfx-arithmetic-equal", "=:=", Specifier.XFX, 700),
            OperatorCase("xfx-arithmetic-not-equal", "=\\=", Specifier.XFX, 700),
            OperatorCase("xfx-less", "<", Specifier.XFX, 700),
            OperatorCase("xfx-less-or-equal", "=<", Specifier.XFX, 700),
            OperatorCase("xfx-greater", ">", Specifier.XFX, 700),
            OperatorCase("xfx-greater-or-equal", ">=", Specifier.XFX, 700),
            OperatorCase("xfy-comma", ",", Specifier.XFY, 1000),
            OperatorCase("xfy-if-then", "->", Specifier.XFY, 1050),
            OperatorCase("xfy-semicolon", ";", Specifier.XFY, 1100),
            OperatorCase("fy-not-provable", "\\+", Specifier.FY, 900),
            OperatorCase("fx-directive", ":-", Specifier.FX, 1200),
            OperatorCase("fx-query", "?-", Specifier.FX, 1200),
            OperatorCase("xfx-rule", ":-", Specifier.XFX, 1200),
            OperatorCase("xfx-dcg-rule", "-->", Specifier.XFX, 1200),
        )
    }
}
