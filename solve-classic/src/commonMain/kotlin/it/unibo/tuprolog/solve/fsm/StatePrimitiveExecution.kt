package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.utils.Cursor

internal data class StatePrimitiveExecution(override val context: ExecutionContextImpl) : AbstractState(context) {

    private fun ExecutionContextImpl.copyFromCurrentPrimitive(goals: Cursor<out Term>? = null,
                                              parentProcedure: Boolean = false,
                                              substitution: Substitution? = null): ExecutionContextImpl {
        return copy(
            goals = goals ?: this.goals,
            procedure = if (parentProcedure) parent?.procedure else procedure,
            primitives = Cursor.empty(),
            libraries = primitives.current?.libraries ?: libraries,
            staticKB = primitives.current?.staticKB ?: staticKB,
            dynamicKB = primitives.current?.dynamicKB ?: dynamicKB,
            flags = primitives.current?.flags ?: flags,
            inputChannels = primitives.current?.inputChannels ?: inputChannels,
            outputChannels = primitives.current?.outputChannels ?: outputChannels,
            substitution = (substitution ?: this.substitution) as Substitution.Unifier,
            step = nextStep()
        )
    }


    override fun computeNext(): State = try {
        when (val sol = context.primitives.current!!.solution) {
            is Solution.Yes -> {
                StateGoalSelection(
                    context.copyFromCurrentPrimitive(
                        goals = context.goals.next,
                        parentProcedure = true,
                        substitution = (context.substitution + sol.substitution)
                    )
                )
            }
            is Solution.No -> {
                StateBacktracking(context.copyFromCurrentPrimitive())
            }
            is Solution.Halt -> {
                StateException(
                    sol.exception.updateContext(context),
                    context.copyFromCurrentPrimitive()
                )
            }
        }
    } catch (exception: TuPrologRuntimeException) {
        StateException(
            exception.updateContext(context),
            context.copy(step = nextStep())
        )
    }

}