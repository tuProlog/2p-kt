package it.unibo.tuprolog.solve.stdlib

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library

object CommonBuiltins : AliasedLibrary by
    Library.aliased(
        alias = "prolog.lang",
        operatorSet = OperatorSet.DEFAULT,
        theory = CommonRules.theory,
        primitives = CommonPrimitives.primitives,
        functions = CommonFunctions.functions
    )
