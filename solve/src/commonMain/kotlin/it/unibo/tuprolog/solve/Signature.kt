package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermConvertible
import kotlin.js.JsName

/** The signature of a query Struct or a Primitive */
data class Signature(
    @JsName("name")
    val name: String,
    @JsName("arity")
    val arity: Int,
    @JsName("vararg")
    val vararg: Boolean = false,
) : TermConvertible {
    init {
        require(arity >= 0) { "Signature arity should be greater than or equals to 0: $arity" }
    }

    /** Converts this signature to a Struct `'/'([name], [arity])` or `'/'([name],'+'([arity], vararg))` */
    override fun toTerm(): Struct =
        when {
            vararg -> {
                Struct.of(
                    FUNCTOR,
                    Atom.of(name),
                    Struct.of(
                        VARARG_FUNCTOR,
                        Integer.of(arity),
                        varargAtom,
                    ),
                )
            }
            else -> {
                Struct.of(FUNCTOR, Atom.of(name), Integer.of(arity))
            }
        }

    /** Converts this Signature to [Indicator], if possible without loosing information, otherwise throws an exception */
    @JsName("toIndicator")
    fun toIndicator(): Indicator =
        when {
            this.vararg -> TODO("Implement conversion to indicator in case of vararg signature")
            else -> Indicator.of(this.name, this.arity)
        }

    /** Creates corresponding Struct of this Signature with provided arguments */
    @JsName("withArgs")
    infix fun withArgs(arguments: Iterable<Term>): Struct =
        when {
            vararg ->
                require(arguments.count() >= this.arity) {
                    "Trying to create Struct of signature `$this` with not enough arguments ${arguments.toList()}"
                }
            else ->
                require(arguments.count() == this.arity) {
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
        private const val VARARG_FUNCTOR = "+"

        /** The functor of a Signature struct */
        const val FUNCTOR = Indicator.FUNCTOR

        /** Creates a Signature instance from a well-formed Signature Struct, or returns `null` if it cannot be interpreted as Signature */
        @JsName("fromSignatureStruct")
        fun fromSignatureTerm(term: Struct): Signature? =
            try {
                with(term) {
                    when {
                        functor == FUNCTOR && arity == 2 && args.first().isAtom ->
                            when {
                                args.last().isInteger -> {
                                    Signature(
                                        args.first().castToAtom().value,
                                        args
                                            .last()
                                            .castToInteger()
                                            .intValue
                                            .toInt(),
                                    )
                                }
                                args.last().let {
                                    it is Struct &&
                                        it.functor == VARARG_FUNCTOR &&
                                        it.arity == 2 &&
                                        it.args.first().isInteger &&
                                        it.args.last() == varargAtom
                                } -> {
                                    Signature(
                                        args.first().castToAtom().value,
                                        args
                                            .last()
                                            .castToStruct()[0]
                                            .castToInteger()
                                            .intValue
                                            .toInt(),
                                        true,
                                    )
                                }
                                else -> null
                            }
                        else -> null
                    }
                }
            } catch (ex: IllegalArgumentException) {
                // caught when parsed arity is negative
                null
            }

        /** Creates a Signature instance from a well-formed Signature Term, or returns `null` if it cannot be interpreted as Signature */
        @JsName("fromSignatureTerm")
        fun fromSignatureTerm(term: Term): Signature? =
            when (term) {
                is Struct -> fromSignatureTerm(term)
                else -> null
            }

        /** Creates a Signature instance from a well-formed Indicator, or returns `null` if it wasn't */
        @JsName("fromIndicator")
        fun fromIndicator(indicator: Indicator): Signature? =
            when {
                indicator.isWellFormed -> {
                    Signature(
                        indicator.indicatedName!!,
                        indicator.indicatedArity!!,
                    )
                }
                else -> null
            }
    }
}
