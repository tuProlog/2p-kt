/**
 * @author Jason Dellaluce
 */

@file:JvmName("ProbExtensions")

package it.unibo.tuprolog.solve

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Taggable
import it.unibo.tuprolog.utils.setTag
import kotlin.jvm.JvmName
import kotlin.math.max
import kotlin.math.min

private const val PROBABILITY_TAG = "it.unibo.tuprolog.solve.probability"
private const val BINARY_DECISION_DIAGRAM_TAG = "it.unibo.tuprolog.solve.bdd"

private const val PROBABILISTIC_OPTION_TAG =
    "it.unibo.tuprolog.solve.options.probabilistic"

const val DEFAULT_PROBABILITY = 1.0
const val DEFAULT_PROBABILISTIC_OPTION = false

private fun normalizeProbability(probability: Double): Double =
    max(min(1.0, probability), 0.0)

/**
 * Returns the probability value assigned to this object.
 */
val <T : Taggable<T>, U : T> U.probability: Double
    get() = getTag(PROBABILITY_TAG) ?: DEFAULT_PROBABILITY

/**
 * Assigns a probability value to this object.
 */
fun <T : Taggable<T>, U : T> U.setProbability(
    value: Double
): U = setTag(PROBABILITY_TAG, normalizeProbability(value))

/**
 * Returns true if probabilistic computation option is enabled.
 * This is a "best effort" option. If the option is enabled, the
 * solver attempts to solve the given query in probabilistic mode.
 * If probabilistic computation is not supported, then the solver
 * must fall back to regular logic query resolution. In such a case,
 * the probability value of each solution would be stubbed to a
 * default value.
 */
val SolveOptions.isProbabilistic: Boolean
    get() = (customOptions[PROBABILISTIC_OPTION_TAG] as Boolean?)
        ?: DEFAULT_PROBABILISTIC_OPTION

/**
 * Sets the probabilistic computation option to [value].
 */
fun SolveOptions.setProbabilistic(
    value: Boolean
) = setOption(PROBABILISTIC_OPTION_TAG, value)

/**
 * Returns true if the solution contains a [BinaryDecisionDiagram].
 */
val Solution.hasBinaryDecisionDiagram: Boolean
    get() = containsTag(BINARY_DECISION_DIAGRAM_TAG)

/**
 * Returns the [BinaryDecisionDiagram] instance contained in the solution.
 */
val Solution.binaryDecisionDiagram: BinaryDecisionDiagram<out Term>?
    get() = getTag(BINARY_DECISION_DIAGRAM_TAG)

/**
 * Returns a new [Solution] obtained by assigning the [value] instance
 * of [BinaryDecisionDiagram] to [this] [Solution].
 */
fun Solution.setBinaryDecisionDiagram(
    value: BinaryDecisionDiagram<out Term>
) = setTag(BINARY_DECISION_DIAGRAM_TAG, value)
