package it.unibo.tuprolog.libraries

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.atomOf
import it.unibo.tuprolog.core.numOf
import it.unibo.tuprolog.core.structOf

data class Signature(val name: String, val arity: String, val vararg: Boolean) {

    fun toTerm(): Struct =
            if (vararg) {
                structOf("/", atomOf(name), structOf("+", numOf(arity), atomOf("vararg")))
            } else {
                structOf("/", atomOf(name), numOf(arity))
            }

}

