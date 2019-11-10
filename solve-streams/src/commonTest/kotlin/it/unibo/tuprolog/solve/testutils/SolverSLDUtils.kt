package it.unibo.tuprolog.solve.testutils

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.libraries.stdlib.primitive.Conjunction
import it.unibo.tuprolog.libraries.stdlib.primitive.Cut
import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.CallUtils
import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.CatchUtils
import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.HaltUtils
import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.ThrowUtils
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.prologStandardExampleDatabase
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.prologStandardExampleWithCutDatabase
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.SolverSLD
import it.unibo.tuprolog.solve.TestingClauseDatabases.customReverseListDatabase
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import kotlin.collections.listOf as ktListOf

/**
 * Utils singleton to help testing [SolverSLD]
 *
 * @author Enrico
 */
internal object SolverSLDUtils {

    // TODO: 08/11/2019 remove this whole class when all primitive testing databases will be moved to common testing

    /** Contains context and goal requests to be launched with solver, and corresponding expected solutions */
    internal val contextAndRequestToSolutionMap by lazy {
        Scope.empty {
            mapOf(
                    structOf("p", varOf("U"), varOf("V")).let {
                        (it to ExecutionContextImpl(
                                libraries = Libraries(Library.of(
                                        alias = "testLib",
                                        theory = prologStandardExampleDatabase,
                                        primitives = mapOf(Conjunction.descriptionPair)
                                ))
                        )) to ktListOf(
                                Solution.Yes(it, Substitution.of(
                                        varOf("U") to atomOf("b"),
                                        varOf("V") to atomOf("b1")
                                ) as Substitution.Unifier),
                                Solution.Yes(it, Substitution.of(
                                        varOf("U") to atomOf("c"),
                                        varOf("V") to atomOf("c1")
                                ) as Substitution.Unifier),
                                Solution.Yes(it, Substitution.of(
                                        varOf("U") to atomOf("d"),
                                        varOf("V") to varOf("Y")
                                ) as Substitution.Unifier)
                        )
                    },
                    structOf("p", varOf("U"), varOf("V")).let {
                        (it to ExecutionContextImpl(
                                libraries = Libraries(Library.of(
                                        alias = "testLib",
                                        theory = prologStandardExampleWithCutDatabase,
                                        primitives = mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
                                ))
                        )) to ktListOf(Solution.No(it))
                    },
                    structOf("my_reverse", listOf((1..4).map(::numOf)), varOf("L")).let {
                        (it to ExecutionContextImpl(
                                libraries = Libraries(Library.of(
                                        alias = "testLib",
                                        theory = customReverseListDatabase,
                                        primitives = mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
                                ))
                        )) to ktListOf(
                                Solution.Yes(it, Substitution.of(
                                        varOf("L") to listOf((1..4).reversed().map(::numOf))
                                ) as Substitution.Unifier)
                        )
                    },
                    *extractQueryContextSolutionPairs(HaltUtils.requestSolutionMap).toTypedArray(),
                    *extractQueryContextSolutionPairs(CallUtils.requestSolutionMap).toTypedArray(),
                    *extractQueryContextSolutionPairs(CallUtils.requestToErrorSolutionMap).toTypedArray(),
                    *extractQueryContextSolutionPairs(ThrowUtils.requestSolutionMap).toTypedArray(),
                    *extractQueryContextSolutionPairs(CatchUtils.requestSolutionMap).toTypedArray(),
                    *extractQueryContextSolutionPairs(CatchUtils.prologStandardCatchExamples).toTypedArray(),
                    *extractQueryContextSolutionPairs(CatchUtils.prologStandardThrowExamples).toTypedArray(),
                    *extractQueryContextSolutionPairs(CatchUtils.prologStandardThrowExamplesWithError).toTypedArray()
            )
        }
    }

    /** An utility method to convert (request, solution list) format, to ((query, context), solution list) one */
    private fun extractQueryContextSolutionPairs(requestSolutionMap: Map<Solve.Request<ExecutionContextImpl>, Iterable<Solution>>) =
            requestSolutionMap.mapKeys { it.key.query to it.key.context }.entries.map { it.toPair() }

}
