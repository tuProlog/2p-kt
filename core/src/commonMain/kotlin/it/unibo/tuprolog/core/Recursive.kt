package it.unibo.tuprolog.core

import kotlin.collections.List
import kotlin.js.JsName

interface Recursive : Struct {

    override val isRecursive: Boolean
        get() = true

    override fun asRecursive(): Recursive = this

    @JsName("unfoldedSequence")
    val unfoldedSequence: Sequence<Term>

    @JsName("unfoldedList")
    val unfoldedList: List<Term>

    @JsName("unfoldedArray")
    val unfoldedArray: Array<Term>

    @JsName("size")
    val size: Int

    @JsName("items")
    val items: Iterable<Term>
        get() = toSequence().asIterable()

    @JsName("toArray")
    fun toArray(): Array<Term>

    @JsName("toList")
    fun toList(): List<Term>

    @JsName("toSequence")
    fun toSequence(): Sequence<Term>

    @JsName("unfold")
    fun unfold(): Sequence<Term>

    override fun freshCopy(): Recursive

    override fun freshCopy(scope: Scope): Recursive
}
