package it.unibo.tuprolog.solve.solver.statemachine.state.testutils

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.statemachine.state.StateGoalEvaluation
import it.unibo.tuprolog.solve.solver.statemachine.state.StateGoalSelection
import it.unibo.tuprolog.solve.testutils.DummyInstances

/**
 * Utils singleton to help testing [StateGoalSelection]
 *
 * @author Enrico
 */
internal object StateGoalSelectionUtils {

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

    /** Collection of Solve.Request that should pass [StateGoalSelection] going into [StateGoalEvaluation] */
    internal val notVarargPrimitiveAndWellFormedGoalRequests by lazy {
        listOf(
                failSolveRequest,
                wellFormedGoal,
                preparationNeededGoal
        )
    }

    /** Utility function to compose Struct corresponding to Solve.Request Signature and Arguments */
    internal fun composeSignatureAndArgs(solveRequest: Solve.Request) =
            with(solveRequest) { signature.withArgs(arguments)!! }
}
