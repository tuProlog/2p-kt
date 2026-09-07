/**
 * This module bolts probabilistic-logic-programming (PLP) concerns onto the plain `:solve` API,
 * without changing either `it.unibo.tuprolog.solve.SolveOptions` or `it.unibo.tuprolog.solve.Solution`:
 * every extra bit of information (a term/solution's assigned probability, whether probabilistic
 * resolution was requested, the [BinaryDecisionDiagram] backing a solution's probability) rides
 * along as an [it.unibo.tuprolog.utils.Taggable] tag, keyed by one of the private `*_TAG` constants
 * below.
 *
 * In PLP, facts/clauses are annotated with a probability (e.g. `0.3::burglary.` in ProbLog-style
 * syntax) and a query's solutions are reported together with the probability that they hold, computed
 * by combining the probabilities of every fact used to prove them. This module does not parse that
 * syntax nor perform the actual probability computation itself (that lives in the `:solve-problog`
 * module, which depends on this one and on `it.unibo.tuprolog.bdd.BinaryDecisionDiagram` for
 * Weighted Model Counting) — it only defines the shared vocabulary: how a probability is attached to
 * a [it.unibo.tuprolog.core.Term]-like object, how a caller opts into probabilistic resolution via
 * [it.unibo.tuprolog.solve.SolveOptions], and how a probabilistic [it.unibo.tuprolog.solve.Solution]
 * carries both its probability and (optionally) the [BinaryDecisionDiagram] that explains it.
 *
 * @author Jason Dellaluce
 */

@file:JvmName("ProbExtensions")

package it.unibo.tuprolog.solve

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Taggable
import it.unibo.tuprolog.utils.setTag
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.math.max
import kotlin.math.min

private const val PROBABILITY_TAG = "it.unibo.tuprolog.solve.probability"
private const val BINARY_DECISION_DIAGRAM_TAG = "it.unibo.tuprolog.solve.bdd"

private const val PROBABILISTIC_OPTION_TAG =
    "it.unibo.tuprolog.solve.options.probabilistic"

/**
 * The probability assigned to a [Taggable] object (e.g. a [Term] or a [Solution]) that has never
 * had [setProbability] called on it, i.e. one that is treated as certain (probability 1.0). This is
 * also the fallback used by [ProblogSolver][it.unibo.tuprolog.solve.problog.ProblogSolver]-like
 * solvers when [SolveOptions.isProbabilistic] is `false`.
 */
const val DEFAULT_PROBABILITY = 1.0

/**
 * The default value of [SolveOptions.isProbabilistic]: probabilistic resolution is opt-in, so plain
 * logic-programming queries are unaffected unless a caller explicitly turns it on via
 * [SolveOptions.setProbabilistic] or [SolveOptions.probabilistic].
 */
const val DEFAULT_PROBABILISTIC_OPTION = false

private fun normalizeProbability(probability: Double): Double = max(min(1.0, probability), 0.0)

/**
 * The probability value assigned to this object via [setProbability], or [DEFAULT_PROBABILITY]
 * (certainty) if none was ever assigned. Any [it.unibo.tuprolog.utils.Taggable] object can carry a
 * probability this way; in practice this is read off [it.unibo.tuprolog.core.Term]s representing
 * probabilistic facts/clauses (e.g. by `:solve-problog`'s knowledge-base primitives) and off
 * [Solution]s produced by a probabilistic solver.
 *
 * Example:
 * ```kotlin
 * val fact = Rule.of(Atom.of("hello"), Atom.of("world"))
 * fact.probability // == DEFAULT_PROBABILITY (1.0), since it was never set
 * fact.setProbability(0.5).probability // == 0.5
 * ```
 */
val <T : Taggable<T>, U : T> U.probability: Double
    get() = getTag(PROBABILITY_TAG) ?: DEFAULT_PROBABILITY

/**
 * Returns a new instance, otherwise equivalent to this one, tagged with [value] as its [probability].
 * [value] is clamped into the `[0.0, 1.0]` range before being stored (e.g. `1.5` is stored as `1.0`,
 * `-1.0` as `0.0`), so [probability] always reads back a valid probability — with the notable
 * exception of `Double.NaN`, which [normalizeProbability]'s `min`/`max` comparisons let through
 * unchanged (used by `:solve-problog`'s `ProblogSolver` to flag a solution whose probability could
 * not be computed).
 *
 * As with every [it.unibo.tuprolog.utils.Taggable] mutator, the receiver itself is left untouched;
 * the new probability is only visible on the returned instance.
 */
@JsName("setProbability")
fun <T : Taggable<T>, U : T> U.setProbability(value: Double): U = setTag(PROBABILITY_TAG, normalizeProbability(value))

/**
 * Whether the probabilistic-computation option is enabled on these [SolveOptions] (`false` by
 * default, see [DEFAULT_PROBABILISTIC_OPTION]). This is a "best effort" option: a solver that
 * supports probabilistic resolution attempts to solve the query in probabilistic mode when this is
 * `true`, tagging each [Solution] with its computed [probability] (and possibly a
 * [binaryDecisionDiagram] explaining it); a solver that does not support it falls back to plain
 * logic-programming resolution, in which case every solution's [probability] reads back the
 * [DEFAULT_PROBABILITY] stub rather than an actually computed value.
 */
val SolveOptions.isProbabilistic: Boolean
    get() =
        (customOptions[PROBABILISTIC_OPTION_TAG] as Boolean?)
            ?: DEFAULT_PROBABILISTIC_OPTION

/**
 * Returns a copy of these [SolveOptions] with the probabilistic-computation option ([isProbabilistic])
 * set to [value].
 */
@JsName("setProbabilistic")
fun SolveOptions.setProbabilistic(value: Boolean) = setOption(PROBABILISTIC_OPTION_TAG, value)

/**
 * Returns a copy of these [SolveOptions] with the probabilistic-computation option ([isProbabilistic])
 * turned on. Shorthand for `setProbabilistic(true)`.
 */
@JsName("probabilistic")
fun SolveOptions.probabilistic() = setProbabilistic(true)

/**
 * Whether this [Solution] was tagged with a [BinaryDecisionDiagram] via [setBinaryDecisionDiagram],
 * i.e. whether [binaryDecisionDiagram] would return a non-`null` value. A probabilistic solver only
 * attaches one when it actually managed to compute an explanation for the solution's [probability];
 * e.g. a solution obtained while [SolveOptions.isProbabilistic] is `false` never carries one.
 */
val Solution.hasBinaryDecisionDiagram: Boolean
    get() = containsTag(BINARY_DECISION_DIAGRAM_TAG)

/**
 * The [BinaryDecisionDiagram] attached to this [Solution] via [setBinaryDecisionDiagram], or `null`
 * if none was attached (see [hasBinaryDecisionDiagram]). Where present, the diagram is the Boolean
 * formula (over the probabilistic facts/clauses used to prove the query) that a probabilistic solver
 * derived this solution's [probability] from via Weighted Model Counting — see
 * `it.unibo.tuprolog.bdd.BinaryDecisionDiagram` for how such a diagram is built up and expanded.
 */
val Solution.binaryDecisionDiagram: BinaryDecisionDiagram<out Term>?
    get() = getTag(BINARY_DECISION_DIAGRAM_TAG)

/**
 * Returns a new [Solution], otherwise equivalent to this one, tagged with [value] as its
 * [binaryDecisionDiagram]. Used by probabilistic solvers to attach the explanation a solution's
 * [probability] was computed from, so that callers can inspect (or recompute against) it later
 * without re-deriving it from the original goal.
 */
@JsName("setBinaryDecisionDiagram")
fun Solution.setBinaryDecisionDiagram(value: BinaryDecisionDiagram<out Term>) =
    setTag(BINARY_DECISION_DIAGRAM_TAG, value)
