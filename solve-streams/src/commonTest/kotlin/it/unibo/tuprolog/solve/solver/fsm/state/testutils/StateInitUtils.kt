package it.unibo.tuprolog.solve.solver.fsm.state.testutils

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.fsm.state.StateGoalEvaluation
import it.unibo.tuprolog.solve.solver.fsm.state.StateInit

/**
 * Utils singleton to help testing [StateInit]
 *
 * @author Enrico
 */
internal object StateInitUtils {

    /** Solve request for `true` Atom */
    internal val trueSolveRequest = Solve.Request(
            Signature("true", 0),
            emptyList(),
            ExecutionContextImpl()
    )

    /** Solve request for `fail` Atom */
    internal val failSolveRequest = Solve.Request(
            Signature("fail", 0),
            emptyList(),
            ExecutionContextImpl()
    )

    /** Solve request for some vararg primitive */
    internal val varargPrimitive = Solve.Request(
            Signature("varargPrimitive", 2, true),
            listOf(Truth.`true`(), Truth.`true`(), Truth.`true`()),
            ExecutionContextImpl()
    )

    /** Solve request with non well-formed goal */
    internal val nonWellFormedGoal = Solve.Request(
            Signature(",", 2),
            listOf(Truth.`true`(), Integer.of(2)),
            ExecutionContextImpl()
    )

    /** Solve request with well-formed goal */
    internal val wellFormedGoal = Solve.Request(
            Signature(";", 2),
            listOf(Truth.`true`(), Truth.fail()),
            ExecutionContextImpl()
    )

    /** Solve request with a goal that needs preparation for execution */
    internal val preparationNeededGoal = Solve.Request(
            Signature(",", 2),
            listOf(Var.of("A"), Var.of("B")),
            ExecutionContextImpl()
    )

    /** Collection of Solve.Request that should pass [StateInit] going into [StateGoalEvaluation] */
    internal val notVarargPrimitiveAndWellFormedGoalRequests by lazy {
        listOf(
                failSolveRequest,
                wellFormedGoal,
                preparationNeededGoal
        )
    }
}
