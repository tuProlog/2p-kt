package it.unibo.tuprolog.solve.problogclassic.fsm

import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException

interface ExceptionalState : State {
    val exception: TuPrologRuntimeException
}
