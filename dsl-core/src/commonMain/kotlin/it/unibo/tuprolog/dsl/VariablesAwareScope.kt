package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Var

@Suppress("PropertyName")
interface VariablesAwareScope : PrologAwareScope {
    val A: Var get() = varOf("A")
    val B: Var get() = varOf("B")
    val C: Var get() = varOf("C")
    val D: Var get() = varOf("D")
    val E: Var get() = varOf("E")
    val F: Var get() = varOf("F")
    val G: Var get() = varOf("G")
    val H: Var get() = varOf("H")
    val I: Var get() = varOf("I")
    val J: Var get() = varOf("J")
    val K: Var get() = varOf("K")
    val L: Var get() = varOf("L")
    val M: Var get() = varOf("M")
    val N: Var get() = varOf("N")
    val O: Var get() = varOf("O")
    val P: Var get() = varOf("P")
    val Q: Var get() = varOf("Q")
    val R: Var get() = varOf("R")
    val S: Var get() = varOf("S")
    val T: Var get() = varOf("T")
    val U: Var get() = varOf("U")
    val V: Var get() = varOf("V")
    val W: Var get() = varOf("W")
    val X: Var get() = varOf("X")
    val Y: Var get() = varOf("Y")
    val Z: Var get() = varOf("Z")
}