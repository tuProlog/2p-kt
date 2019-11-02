package it.unibo.tuprolog.solve.solver.fsm.state.testutils

import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.solver.fsm.state.StateEnd
import it.unibo.tuprolog.solve.testutils.DummyInstances

/**
 * Utils singleton to help testing [StateEnd] and subclasses
 *
 * @author Enrico
 */
internal object StateEndUtils {

    /** The query to which response responds */
    private val responseQuery = Truth.`true`()

    /** The exception inside [anExceptionalResponse] */
    private val responseException = HaltException(context = DummyInstances.executionContext)


    /** A Yes Response */
    internal val aYesResponse by lazy { Solve.Response(Solution.Yes(responseQuery)) }

    /** A No Response */
    internal val aNoResponse by lazy { Solve.Response(Solution.No(responseQuery)) }

    /** An Exceptional Response */
    internal val anExceptionalResponse by lazy { Solve.Response(Solution.Halt(responseQuery, responseException)) }


    /** All Response types  */
    internal val allResponseTypes by lazy { listOf(aYesResponse, aNoResponse, anExceptionalResponse) }
}
