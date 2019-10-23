package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.libraries.LibraryAliased
import it.unibo.tuprolog.libraries.stdlib.primitive.*

object CommonBuiltins : LibraryAliased by Library.of(
        alias = "prolog.lang",
        operatorSet = OperatorSet.DEFAULT,
        theory = CommonRules,
        primitives = sequenceOf(
                GreaterThan,
                GreaterThanOrEqualsTo,
                LowerThan,
                LowerThanOrEqualsTo,
                UnifiesWith,
                NotUnifiableWith,
                EqualsTo,
                DifferentFrom,
                Natural
        ).map { it.descriptionPair }.toMap()
)