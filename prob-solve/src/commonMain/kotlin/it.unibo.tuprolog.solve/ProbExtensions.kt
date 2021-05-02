@file:JvmName("ProbExtensions")

package it.unibo.tuprolog.solve

import it.unibo.tuprolog.utils.Taggable
import it.unibo.tuprolog.utils.setTag
import kotlin.jvm.JvmName
import kotlin.math.max
import kotlin.math.min

private const val PROBABILITY_TAG = "it.unibo.tuprolog.solve.probability"
private const val DOT_REPRESENTATION_TAG = "it.unibo.tuprolog.solve.representation.dot"
private const val DOT_REPRESENTATION_OPTION_TAG = "it.unibo.tuprolog.solve.options.dot_representation"
private const val PROBABILISTIC_OPTION_TAG = "it.unibo.tuprolog.solve.options.probabilistic"

const val DEFAULT_PROBABILITY = 1.0
const val DEFAULT_PROBABILISTIC_OPTION = false
const val DEFAULT_DOT_REPRESENTATION_OPTION = false

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
 * Returns true the DOT representation option is enabled.
 * It so, solvers attempt to provide a string representation
 * of [Solution]s using using Graphviz DOT
 * notation (https://graphviz.org/). It can then be retrieved
 * through the [Solution.dotGraphRepresentation] method. Note, this
 * option is ignored if [SolveOptions.isProbabilistic] is set
 * to false, as this is a feature specific to probabilistic
 * computation.
 */
val SolveOptions.isDotGraphRepresentation: Boolean
    get() = (customOptions[DOT_REPRESENTATION_OPTION_TAG] as Boolean?)
        ?: DEFAULT_DOT_REPRESENTATION_OPTION

/**
 * Sets the DOT representation option to [value]. See
 * [SolveOptions.isDotGraphRepresentation] for reference.
 */
fun SolveOptions.setDotGraphRepresentation(
    value: Boolean
) = setOption(DOT_REPRESENTATION_OPTION_TAG, value)

/**
 * Returns true if probabilistic computation option is enabled.
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
 * Returns a string representation of the solution using Graphviz DOT
 * notation (https://graphviz.org/). This is supported only by solutions
 * defined over a graph data structure. Returns null if the functionality
 * is not supported.
 */
val Solution.dotGraphRepresentation: String? get() = getTag(DOT_REPRESENTATION_TAG)

/**
 * Sets a string representation of the solution using Graphviz DOT
 * notation (https://graphviz.org/).
 */
fun Solution.setDotGraphRepresentation(
    value: String
) = setTag(DOT_REPRESENTATION_TAG, value)
