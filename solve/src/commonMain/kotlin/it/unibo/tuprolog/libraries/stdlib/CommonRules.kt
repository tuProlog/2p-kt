package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.libraries.stdlib.primitive.EnsureExecutable
import it.unibo.tuprolog.theory.ClauseDatabase

object CommonRules : ClauseDatabase by ClauseDatabase.of(
    { factOf(atomOf("!")) },

    { ruleOf(structOf("\\+", varOf("X")),
        structOf(EnsureExecutable.functor, varOf("X")),
        structOf("call", varOf("X")),
        atomOf("!"),
        Truth.fail()) },

    { factOf(structOf("\\+", `_`)) },

    { ruleOf(structOf("not", varOf("X")),
        structOf("\\+", varOf("X"))) },

    { ruleOf(structOf(";", varOf("A"), whatever()),
        varOf("A")) },

    { ruleOf(structOf(";", whatever(), varOf("B")),
        varOf("B")) },

    { ruleOf(structOf("->", varOf("Cond"), varOf("Then")), varOf("Cond"), atomOf("!"), varOf("Then")) },

    { factOf(structOf("member", varOf("H"), consOf(varOf("H"), whatever()))) },

    {
        ruleOf(
            structOf("member", varOf("H"), consOf(whatever(), varOf("T"))),
            structOf("member", varOf("H"), varOf("T"))
        )
    }
)