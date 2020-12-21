package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.Solution

interface EndState : State {

    val solution: Solution

    val hasOpenAlternatives: Boolean
        get() = solution is Solution.Yes && context.hasOpenAlternatives
}
