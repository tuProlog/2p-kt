package it.unibo.tuprolog.solve.solver.statemachine.state.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.statemachine.state.StateRuleSelection
import it.unibo.tuprolog.solve.testutils.DummyInstances
import it.unibo.tuprolog.theory.ClauseDatabase

/**
 * Utils singleton to help testing [StateRuleSelection]
 *
 * @author Enrico
 */
internal object StateRuleSelectionUtils {

    /**
     * Database containing two different facts:
     * ```
     * a.
     * b.
     * ```
     */
    internal val singleMatchDatabase = ClauseDatabase.of(
            Fact.of(Atom.of("a")),
            Fact.of(Atom.of("b"))
    )

    /** Database containing three rules like `f(_)` and one rule `a(b)`:
     *
     * ```
     * f(x) :- failed.
     * f(y) :- a(b).
     * f(z).
     * a(b).
     * ```
     */
    internal val multipleMatchesDatabase = ClauseDatabase.of(
            Rule.of(Struct.of("f", Atom.of("x")), Struct.of("failed")),
            Rule.of(Struct.of("f", Atom.of("y")), Struct.of("a", Atom.of("b"))),
            Rule.of(Struct.of("f", Atom.of("z"))),
            Fact.of(Struct.of("a", Atom.of("b")))
    )

    /** Function to create request over a specific database */
    internal fun createRequestWith(query: Struct, database: ClauseDatabase) = Solve.Request(
            Signature.fromIndicator(query.indicator)!!,
            query.argsList,
            DummyInstances.executionContext.copy(libraries = Libraries(Library.of(
                    alias = "test",
                    theory = database
            )))
    )
}
