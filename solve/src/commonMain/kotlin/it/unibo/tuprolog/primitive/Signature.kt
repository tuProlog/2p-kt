package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct

data class Signature(val name: String, val arity: Int, val vararg: Boolean) {

    fun toTerm(): Struct =
            if (vararg) {
                Struct.of("/", Atom.of(name), Struct.of("+", Numeric.of(arity), Atom.of("vararg")))
            } else {
                Struct.of("/", Atom.of(name), Numeric.of(arity))
            }

}

