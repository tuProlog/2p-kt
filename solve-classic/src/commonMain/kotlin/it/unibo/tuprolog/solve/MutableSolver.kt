package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.solve.fsm.EndState
import it.unibo.tuprolog.solve.fsm.State
import it.unibo.tuprolog.solve.fsm.StateInit
import it.unibo.tuprolog.theory.ClauseDatabase

fun Solver.Companion.mutable(libraries: Libraries = Libraries(),
                             flags: PrologFlags = emptyMap(),
                             staticKB: ClauseDatabase = ClauseDatabase.empty(),
                             dynamicKB: ClauseDatabase = ClauseDatabase.empty()) =
        MutableSolver(libraries, flags, staticKB, dynamicKB)

data class MutableSolver(override var libraries: Libraries = Libraries(),
                         /** Enabled flags */
                         override var flags: PrologFlags = emptyMap(),
                         /** Static Knowledge-base, that is a KB that *can't* change executing goals */
                         override var staticKB: ClauseDatabase = ClauseDatabase.empty(),
                         /** Dynamic Knowledge-base, that is a KB that *can* change executing goals */
                         override var dynamicKB: ClauseDatabase = ClauseDatabase.empty()) : Solver {

    override fun solve(goal: Struct, maxDuration: TimeDuration): Sequence<Solution> = sequence {
        val initialContext = ExecutionContextImpl(
                query = goal,
                libraries = libraries,
                flags = flags,
                staticKB = staticKB,
                dynamicKB = dynamicKB
        )

        var state: State = StateInit(initialContext)

        while (true) {
            state = state.next()

            state.context.let { ctx ->
                libraries = ctx.libraries
                flags = ctx.flags
                staticKB = ctx.staticKB
                dynamicKB = ctx.dynamicKB
            }

            if (state is EndState) {
                yield(state.solution)

                if (!state.hasOpenAlternatives) break
            }
        }
    }


}