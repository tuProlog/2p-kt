package it.unibo.tuprolog.core.operators.testutils

import it.unibo.tuprolog.core.operators.Associativity
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet

/**
 * Utils singleton to help testing [OperatorSet]
 *
 * @author Enrico
 */
internal object OperatorSetUtils {

    /** The reference "table" of [Operator]s to test if they are stored correctly */
    internal val defaultOperators by lazy {
        listOf(
                Triple("+", Associativity.FY, 200),
                Triple("-", Associativity.FY, 200),
                Triple("\\", Associativity.FY, 200),
                Triple("^", Associativity.XFY, 200),
                Triple("**", Associativity.XFX, 200),
                Triple("*", Associativity.YFX, 400),
                Triple("/", Associativity.YFX, 400),
                Triple("//", Associativity.YFX, 400),
                Triple("rem", Associativity.YFX, 400),
                Triple("mod", Associativity.YFX, 400),
                Triple("<<", Associativity.YFX, 400),
                Triple(">>", Associativity.YFX, 400),
                Triple("+", Associativity.YFX, 500),
                Triple("-", Associativity.YFX, 500),
                Triple("\\/", Associativity.YFX, 500),
                Triple("/\\", Associativity.YFX, 500),
                Triple("=", Associativity.XFX, 700),
                Triple("\\=", Associativity.XFX, 700),
                Triple("==", Associativity.XFX, 700),
                Triple("\\==", Associativity.XFX, 700),
                Triple("@<", Associativity.XFX, 700),
                Triple("@=<", Associativity.XFX, 700),
                Triple("@>", Associativity.XFX, 700),
                Triple("@>=", Associativity.XFX, 700),
                Triple("=..", Associativity.XFX, 700),
                Triple("is", Associativity.XFX, 700),
                Triple("=:=", Associativity.XFX, 700),
                Triple("=\\=", Associativity.XFX, 700),
                Triple("<", Associativity.XFX, 700),
                Triple("=<", Associativity.XFX, 700),
                Triple(">", Associativity.XFX, 700),
                Triple(">=", Associativity.XFX, 700),
                Triple(",", Associativity.XFY, 1000),
                Triple("->", Associativity.XFY, 1050),
                Triple(";", Associativity.XFY, 1100),
                Triple("\\+", Associativity.FY, 900),
                Triple(":-", Associativity.FX, 1200),
                Triple("?-", Associativity.FX, 1200),
                Triple(":-", Associativity.XFX, 1200),
                Triple("-->", Associativity.XFX, 1200)
        )
    }
}
