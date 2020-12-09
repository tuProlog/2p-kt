package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.theory.Theory

object IOLib : AliasedLibrary by
    Library.aliased(
        operatorSet = OperatorSet(),
        theory = Theory.empty(),
        functions = emptyMap(),
        primitives = emptyMap(),
        alias = "prolog.io"
    )
