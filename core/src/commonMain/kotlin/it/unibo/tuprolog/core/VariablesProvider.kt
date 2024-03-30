package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.VariablesProviderImpl
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.reflect.KProperty

@Suppress("PropertyName")
interface VariablesProvider : Scope {
    @JsName("scope")
    val scope: Scope

    fun copy(scope: Scope = this.scope): VariablesProvider

    @JsName("getValue")
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): Var

    @JsName("A")
    val A: Var

    @JsName("B")
    val B: Var

    @JsName("C")
    val C: Var

    @JsName("D")
    val D: Var

    @JsName("E")
    val E: Var

    @JsName("F")
    val F: Var

    @JsName("G")
    val G: Var

    @JsName("H")
    val H: Var

    @JsName("I")
    val I: Var

    @JsName("J")
    val J: Var

    @JsName("K")
    val K: Var

    @JsName("L")
    val L: Var

    @JsName("M")
    val M: Var

    @JsName("N")
    val N: Var

    @JsName("O")
    val O: Var

    @JsName("P")
    val P: Var

    @JsName("Q")
    val Q: Var

    @JsName("R")
    val R: Var

    @JsName("S")
    val S: Var

    @JsName("T")
    val T: Var

    @JsName("U")
    val U: Var

    @JsName("V")
    val V: Var

    @JsName("W")
    val W: Var

    @JsName("X")
    val X: Var

    @JsName("Y")
    val Y: Var

    @JsName("Z")
    val Z: Var

    companion object {
        @JsName("of")
        @JvmStatic
        @JvmOverloads
        fun of(scope: Scope = Scope.empty()): VariablesProvider = VariablesProviderImpl(scope)
    }
}
