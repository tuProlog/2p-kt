package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.impl.ObjectToTermConverterImpl

interface ObjectToTermConverter {
    fun convert(source: Any?): Term

    companion object {
        val default: ObjectToTermConverter = ObjectToTermConverterImpl()
    }
}