package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.impl.ObjectToTermConverterImpl
import kotlin.jvm.JvmStatic

interface ObjectToTermConverter {
    fun convert(source: Any?): Term

    companion object {
        @JvmStatic
        val default: ObjectToTermConverter = ObjectToTermConverterImpl()
    }
}
