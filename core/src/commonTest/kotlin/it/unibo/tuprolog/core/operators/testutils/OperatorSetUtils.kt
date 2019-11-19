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

    /** The reference "table" of [Operator]s to test if they are stored correctly */
    internal val defaultOperators by lazy {
        listOf(
            Triple("+", Specifier.FY, 200),
            Triple("-", Specifier.FY, 200),
            Triple("\\", Specifier.FY, 200),
            Triple("^", Specifier.XFY, 200),
            Triple("**", Specifier.XFX, 200),
            Triple("*", Specifier.YFX, 400),
            Triple("/", Specifier.YFX, 400),
            Triple("//", Specifier.YFX, 400),
            Triple("rem", Specifier.YFX, 400),
            Triple("mod", Specifier.YFX, 400),
            Triple("<<", Specifier.YFX, 400),
            Triple(">>", Specifier.YFX, 400),
            Triple("+", Specifier.YFX, 500),
            Triple("-", Specifier.YFX, 500),
            Triple("\\/", Specifier.YFX, 500),
            Triple("/\\", Specifier.YFX, 500),
            Triple("=", Specifier.XFX, 700),
            Triple("\\=", Specifier.XFX, 700),
            Triple("==", Specifier.XFX, 700),
            Triple("\\==", Specifier.XFX, 700),
            Triple("@<", Specifier.XFX, 700),
            Triple("@=<", Specifier.XFX, 700),
            Triple("@>", Specifier.XFX, 700),
            Triple("@>=", Specifier.XFX, 700),
            Triple("=..", Specifier.XFX, 700),
            Triple("is", Specifier.XFX, 700),
            Triple("=:=", Specifier.XFX, 700),
            Triple("=\\=", Specifier.XFX, 700),
            Triple("<", Specifier.XFX, 700),
            Triple("=<", Specifier.XFX, 700),
            Triple(">", Specifier.XFX, 700),
            Triple(">=", Specifier.XFX, 700),
            Triple(",", Specifier.XFY, 1000),
            Triple("->", Specifier.XFY, 1050),
            Triple(";", Specifier.XFY, 1100),
            Triple("\\+", Specifier.FY, 900),
            Triple(":-", Specifier.FX, 1200),
            Triple("?-", Specifier.FX, 1200),
            Triple(":-", Specifier.XFX, 1200),
            Triple("-->", Specifier.XFX, 1200)
        )
    }
}
