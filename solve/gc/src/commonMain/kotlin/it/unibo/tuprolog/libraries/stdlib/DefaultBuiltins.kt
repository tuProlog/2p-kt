package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.libraries.LibraryAliased
import it.unibo.tuprolog.theory.ClauseDatabase

object DefaultBuiltins : LibraryAliased by Library.of(
        alias = "prolog.lang",
        operatorSet = OperatorSet.DEFAULT,
        theory = ClauseDatabase.of(
                { ruleOf(structOf("call", varOf("X")), varOf("X")) },
                { ruleOf(structOf("catch", varOf("G"), whatever(), whatever()), varOf("G")) },
                { ruleOf(structOf("not", varOf("X")), varOf("X"), atomOf("!"), Truth.fail()) },
                { ruleOf(structOf("\\+", varOf("X")), varOf("X"), atomOf("!"), Truth.fail()) },
                { ruleOf(structOf(";", varOf("A"), whatever()), varOf("A")) },
                { ruleOf(structOf(";", whatever(), varOf("B")), varOf("B")) },
                { ruleOf(structOf("->", varOf("A"), varOf("B")), structOf(";", structOf("\\+", varOf("A")), varOf("B"))) },
                { factOf(structOf("member", varOf("H"), consOf(varOf("H"), whatever()))) },
                { ruleOf(structOf("member", varOf("H"), consOf(whatever(), varOf("T"))), structOf("member", varOf("H"), varOf("T"))) }
        ),
        primitives = sequenceOf(
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