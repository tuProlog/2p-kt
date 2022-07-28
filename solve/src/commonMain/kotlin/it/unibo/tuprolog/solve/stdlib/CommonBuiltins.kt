package it.unibo.tuprolog.solve.stdlib

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.library.Library

object CommonBuiltins : Library by
Library.of(
    alias = "prolog.lang",
    primitives = CommonPrimitives.primitives,
    theory = CommonRules.theory,
    operators = OperatorSet.DEFAULT,
    functions = CommonFunctions.functions
)
