package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.solve.fsm.EndState
import it.unibo.tuprolog.solve.fsm.State
import it.unibo.tuprolog.solve.fsm.StateInit
import it.unibo.tuprolog.theory.ClauseDatabase

fun Solver.Companion.classic(
    libraries: Libraries = Libraries(),
    flags: PrologFlags = emptyMap(),
    staticKB: ClauseDatabase = ClauseDatabase.empty(),
    dynamicKB: ClauseDatabase = ClauseDatabase.empty()
) = ClassicSolver(libraries, flags, staticKB, dynamicKB)

data class ClassicSolver(
    override val libraries: Libraries = Libraries(),
    /** Enabled flags */
    override val flags: PrologFlags = emptyMap(),
    /** Static Knowledge-base, that is a KB that *can't* change executing goals */
    override val staticKB: ClauseDatabase = ClauseDatabase.empty(),
    /** Dynamic Knowledge-base, that is a KB that *can* change executing goals */
    override val dynamicKB: ClauseDatabase = ClauseDatabase.empty()
) : Solver {

    private fun Substitution.Unifier.cleanUp(): Substitution.Unifier {
        return filter { _, term -> term !is Var }
    }

    private fun Solution.Yes.cleanUp(): Solution.Yes {
        return copy(substitution = substitution.cleanUp())
    }

    override fun solve(goal: Struct, maxDuration: TimeDuration): Sequence<Solution> = sequence {
        val initialContext = ExecutionContextImpl(
            query = goal,
            procedure = null,
            libraries = libraries,
            flags = flags,
            staticKB = staticKB,
            dynamicKB = dynamicKB,
            startTime = currentTimeInstant(),
            maxDuration = maxDuration
        )

        var state: State = StateInit(initialContext)

        while (true) {
            state = state.next()
            
            if (state is EndState) {
                yield(
                    when(val sol = state.solution) {
                        is Solution.Yes -> sol.cleanUp()
                        else -> sol
                    }
                )

                if (!state.hasOpenAlternatives) break
            }
        }
    }

}
