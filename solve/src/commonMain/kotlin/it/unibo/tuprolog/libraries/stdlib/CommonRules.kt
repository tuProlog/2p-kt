package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.theory.ClauseDatabase

object CommonRules : ClauseDatabase by ClauseDatabase.of(
    { factOf(atomOf("!")) },
    { ruleOf(structOf("not", varOf("X")), varOf("X"), atomOf("!"), Truth.fail()) },
    { factOf(structOf("not", `_`)) },
    { ruleOf(structOf("\\+", varOf("X")), varOf("X"), atomOf("!"), Truth.fail()) },
    { factOf(structOf("\\+", `_`)) },
    { ruleOf(structOf(";", varOf("A"), whatever()), varOf("A")) },
    { ruleOf(structOf(";", whatever(), varOf("B")), varOf("B")) },
    { ruleOf(structOf("->", varOf("A"), varOf("B")), structOf(";", structOf("\\+", varOf("A")), varOf("B"))) },
    { factOf(structOf("member", varOf("H"), consOf(varOf("H"), whatever()))) },
    {
        ruleOf(
            structOf("member", varOf("H"), consOf(whatever(), varOf("T"))),
            structOf("member", varOf("H"), varOf("T"))
        )
    }
)