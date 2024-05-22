package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Termificator

interface OopLpBidirectionalBridge {
    val termificator: Termificator

    val objectifier: Objectifier
}