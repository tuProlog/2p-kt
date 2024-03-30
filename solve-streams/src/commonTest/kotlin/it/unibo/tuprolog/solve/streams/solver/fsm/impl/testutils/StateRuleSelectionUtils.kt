package it.unibo.tuprolog.solve.streams.solver.fsm.impl.testutils

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.StateRuleSelection
import it.unibo.tuprolog.solve.streams.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.theory.Theory

/**
 * Utils singleton to help testing [StateRuleSelection]
 *
 * @author Enrico
 */
internal object StateRuleSelectionUtils {
    private val emptyUnifier = Substitution.empty()

    /** Utility function to create a Solve.Request of given query against given rule database */
    internal fun createRequest(
        query: Struct,
        ruleDatabase: Theory,
    ) = createSolveRequest(query, ruleDatabase)

    /** Test data in the form (query Struct, a database with no query matches) */
    internal val queryToNoMatchesTheoryMap by lazy {
        logicProgramming {
            mapOf(
                atomOf("a") to theoryOf(),
                atomOf("a") to theory({ "b" }),
            )
        }
    }

    /** Test data in the form (query struct, a database with one fact that matches, the resulting Substitution) */
    internal val queryToOneMatchFactTheoryAndSubstitution by lazy {
        logicProgramming {
            listOf(
                Triple(atomOf("a"), theory({ "a" }), emptyUnifier),
                Triple(atomOf("a"), theory({ "a" }, { "b" }), emptyUnifier),
                Triple("f"("Var"), theory({ "f"("a") }), "Var" to "a"),
                Triple(
                    "f"("Var"),
                    theory(
                        { "f"("a") },
                        { "f"("a", "b") },
                    ),
                    "Var" to "a",
                ),
            )
        }
    }

    /** Test data in the form (query struct, a database with one rule that matches, the resulting Substitution) */
    internal val queryToOneMatchRuleTheoryAndSubstitution by lazy {
        logicProgramming {
            listOf(
                Triple(atomOf("a"), theory({ "a" impliedBy "b" }), Substitution.failed()),
                Triple(atomOf("a"), theory({ "a" impliedBy "b" }, { "b" }), emptyUnifier),
                Triple(
                    "f"("Var"),
                    theory(
                        { "f"("Var") impliedBy "g"("Var") },
                        { "g"("a") },
                    ),
                    "Var" to "a",
                ),
            )
        }
    }

    /** Test data in the form (query struct, a database with multiple matches, the result Substitutions in order) */
    internal val queryToMultipleMatchesTheoryAndSubstitution by lazy {
        logicProgramming {
            listOf(
                Triple(
                    atomOf("a"),
                    theory(
                        { "a" },
                        { "a" impliedBy "b" },
                    ),
                    listOf(emptyUnifier, Substitution.failed()),
                ),
                Triple(
                    atomOf("a"),
                    theory(
                        { "a" impliedBy "b" },
                        { "a" },
                    ),
                    listOf(emptyUnifier),
                ),
                Triple(
                    "f"("Var"),
                    theory(
                        { "f"("Var") impliedBy "g"("Var") },
                        { "g"("a") },
                        { "g"("b") },
                    ),
                    listOf(
                        "Var" to "a",
                        "Var" to "b",
                    ),
                ),
                Triple(
                    structOf("f", varOf("V")),
                    theory(
                        { "f"("B") impliedBy "g"("B") },
                        { "f"("B") impliedBy "h"("B") },
                        { "g"("c1") },
                        { "g"("c2") },
                        { "h"("d1") },
                        { "h"("d2") },
                    ),
                    listOf(
                        "V" to "c1",
                        "V" to "c2",
                        "V" to "d1",
                        "V" to "d2",
                    ),
                ),
            )
        }
    }
}
