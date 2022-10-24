package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.ui.gui.impl.HistoryImpl
import it.unibo.tuprolog.utils.observe.Observable
import kotlin.js.JsName
import kotlin.jvm.JvmField

interface History<T> {
    @JsName("items")
    val items: List<T>

    @JsName("selectedIndex")
    var selectedIndex: Int

    @JsName("append")
    fun append(item: T)

    @JsName("onAppended")
    val onAppended: Observable<Event<T>>

    @JsName("onSelected")
    val onSelected: Observable<Event<Pair<Int, T>>>

    companion object {
        @JvmField
        val EVENT_APPENDED = History<*>::onAppended.name

        @JvmField
        val EVENT_SELECTED = History<*>::onSelected.name

        @JsName("of")
        fun <T> of(first: T, vararg others: T): History<T> = HistoryImpl(first, *others)

        @JsName("empty")
        fun <T> empty(): History<T> = HistoryImpl()
    }
}
