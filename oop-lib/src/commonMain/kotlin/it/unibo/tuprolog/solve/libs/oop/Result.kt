package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.ToTermConvertible

sealed class Result {
    object None : Result()

    data class Value(val value: Any?) : Result(), ToTermConvertible {

        private val termValue by lazy {
            ObjectToTermConverter.default.convert(value)
        }

        override fun toTerm(): Term = termValue
    }
}