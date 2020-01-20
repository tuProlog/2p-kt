package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException

interface ExceptionalState : State {
    val exception: TuPrologRuntimeException
}