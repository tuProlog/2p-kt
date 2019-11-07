package it.unibo.tuprolog.libraries.stdlib.primitive.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.libraries.stdlib.primitive.Call
import it.unibo.tuprolog.libraries.stdlib.primitive.Conjunction
import it.unibo.tuprolog.libraries.stdlib.primitive.Halt
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleFactDatabase
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.collections.listOf as ktListOf

/**
 * Utils singleton to help testing [Halt]
 *
 * @author Enrico
 */
internal object HaltUtils {

    /** An halt primitive that will throw immediately HaltException */
    internal val exposedHaltBehaviourRequest = createSolveRequest(Atom.of("halt"), primitives = mapOf(Halt.descriptionPair))

    /** A database to test `halt/0` functionality */
    private val haltTestingDatabase by lazy {
        ClauseDatabase.of(
                Fact.of(Struct.of("p", Atom.of("a"))),
                Rule.of(Struct.of("p", Atom.of("b")), Atom.of("halt")),
                Fact.of(Struct.of("p", Atom.of("c")))
        )
    }

    /** Halt primitive working examples (when wrapped inside other queries), with expected responses */
    internal val requestSolutionMap by lazy {
        mapOf(
                Struct.of("call", Atom.of("halt")).let {
                    createSolveRequest(it, primitives = mapOf(Halt.descriptionPair, Call.descriptionPair)).run {
                        this to ktListOf(
                                Solution.Halt(it, HaltException(context = this.context))
                        )
                    }
                },
                Tuple.of(Atom.of("halt"), Atom.of("halt")).let {
                    createSolveRequest(it, primitives = mapOf(Halt.descriptionPair, Conjunction.descriptionPair)).run {
                        this to ktListOf(
                                Solution.Halt(it, HaltException(context = this.context))
                        )
                    }
                },
                Struct.of("call", Tuple.of(Atom.of("halt"), Atom.of("halt"))).let {
                    createSolveRequest(it, primitives = mapOf(Halt.descriptionPair, Call.descriptionPair, Conjunction.descriptionPair)).run {
                        this to ktListOf(
                                Solution.Halt(it, HaltException(context = this.context))
                        )
                    }
                },
                prolog {
                    tupleOf("halt", "h"("A")).run {
                        createSolveRequest(
                                this,
                                database = simpleFactDatabase,
                                primitives = mapOf(Conjunction.descriptionPair, Halt.descriptionPair)
                        ).let { solveRequest ->
                            solveRequest to ktListOf( // TODO: 07/11/2019 refactor with haltSolution()
                                    Solution.Halt(this, HaltException(context = solveRequest.context))
                            )
                        }
                    }
                },
                Scope.empty {
                    structOf("p", varOf("V")).let {
                        createSolveRequest(
                                it,
                                database = haltTestingDatabase,
                                primitives = mapOf(Halt.descriptionPair)
                        ).run {
                            this to ktListOf(
                                    Solution.Yes(it, Substitution.of(varOf("V"), atomOf("a"))),
                                    Solution.Halt(it, HaltException(context = this.context))
                            )
                        }
                    }
                }
        )
    }

}
