package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

/**
 * Common abstraction of [Term]s made of a functor and a (possibly empty) list of arguments.
 *
 * Its concrete sub-types refine [functor] covariantly: a [Struct] has a [String] functor, whereas a [Pattern] has a
 * [Var] functor. [functorTerm] is the [Term] form of the functor,
 * and [indicator] is always `functorTerm/arity`.
 *
 * [Compound.of] is the canonical factory: it selects the most specific sub-type from the functor's type, delegating
 * to [Struct.of] (which in turn selects specialised [Struct]s such as [Atom], [List], [Tuple], [Clause]...) or to
 * [Pattern.of].
 */
interface Compound : Term {
    override val isCompound: Boolean
        get() = true

    override fun asCompound(): Compound = this

    override fun freshCopy(): Compound

    override fun freshCopy(scope: Scope): Compound

    /** The functor of this [Compound]: sub-types refine its type covariantly. */
    @JsName("functor")
    val functor: Any

    /** The functor of this [Compound], as a [Term]. */
    @JsName("functorTerm")
    val functorTerm: Term

    /** The total amount of arguments of this [Compound], i.e. the length of [args]. */
    @JsName("arity")
    val arity: Int
        get() = args.size

    /** The indicator corresponding to this [Compound], i.e. [functorTerm]/[arity]. */
    @JsName("indicator")
    val indicator: Indicator
        get() = Indicator.of(functorTerm, Integer.of(arity))

    /** List of arguments of this [Compound]. */
    @JsName("argsList")
    val args: KtList<Term>

    /** Sequence of arguments of this [Compound]. */
    @JsName("argsSequence")
    val argsSequence: Sequence<Term>
        get() = args.asSequence()

    /**
     * Gets the [index]-th argument of this [Compound].
     * @throws IndexOutOfBoundsException if [index] is lower than 0 or greater or equal to [arity]
     */
    @JsName("getArgAt")
    fun getArgAt(index: Int): Term = args[index]

    /** Alias for [getArgAt], enabling the syntax `compound[index]`. */
    @JsName("get")
    operator fun get(index: Int): Term = getArgAt(index)

    /** Creates a copy of this [Compound] whose arguments are [args], and whose functor is unchanged. */
    @JsName("setArgs")
    fun setArgs(vararg args: Term): Compound

    /** @see setArgs */
    @JsName("setArgsIterable")
    fun setArgs(args: Iterable<Term>): Compound

    /** @see setArgs */
    @JsName("setArgsSequence")
    fun setArgs(args: Sequence<Term>): Compound

    companion object {
        /**
         * Creates a new [Compound], of the most specific sub-type, given its [functor] and [args].
         * A [String] or [Atom] functor delegates to [Struct.of], a [Var] functor delegates to [Pattern.of].
         * @throws IllegalArgumentException if [functor] is not a valid functor
         */
        @JvmStatic
        @JsName("ofList")
        fun of(
            functor: Any,
            args: KtList<Term>,
        ): Compound =
            when (functor) {
                is String -> Struct.of(functor, args)
                is Atom -> Struct.of(functor.value, args)
                is Var -> Pattern.of(functor, args)
                else -> throw IllegalArgumentException(
                    "Invalid functor for a compound term: $functor (a String, an Atom, or a Var is required)",
                )
            }

        /** @see of */
        @JvmStatic
        @JsName("of")
        fun of(
            functor: Any,
            vararg args: Term,
        ): Compound = of(functor, args.toList())

        /** @see of */
        @JvmStatic
        @JsName("ofIterable")
        fun of(
            functor: Any,
            args: Iterable<Term>,
        ): Compound = of(functor, args.toList())

        /** @see of */
        @JvmStatic
        @JsName("ofSequence")
        fun of(
            functor: Any,
            args: Sequence<Term>,
        ): Compound = of(functor, args.toList())
    }
}
