package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.solve.fsm.EndState
import it.unibo.tuprolog.theory.ClauseDatabase
import it.unibo.tuprolog.solve.fsm.State
import it.unibo.tuprolog.solve.fsm.StateInit

/**
 * Represents a Prolog Goal solver
 *
 * @author Enrico
 */
interface Solver {

    /** Solves the provided goal, returning lazily initialized sequence of responses */
    fun solve(goal: Struct): Sequence<Solution>

}

fun Solver.solve(scopedContext: Scope.()->Struct): Sequence<Solution> =
        solve(scopedContext(Scope.empty()))

data class MutableSolver(var libraries: Libraries = Libraries(),
                    /** Enabled flags */
                    var flags: Map<Atom, Term> = emptyMap(),
                    /** Static Knowledge-base, that is a KB that *can't* change executing goals */
                    var staticKB: ClauseDatabase = ClauseDatabase.empty(),
                    /** Dynamic Knowledge-base, that is a KB that *can* change executing goals */
                    var dynamicKB: ClauseDatabase = ClauseDatabase.empty()) : Solver {

    override fun solve(goal: Struct): Sequence<Solution> = sequence {
        val initialContext = ExecutionContext(
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