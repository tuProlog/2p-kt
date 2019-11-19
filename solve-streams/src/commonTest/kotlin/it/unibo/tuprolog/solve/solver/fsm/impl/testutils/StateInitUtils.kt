package it.unibo.tuprolog.solve.solver.fsm.impl.testutils

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.fsm.impl.StateGoalEvaluation
import it.unibo.tuprolog.solve.solver.fsm.impl.StateInit

/**
 * Utils singleton to help testing [StateInit]
 *
 * @author Enrico
 */
internal object StateInitUtils {

    /** Utility function to create a [Solve.Request] */
    private fun createSolveRequest(signature: Signature, argList: List<Term> = emptyList()) =
        Solve.Request(signature, argList, ExecutionContextImpl())

    /** Solve request for `true` Atom */
    internal val trueRequest = createSolveRequest(Signature("true", 0))

    /** Solve request for `fail` Atom */
    private val failRequest = createSolveRequest(Signature("fail", 0))

    /** Solve request for some vararg primitive */
    private val varargPrimitiveRequest = createSolveRequest(
        Signature("varargPrimitive", 2, true),
        listOf(Truth.`true`(), Truth.`true`(), Truth.`true`())
    )

    /** Solve request with well-formed goal */
    private val wellFormedGoalRequest = createSolveRequest(
        Signature(";", 2),
        listOf(Truth.`true`(), Truth.fail())
    )

    /** Solve request with a goal that needs preparation for execution */
    private val preparationNeededGoalRequest = createSolveRequest(
        Signature(",", 2),
        listOf(Var.of("A"), Var.of("B"))
    )

    /** Well formed goals needing preparation for execution */
    internal val wellFormedGoalRequestsNeedingPreparationForExecution by lazy {
        listOf(preparationNeededGoalRequest)
    }

    /** Well formed goals needing preparation for execution */
    internal val wellFormedGoalRequestsNotNeedingPreparationForExecution by lazy {
        listOf(
            failRequest,
            varargPrimitiveRequest,
            wellFormedGoalRequest
        )
    }

    /** Collection of [Solve.Request] that should pass [StateInit] going into [StateGoalEvaluation] */
    internal val allWellFormedGoalRequests by lazy {
        wellFormedGoalRequestsNeedingPreparationForExecution +
                wellFormedGoalRequestsNotNeedingPreparationForExecution
    }

    /** Solve request with non well-formed goal */
    internal val nonWellFormedGoalRequest = createSolveRequest(
        Signature(",", 2),
        listOf(Truth.`true`(), Integer.of(2))
    )

    /** All [StateInit] testing requests */
    internal val allRequests by lazy { allWellFormedGoalRequests + nonWellFormedGoalRequest }
}
