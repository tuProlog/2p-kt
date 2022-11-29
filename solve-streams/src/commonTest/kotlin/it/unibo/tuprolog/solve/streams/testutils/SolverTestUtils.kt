package it.unibo.tuprolog.solve.streams.testutils

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext

/**
 * Utils singleton for helping test [Solver] behaviour
 *
 * @author Enrico
 */
internal object SolverTestUtils {

    /** Creates a Solve.Request with provided goal, against provided database as library theory, loading given primitives */
    internal fun createSolveRequest(
        query: Struct,
        database: Iterable<Clause> = emptyList(),
        primitives: Map<Signature, Primitive> = mapOf()
    ) = Solve.Request(
        query.extractSignature(),
        query.args,
        StreamsExecutionContext(
            libraries = Runtime.of(
                Library.of(
                    alias = "solve.solver.test",
                    primitives = primitives,
                    clauses = database
                )
            )
        )
    )
}
