package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.FlagStore
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

interface ClausePartition {
    @JsName("staticClauses")
    val staticClauses: Theory

    @JsName("dynamicClauses")
    val dynamicClauses: Theory

    @JsName("operators")
    val operators: OperatorSet

    @JsName("initialGoals")
    val initialGoals: List<Struct>

    @JsName("includes")
    val includes: List<Atom>

    @JsName("flagStore")
    val flagStore: FlagStore
}
