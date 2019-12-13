package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.utils.Cursor

internal data class StatePrimitiveExecution(override val context: ExecutionContextImpl) : AbstractState(context) {

    override fun computeNext(): State {
        return with(context) {
            try {
                when (val sol = primitives.current!!.solution) {
                    is Solution.Yes -> {
                        StateGoalSelection(
                            copy(
                                goals = goals.next,
                                primitives = Cursor.empty(),
                                libraries = primitives.current!!.libraries ?: libraries,
                                staticKB = primitives.current!!.staticKB ?: staticKB,
                                dynamicKB = primitives.current!!.dynamicKB ?: dynamicKB,
                                flags = primitives.current!!.flags ?: flags,
                                substitution = (substitution + sol.substitution) as Substitution.Unifier,
                                step = nextStep()
                            )
                        )
                    }
                    is Solution.No -> {
                        StateBacktracking(
                            copy(
                                primitives = Cursor.empty(),
                                libraries = primitives.current!!.libraries ?: libraries,
                                staticKB = primitives.current!!.staticKB ?: staticKB,
                                dynamicKB = primitives.current!!.dynamicKB ?: dynamicKB,
                                flags = primitives.current!!.flags ?: flags,
                                step = nextStep()
                            )
                        )
                    }
                    is Solution.Halt -> StateException(
                        sol.exception.updateContext(context),
                        copy(
                            primitives = Cursor.empty(),
                            libraries = primitives.current!!.libraries ?: libraries,
                            staticKB = primitives.current!!.staticKB ?: staticKB,
                            dynamicKB = primitives.current!!.dynamicKB ?: dynamicKB,
                            flags = primitives.current!!.flags ?: flags,
                            step = nextStep()
                        )
                    )
                }
            } catch (exception: TuPrologRuntimeException) {
                StateException(
                    exception.updateContext(context),
                    copy(step = nextStep())
                )
            }
        }
    }

}