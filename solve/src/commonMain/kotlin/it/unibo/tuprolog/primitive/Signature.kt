package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.core.*

/** Converts this Signature to [Indicator], if possible without loosing information, otherwise returns `null` */
fun Signature.toIndicator(): Indicator? =
        when {
            this.vararg -> null
            else -> Indicator.of(this.name, this.arity)
        }

/** The signature of a query Struct or a Primitive */
data class Signature(val name: String, val arity: Int, val vararg: Boolean = false) {

    init {
        require(arity >= 0) { "Signature arity should be greater than or equals to 0: $arity" }
    }

    /** Converts this signature to a Struct `'/'([name],[arity])` or `'/'([name],'+'([arity], vararg))` */
    fun toTerm(): Struct =
            when {
                vararg -> Struct.of(FUNCTOR, Atom.of(name), Struct.of(varargStructFunctor, Integer.of(arity), varargAtom))
                else -> Struct.of(FUNCTOR, Atom.of(name), Integer.of(arity))
            }

    /** Creates corresponding Struct of this Signature with provided arguments, if conversion is possible with no information loss */
    infix fun withArgs(arguments: Iterable<Term>): Struct? = when {
        vararg -> null // because if you create a Struct, here, and then use extractSignature() the Signatures will not match
        else ->
            require(this.arity == arguments.count()) {
                "Trying to create Term of signature `$this` with wrong number of arguments ${arguments.toList()}"
            }.let { Struct.of(name, arguments.asSequence()) }
    }

    companion object {

        /** An atom to denote vararg presence */
        private val varargAtom = Atom.of("vararg")
        private const val varargStructFunctor = "+"

        /** The functor of a Signature struct */
        const val FUNCTOR = Indicator.FUNCTOR

        /** Creates a Signature instance from a well-formed Signature Struct, or returns `null` if it cannot be interpreted as Signature */
        fun fromSignatureTerm(term: Struct): Signature? = try {
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
        } catch (ex: IllegalArgumentException) { // caught when parsed arity is negative
            null
        }

        /** Creates a Signature instance from a well-formed Signature Term, or returns `null` if it cannot be interpreted as Signature */
        fun fromSignatureTerm(term: Term): Signature? = when (term) {
            is Struct -> fromSignatureTerm(term)
            else -> null
        }

        /** Creates a Signature instance from a well-formed Indicator, or returns `null` if it wasn't */
        fun fromIndicator(indicator: Indicator): Signature? = when {
            indicator.isWellFormed -> Signature(indicator.indicatedName!!, indicator.indicatedArity!!)
            else -> null
        }
    }
}

/** Extracts this [Struct] indicator and converts it to [Signature] */
fun Struct.extractSignature(): Signature = Signature.fromIndicator(indicator)!!
