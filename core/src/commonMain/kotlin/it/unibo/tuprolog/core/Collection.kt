package it.unibo.tuprolog.core

import kotlin.collections.List

interface Collection : Struct {
    val unfoldedSequence: Sequence<Term>

    val unfoldedList: List<Term>

    val unfoldedArray: Array<Term>

    val size: Int

    fun toArray(): Array<Term>

    fun toList(): List<Term>

    fun toSequence(): Sequence<Term>
}