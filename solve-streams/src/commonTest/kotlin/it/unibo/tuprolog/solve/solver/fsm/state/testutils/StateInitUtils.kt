package it.unibo.tuprolog.solve.solver.fsm.state.testutils

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.solver.fsm.state.StateGoalEvaluation
import it.unibo.tuprolog.solve.solver.fsm.state.StateInit
import it.unibo.tuprolog.solve.testutils.DummyInstances

/**
 * Utils singleton to help testing [StateInit]
 *
 * @author Enrico
 */
internal object StateInitUtils {

    /** Solve request for `true` Atom */
    internal val trueSolveRequest = DummyInstances.solveRequest.copy(
            Signature("true", 0),
            emptyList()
    )

    /** Solve request for `fail` Atom */
    internal val failSolveRequest = DummyInstances.solveRequest.copy(
            Signature("fail", 0),
            emptyList()
    )

    /** Solve request for some vararg primitive */
    internal val varargPrimitive = DummyInstances.solveRequest.copy(
            Signature("varargPrimitive", 2, true),
            listOf(Truth.`true`(), Truth.`true`(), Truth.`true`())
    )

    /** Solve request with non well-formed goal */
    internal val nonWellFormedGoal = DummyInstances.solveRequest.copy(
            Signature(",", 2),
            listOf(Truth.`true`(), Integer.of(2))
    )

    /** Solve request with well-formed goal */
    internal val wellFormedGoal = DummyInstances.solveRequest.copy(
            Signature(";", 2),
            listOf(Truth.`true`(), Truth.fail())
    )

    /** Solve request with a goal that needs preparation for execution */
    internal val preparationNeededGoal = DummyInstances.solveRequest.copy(
            Signature(",", 2),
            listOf(Var.of("A"), Var.of("B"))
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
