package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Var
import kotlin.js.JsName

@Suppress("PropertyName")
interface VariablesAwareScope : LogicProgrammingAwareScope {

    @JsName("A")
    val A: Var get() = varOf("A")

    @JsName("B")
    val B: Var get() = varOf("B")

    @JsName("C")
    val C: Var get() = varOf("C")

    @JsName("D")
    val D: Var get() = varOf("D")

    @JsName("E")
    val E: Var get() = varOf("E")

    @JsName("F")
    val F: Var get() = varOf("F")

    @JsName("G")
    val G: Var get() = varOf("G")

    @JsName("H")
    val H: Var get() = varOf("H")

    @JsName("I")
    val I: Var get() = varOf("I")

    @JsName("J")
    val J: Var get() = varOf("J")

    @JsName("K")
    val K: Var get() = varOf("K")

    @JsName("L")
    val L: Var get() = varOf("L")

    @JsName("M")
    val M: Var get() = varOf("M")

    @JsName("N")
    val N: Var get() = varOf("N")

    @JsName("O")
    val O: Var get() = varOf("O")

    @JsName("P")
    val P: Var get() = varOf("P")

    @JsName("Q")
    val Q: Var get() = varOf("Q")

    @JsName("R")
    val R: Var get() = varOf("R")

    @JsName("S")
    val S: Var get() = varOf("S")

    @JsName("T")
    val T: Var get() = varOf("T")

    @JsName("U")
    val U: Var get() = varOf("U")

    @JsName("V")
    val V: Var get() = varOf("V")

    @JsName("W")
    val W: Var get() = varOf("W")

    @JsName("X")
    val X: Var get() = varOf("X")

    @JsName("Y")
    val Y: Var get() = varOf("Y")

    @JsName("Z")
    val Z: Var get() = varOf("Z")
}
