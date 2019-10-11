package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.libraries.LibraryAliased
import it.unibo.tuprolog.theory.ClauseDatabase

object DefaultBuiltins : LibraryAliased by Library.of(
        alias = CommonBuiltins.alias,
        operatorSet = CommonBuiltins.operators,
        theory = CommonBuiltins.theory + ClauseDatabase.of(
                { ruleOf(tupleOf(varOf("A"), varOf("B")), varOf("A"), varOf("B")) },
                { ruleOf(structOf("call", varOf("X")), varOf("X")) },
                { ruleOf(structOf("catch", varOf("G"), whatever(), whatever()), varOf("G")) }
        ),
        primitives = CommonBuiltins.primitives + sequenceOf(
                GreaterThan,
                GreaterThanOrEqualsTo,
                LowerThan,
                LowerThanOrEqualsTo,
                UnifiesWith,
                NotUnifiableWith,
                EqualsTo,
                DifferentFrom,
                Throw,
                Natural
        ).map { it.descriptionPair }.toMap()
)