@file:JvmName("ProbExtensions")

package it.unibo.tuprolog.solve

import it.unibo.tuprolog.utils.Taggable
import it.unibo.tuprolog.utils.setTag
import kotlin.jvm.JvmName
import kotlin.math.max
import kotlin.math.min

private const val PROBABILITY_TAG = "it.unibo.tuprolog.solve.probability"
private const val PROBABILISTIC_OPTION = "it.unibo.tuprolog.solve.probabilistic"

const val DEFAULT_PROBABILITY = 1.0
const val DEFAULT_PROBABILISTIC_OPTION = false

private fun normalize(probability: Double): Double =
    max(min(1.0, probability), 0.0)

val <T : Taggable<T>, U : T> U.probability: Double
    get() = getTag(PROBABILITY_TAG) ?: DEFAULT_PROBABILITY

fun <T : Taggable<T>, U : T> U.setProbability(value: Double): U = setTag(PROBABILITY_TAG, normalize(value))

val SolveOptions.isProbabilistic: Boolean get() = (customOptions[PROBABILISTIC_OPTION] as Boolean?)
    ?: DEFAULT_PROBABILISTIC_OPTION

fun SolveOptions.setProbabilistic(value: Boolean) = setOption(PROBABILISTIC_OPTION, value)
