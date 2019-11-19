package it.unibo.tuprolog.libraries.stdlib.primitive.testutils

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.stdlib.CommonBuiltins
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.theory.ClauseDatabase

/**
 * Utils singleton to help testing Primitives
 *
 * @author Enrico
 */
internal object PrimitiveUtils {

    private fun contextWith(database: ClauseDatabase) = object : ExecutionContext by DummyInstances.executionContext {
        override val libraries: Libraries = Libraries(CommonBuiltins)
        override val staticKB: ClauseDatabase = database
    }

    /** Creates a solve request with [database] and [CommonBuiltins] loaded and given query struct*/
    internal fun createSolveRequest(
        query: Struct,
        database: ClauseDatabase = ClauseDatabase.empty()
    ): Solve.Request<ExecutionContext> =
        Solve.Request<ExecutionContext>(
            query.extractSignature(),
            query.argsList,
            contextWith(database)
        )

}
