package it.unibo.tuprolog.solve.testutils

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.solver.StreamsExecutionContext
import it.unibo.tuprolog.theory.Theory

/**
 * Utils singleton for helping test [Solver] behaviour
 *
 * @author Enrico
 */
internal object SolverTestUtils {

    /** Creates a Solve.Request with provided goal, against provided database as library theory, loading given primitives */
    internal fun createSolveRequest(
        query: Struct,
        database: Theory = Theory.empty(),
        primitives: Map<Signature, Primitive> = mapOf()
    ) = Solve.Request(
        query.extractSignature(),
        query.argsList,
        StreamsExecutionContext(
            libraries = Libraries(
                Library.aliased(
                    alias = "solve.solver.test",
                    theory = database,
                    primitives = primitives
                )
            )
        )
    )
}
