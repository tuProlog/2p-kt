package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.solve.ClassicExecutionContext

interface State {
    val context: ClassicExecutionContext

    fun next(): State
}


