@file:JvmName("ProbExtensions")

package it.unibo.tuprolog.solve

import it.unibo.tuprolog.utils.Taggable
import it.unibo.tuprolog.utils.setTag
import kotlin.jvm.JvmName
import kotlin.math.max
import kotlin.math.min

private const val PROBABILITY_TAG = "it.unibo.tuprolog.probability"

const val DEFAULT_PROBABILITY = 0.0

private fun normalize(probability: Double): Double =
    max(min(1.0, probability), 0.0)

val <T : Taggable<T>, U : T> U.probability: Double
    get() = getTag(PROBABILITY_TAG) ?: DEFAULT_PROBABILITY

fun <T : Taggable<T>, U : T> U.setProbability(value: Double): U = setTag(PROBABILITY_TAG, normalize(value))
