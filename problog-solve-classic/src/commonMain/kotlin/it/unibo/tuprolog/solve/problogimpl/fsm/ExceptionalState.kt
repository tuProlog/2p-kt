package it.unibo.tuprolog.solve.problogimpl.fsm

import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException

interface ExceptionalState : State {
    val exception: TuPrologRuntimeException
}
