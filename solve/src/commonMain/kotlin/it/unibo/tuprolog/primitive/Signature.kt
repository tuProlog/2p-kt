package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct

/** The signature of a Primitive */
data class Signature(val name: String, val arity: Int, val vararg: Boolean = false) {

    init {
        require(arity >= 0) { "Signature arity should be greater than or equals to 0: $arity" }
    }

    /** Converts this signature to a Struct */
    fun toTerm(): Struct =
            when {
                vararg -> Struct.of(FUNCTOR, Atom.of(name), Struct.of("+", Numeric.of(arity), Atom.of("vararg")))
                else -> Struct.of(FUNCTOR, Atom.of(name), Numeric.of(arity))
            }

    companion object {

        /** The functor of a Signature struct */
        const val FUNCTOR = Indicator.FUNCTOR
    }
}

/**
 * Converts this Signature to [Indicator], if possible without loosing information
 *
 * @throws [IllegalStateException] when conversion would produce loss of information
 */
fun Signature.toIndicator(): Indicator = when {
    this.vararg -> throw IllegalStateException("Trying to convert a vararg Signature to Indicator, this will produce loss of information: $this")
    else -> Indicator.of(this.name, this.arity)
}
