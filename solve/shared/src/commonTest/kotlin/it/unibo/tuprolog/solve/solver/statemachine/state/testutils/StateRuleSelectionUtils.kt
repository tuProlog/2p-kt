package it.unibo.tuprolog.solve.solver.statemachine.state.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.solver.statemachine.state.StateRuleSelection
import it.unibo.tuprolog.theory.ClauseDatabase

/**
 * Utils singleton to help testing [StateRuleSelection]
 *
 * @author Enrico
 */
internal object StateRuleSelectionUtils {

    /**
     * Database containing two different facts:
     * ```prolog
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
     * ```prolog
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

    /**
     * Database containing multiple nested matching rules:
     * ```prolog
     * f(B) :- g(B).
     * f(B) :- h(B).
     * g(c1).
     * g(c2).
     * h(d1).
     * h(d2).
     * ```
     */
    internal val multipleNestedMatchesDatabase = ClauseDatabase.of(
            { ruleOf(structOf("f", varOf("B")), structOf("g", varOf("B"))) },
            { ruleOf(structOf("f", varOf("B")), structOf("h", varOf("B"))) },
            { factOf(structOf("g", atomOf("c1"))) },
            { factOf(structOf("g", atomOf("c2"))) },
            { factOf(structOf("h", atomOf("d1"))) },
            { factOf(structOf("h", atomOf("d2"))) }
    )
}
