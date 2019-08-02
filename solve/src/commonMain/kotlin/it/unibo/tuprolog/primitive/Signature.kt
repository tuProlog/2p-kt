package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.core.*

/** The signature of a Primitive */
data class Signature(val name: String, val arity: Int, val vararg: Boolean = false) {

    init {
        require(arity >= 0) { "Signature arity should be greater than or equals to 0: $arity" }
    }

    /** Converts this signature to a Struct */
    fun toTerm(): Struct =
            when {
                vararg -> Struct.of(FUNCTOR, Atom.of(name), Struct.of(varargStructFunctor, Integer.of(arity), varargAtom))
                else -> Struct.of(FUNCTOR, Atom.of(name), Integer.of(arity))
            }

    companion object {

        /** An atom to denote vararg presence */
        private val varargAtom = Atom.of("vararg")
        private const val varargStructFunctor = "+"

        /** The functor of a Signature struct */
        const val FUNCTOR = Indicator.FUNCTOR

        /** Creates a Signature instance from a well-formed Term, or returns `null` if it cannot be interpreted as Signature */
        fun fromTerm(term: Struct): Signature? = try {
            with(term) {
                when {
                    functor == FUNCTOR && arity == 2 && args.first().isAtom -> when {
                        args.last().isInt ->
                            Signature(
                                    args.first().`as`<Atom>().value,
                                    args.last().`as`<Integer>().intValue.toInt()
                            )

                        with(args.last()) {
                            this is Struct && functor == varargStructFunctor && arity == 2 &&
                                    args.first().isInt &&
                                    args.last() == varargAtom
                        } -> Signature(
                                args.first().`as`<Atom>().value,
                                args.last().`as`<Struct>()[0].`as`<Integer>().intValue.toInt(),
                                true
                        )

                        else -> null
                    }
                    else -> null
                }
            }
        } catch (ex: IllegalArgumentException) { // catch when parsed arity is negative
            null
        }

        /** Creates a Signature instance from a well-formed Term, or returns `null` if it cannot be interpreted as Signature */
        fun fromTerm(term: Term): Signature? = when (term) {
            is Struct -> fromTerm(term)
            else -> null
        }
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
