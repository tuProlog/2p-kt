package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.solve.ExecutionContextImpl

interface State {
    val context: ExecutionContextImpl

    fun next(): State
}


