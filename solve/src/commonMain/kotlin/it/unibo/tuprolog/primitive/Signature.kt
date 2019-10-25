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

    /** Creates corresponding Struct of this Signature with provided arguments */
    infix fun withArgs(arguments: Iterable<Term>): Struct = when {
        vararg -> require(arguments.count() >= this.arity) {
            "Trying to create Struct of signature `$this` with not enough arguments ${arguments.toList()}"
        }
        else -> require(arguments.count() == this.arity) {
            "Trying to create Struct of signature `$this` with wrong number of arguments ${arguments.toList()}"
        }
    }.let { Struct.of(name, arguments.asSequence()) }

    // TODO: 24/09/2019 maybe, to fully support vararg signatures, a method to check if a non vararg signature can be treated as another vararg one, should be added
    // for example: if user query is `ciao(1, 2)` with Signature("ciao", 2, false), it should be matched with
    // all signatures having same functor, vararg flag set to true and *less or equals* arity

    // a concept of precedence/inclusion should be enforced; maybe "non vararg exact match and vararg greater arity match" should win,
    // in this matching system
    // the method doing this could be called "includedBy(Signature)"

    // a method to retrieve matching vararg Signatures should also be added to [Library] interface, and return only one result;
    // this method should use "includedBy(Signature)" to retrieve matches and the sort them with the rule above

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
fun Struct.toSignature(): Signature = Signature.fromIndicator(indicator)!!
