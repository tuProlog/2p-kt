package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.ResolutionCounters
import it.unibo.tuprolog.solve.prolog.machine.Cancellation
import it.unibo.tuprolog.solve.search.GoalSelectionPolicy
import it.unibo.tuprolog.solve.search.MetaResolutionPolicy
import it.unibo.tuprolog.solve.search.RuleSelectionPolicy
import it.unibo.tuprolog.utils.time.Chronometer
import it.unibo.tuprolog.utils.time.ChronometerFactory
import kotlin.time.Duration

/**
 * What a `:solve-prolog` resolution fixes once, from its [it.unibo.tuprolog.solve.SolveOptions] and stream, and never
 * changes while it runs: kept apart from [PrologExecutionContext], so that side effects copy it as a single reference.
 *
 * - [chronometer]: the resolution's chronometer (see [it.unibo.tuprolog.solve.Durable.chronometer]).
 * - [counters]: the resolution's opt-in [ResolutionCounters] (see [it.unibo.tuprolog.solve.SolveOptions.counters]).
 * - [interception], [ruleSelection], [goalSelection], [metaResolution]: the resolution's opt-in interceptor and
 *   selection policies (see [it.unibo.tuprolog.solve.SolveOptions]), or `null` for none.
 * - [cancellation]: the [Cancellation] of the `SolutionStream` this resolution answers or is nested in, or `null`; the
 *   solvers its context creates share it.
 */
internal class ResolutionSettings(
    val chronometer: Chronometer = ChronometerFactory.default.create(Duration.INFINITE),
    val counters: ResolutionCounters? = null,
    val interception: Interception? = null,
    val ruleSelection: RuleSelectionPolicy? = null,
    val goalSelection: GoalSelectionPolicy? = null,
    val metaResolution: MetaResolutionPolicy? = null,
    val cancellation: Cancellation? = null,
)
