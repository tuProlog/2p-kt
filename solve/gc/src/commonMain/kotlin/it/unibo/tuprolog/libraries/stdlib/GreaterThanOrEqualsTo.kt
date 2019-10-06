package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Numeric

object GreaterThanOrEqualsTo : ArithmeticRelation(">=") {
    override fun arithmeticRelation(x: Numeric, y: Numeric): Boolean = x >= y
}