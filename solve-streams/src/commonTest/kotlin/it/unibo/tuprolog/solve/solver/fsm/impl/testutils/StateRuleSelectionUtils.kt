package it.unibo.tuprolog.solve.solver.fsm.impl.testutils

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.solver.fsm.impl.StateRuleSelection
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.collections.listOf as ktListOf

/**
 * Utils singleton to help testing [StateRuleSelection]
 *
 * @author Enrico
 */
internal object StateRuleSelectionUtils {

    private val emptyUnifier = Substitution.empty()

    /** Utility function to create a Solve.Request of given query against given rule database */
    internal fun createRequest(query: Struct, ruleDatabase: ClauseDatabase) = createSolveRequest(query, ruleDatabase)

    /** Test data in the form (query Struct, a database with no query matches) */
    internal val queryToNoMatchesDatabaseMap by lazy {
        prolog {
            mapOf(
                    atomOf("a") to theoryOf(),
                    atomOf("a") to theory({ "b" })
            )
        }
    }

    /** Test data in the form (query struct, a database with one fact that matches, the resulting Substitution) */
    internal val queryToOneMatchFactDatabaseAndSubstitution by lazy {
        prolog {
            ktListOf(
                    Triple(atomOf("a"), theory({ "a" }), emptyUnifier),
                    Triple(atomOf("a"), theory({ "a" }, { "b" }), emptyUnifier),
                    Triple(
                            "f"("Var"),
                            theory({ "f"("a") }),
                            Substitution.of(varOf("Var"), atomOf("a"))
                    ),
                    Triple(
                            "f"("Var"),
                            theory(
                                    { "f"("a") },
                                    { "f"("a", "b") }
                            ),
                            Substitution.of(varOf("Var"), atomOf("a"))
                    )
            )
        }
    }

    /** Test data in the form (query struct, a database with one rule that matches, the resulting Substitution) */
    internal val queryToOneMatchRuleDatabaseAndSubstitution by lazy {
        prolog {
            ktListOf(
                    Triple(atomOf("a"), theory({ "a" impliedBy "b" }), Substitution.failed()),
                    Triple(atomOf("a"), theory({ "a" impliedBy "b" }, { "b" }), emptyUnifier),
                    Triple(
                            "f"("Var"),
                            theory(
                                    { "f"("Var") impliedBy "g"("Var") },
                                    { "g"("a") }
                            ),
                            Substitution.of(varOf("Var"), atomOf("a"))
                    )
            )
        }
    }

    /** Test data in the form (query struct, a database with multiple matches, the result Substitutions in order) */
    internal val queryToMultipleMatchesDatabaseAndSubstitution by lazy {
        prolog {
            ktListOf(
                    Triple(
                            atomOf("a"),
                            theory(
                                    { "a" },
                                    { "a" impliedBy "b" }
                            ),
                            ktListOf(emptyUnifier, Substitution.failed())
                    ),
                    Triple(
                            atomOf("a"),
                            theory(
                                    { "a" impliedBy "b" },
                                    { "a" }
                            ),
                            ktListOf(emptyUnifier)
                    ),
                    Triple(
                            "f"("Var"),
                            theory(
                                    { "f"("Var") impliedBy "g"("Var") },
                                    { "g"("a") },
                                    { "g"("b") }
                            ),
                            ktListOf(
                                    Substitution.of(varOf("Var") to atomOf("a")),
                                    Substitution.of(varOf("Var") to atomOf("b"))
                            )
                    ),
                    Triple(
                            structOf("f", varOf("V")),
                            theory(
                                    { "f"("B") impliedBy "g"("B") },
                                    { "f"("B") impliedBy "h"("B") },
                                    { "g"("c1") },
                                    { "g"("c2") },
                                    { "h"("d1") },
                                    { "h"("d2") }
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
