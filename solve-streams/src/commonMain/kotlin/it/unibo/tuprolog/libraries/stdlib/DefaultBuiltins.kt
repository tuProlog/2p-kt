package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.libraries.LibraryAliased

object DefaultBuiltins : LibraryAliased by Library.of(
        alias = CommonBuiltins.alias,
        operatorSet = CommonBuiltins.operators,
        theory = CommonBuiltins.theory,
        primitives = CommonBuiltins.primitives + sequenceOf(
                Call,
                Catch,
                Conjunction,
                Cut,
                Halt,
                Throw
        ).map { it.descriptionPair }.toMap()
)