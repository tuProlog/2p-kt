@file:JvmName("ProbExtensions")

package it.unibo.tuprolog.solve

import it.unibo.tuprolog.utils.Taggable
import it.unibo.tuprolog.utils.addTag
import kotlin.jvm.JvmName

const val PROBABILITY_TAG = "it.unibo.tuprolog.probability"

var <T : Taggable<T>, U : T> U.probability: Double?
    get() = getTag(PROBABILITY_TAG)
    set(value) {
        addTag(PROBABILITY_TAG to value)
    }