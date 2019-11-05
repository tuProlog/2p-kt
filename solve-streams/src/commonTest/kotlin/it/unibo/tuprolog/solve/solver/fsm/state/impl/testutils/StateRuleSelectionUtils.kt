package it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.solver.fsm.state.impl.StateRuleSelection
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.collections.listOf as ktListOf

/**
 * Utils singleton to help testing [StateRuleSelection]
 *
 * @author Enrico
 */
internal object StateRuleSelectionUtils {

    private val aAtom = Atom.of("a")
    private val bAtom = Atom.of("b")
    private val emptyUnifier = Substitution.empty()

    /** Utility function to create a Solve.Request of given query against given rule database */
    internal fun createRequest(query: Struct, ruleDatabase: ClauseDatabase) = createSolveRequest(query, ruleDatabase)

    /** Test data in the form (query Struct, a database with no query matches) */
    internal val queryToNoMatchesDatabaseMap by lazy {
        Scope.empty {
            mapOf(
                    aAtom to ClauseDatabase.empty(),
                    aAtom to ClauseDatabase.of({ factOf(bAtom) })
            )
        }
    }

    /** Test data in the form (query struct, a database with one fact that matches, the resulting Substitution) */
    internal val queryToOneMatchFactDatabaseAndSubstitution by lazy {
        Scope.empty {
            ktListOf(
                    Triple(aAtom, ClauseDatabase.of({ factOf(aAtom) }), emptyUnifier),
                    Triple(aAtom, ClauseDatabase.of({ factOf(aAtom) }, { factOf(bAtom) }), emptyUnifier),
                    Triple(
                            structOf("f", varOf("Var")),
                            ClauseDatabase.of({ factOf(structOf("f", aAtom)) }),
                            Substitution.of(varOf("Var"), aAtom)
                    ),
                    Triple(
                            structOf("f", varOf("Var")),
                            ClauseDatabase.of(
                                    { factOf(structOf("f", aAtom)) },
                                    { factOf(structOf("f", aAtom, bAtom)) }
                            ),
                            Substitution.of(varOf("Var"), aAtom)
                    )
            )
        }
    }

    /** Test data in the form (query struct, a database with one rule that matches, the resulting Substitution) */
    internal val queryToOneMatchRuleDatabaseAndSubstitution by lazy {
        Scope.empty {
            ktListOf(
                    Triple(aAtom, ClauseDatabase.of({ ruleOf(aAtom, bAtom) }), Substitution.failed()),
                    Triple(aAtom, ClauseDatabase.of({ ruleOf(aAtom, bAtom) }, { factOf(bAtom) }), emptyUnifier),
                    Triple(
                            structOf("f", varOf("Var")),
                            ClauseDatabase.of(
                                    { ruleOf(structOf("f", varOf("Var")), structOf("g", varOf("Var"))) },
                                    { factOf(structOf("g", aAtom)) }
                            ),
                            Substitution.of(varOf("Var"), aAtom)
                    )
            )
        }
    }

    /** Test data in the form (query struct, a database with multiple matches, the result Substitutions in order) */
    internal val queryToMultipleMatchesDatabaseAndSubstitution by lazy {
        Scope.empty {
            ktListOf(
                    Triple(
                            aAtom,
                            ClauseDatabase.of({ factOf(aAtom) }, { ruleOf(aAtom, bAtom) }),
                            ktListOf(emptyUnifier, Substitution.failed())
                    ),
                    Triple(
                            aAtom,
                            ClauseDatabase.of({ ruleOf(aAtom, bAtom) }, { factOf(aAtom) }),
                            ktListOf(emptyUnifier)
                    ),
                    Triple(
                            structOf("f", varOf("Var")),
                            ClauseDatabase.of(
                                    { ruleOf(structOf("f", varOf("Var")), structOf("g", varOf("Var"))) },
                                    { factOf(structOf("g", aAtom)) },
                                    { factOf(structOf("g", bAtom)) }
                            ),
                            ktListOf(
                                    Substitution.of(varOf("Var") to aAtom),
                                    Substitution.of(varOf("Var") to bAtom)
                            )
                    ),
                    Triple(
                            structOf("f", varOf("V")),
                            ClauseDatabase.of(
                                    { ruleOf(structOf("f", varOf("B")), structOf("g", varOf("B"))) },
                                    { ruleOf(structOf("f", varOf("B")), structOf("h", varOf("B"))) },
                                    { factOf(structOf("g", atomOf("c1"))) },
                                    { factOf(structOf("g", atomOf("c2"))) },
                                    { factOf(structOf("h", atomOf("d1"))) },
                                    { factOf(structOf("h", atomOf("d2"))) }
                            ),
                            ktListOf(
                                    Substitution.of(varOf("V") to atomOf("c1")),
                                    Substitution.of(varOf("V") to atomOf("c2")),
                                    Substitution.of(varOf("V") to atomOf("d1")),
                                    Substitution.of(varOf("V") to atomOf("d2"))
                            )
                    )
            )
        }
    }
}
