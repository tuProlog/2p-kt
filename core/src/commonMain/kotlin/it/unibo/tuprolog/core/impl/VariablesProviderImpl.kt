package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.VariablesProvider
import kotlin.reflect.KProperty

internal class VariablesProviderImpl(
    override val scope: Scope,
) : VariablesProvider,
    Scope by scope {
    override fun copy(scope: Scope): VariablesProvider = VariablesProviderImpl(scope)

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): Var = varOf(property.name)

    override val A: Var get() = varOf("A")

    override val B: Var get() = varOf("B")

    override val C: Var get() = varOf("C")

    override val D: Var get() = varOf("D")

    override val E: Var get() = varOf("E")

    override val F: Var get() = varOf("F")

    override val G: Var get() = varOf("G")

    override val H: Var get() = varOf("H")

    override val I: Var get() = varOf("I")

    override val J: Var get() = varOf("J")

    override val K: Var get() = varOf("K")

    override val L: Var get() = varOf("L")

    override val M: Var get() = varOf("M")

    override val N: Var get() = varOf("N")

    override val O: Var get() = varOf("O")

    override val P: Var get() = varOf("P")

    override val Q: Var get() = varOf("Q")

    override val R: Var get() = varOf("R")

    override val S: Var get() = varOf("S")

    override val T: Var get() = varOf("T")

    override val U: Var get() = varOf("U")

    override val V: Var get() = varOf("V")

    override val W: Var get() = varOf("W")

    override val X: Var get() = varOf("X")

    override val Y: Var get() = varOf("Y")

    override val Z: Var get() = varOf("Z")
}
