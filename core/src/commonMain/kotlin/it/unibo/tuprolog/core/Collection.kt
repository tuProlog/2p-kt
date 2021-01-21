package it.unibo.tuprolog.core

import kotlin.collections.List
import kotlin.js.JsName

interface Collection : Struct {

    @JsName("unfoldedSequence")
    val unfoldedSequence: Sequence<Term>

    @JsName("unfoldedList")
    val unfoldedList: List<Term>

    @JsName("unfoldedArray")
    val unfoldedArray: Array<Term>

    @JsName("size")
    val size: Int

    @JsName("toArray")
    fun toArray(): Array<Term>

    @JsName("toList")
    fun toList(): List<Term>

    @JsName("toSequence")
    fun toSequence(): Sequence<Term>

    @JsName("unfold")
    fun unfold(): Sequence<Term>

    override fun freshCopy(): Collection

    override fun freshCopy(scope: Scope): Collection
}
