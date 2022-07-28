package it.unibo.tuprolog.solve.library

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.LogicFunction
import it.unibo.tuprolog.solve.library.impl.LibraryImpl
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/** Represents a Prolog library */
interface Library {

    /** The library alias */
    @JsName("alias")
    val alias: String

    /** Library defined operators */
    @JsName("operators")
    val operators: OperatorSet

    /** The library theory clauses */
    @JsName("theory")
    val theory: Theory

    /** The library primitives, identified by their signatures */
    @JsName("primitives")
    val primitives: Map<Signature, Primitive>

    /** The library prolog functions, identified by their signature */
    @JsName("functions")
    val functions: Map<Signature, LogicFunction>

    @JsName("toRuntime")
    fun toRuntime(): Runtime = Runtime.of(this)

    /**
     * Checks whether this library contains the provided signature.
     *
     * The default implementation, checks for signature presence among primitives and theory clauses by indicator-like search
     */
    @JsName("containsSignature")
    operator fun contains(signature: Signature): Boolean =
        primitives.containsKey(signature) ||
            signature.toIndicator().let { theory.contains(it) }

    /** Checks whether this library contains the definition of provided operator */
    @JsName("containsOperator")
    operator fun contains(operator: Operator): Boolean = operator in operators

    /** Checks whether this library has a [Primitive] with provided signature */
    @JsName("hasPrimitive")
    fun hasPrimitive(signature: Signature): Boolean = signature in primitives.keys

    /** Checks whether the provided signature, is protected in this library */
    @JsName("hasProtected")
    fun hasProtected(signature: Signature): Boolean = signature in this

    @JsName("plus")
    operator fun plus(other: Library): Runtime = Runtime.of(this, other)

    companion object {

        /** The character used to separate library alias from original name */
        const val ALIAS_SEPARATOR = "."

        private const val DEFAULT_ALIAS = "default"

        private val ALIAS_PATTERN = "^\\w+(\\${ALIAS_SEPARATOR}\\w+)*$".toRegex()

        @JvmStatic
        @JsName("sequenceToMapEnsuringNoDuplicates")
        fun <T> Sequence<Pair<Signature, T>>.toMapEnsuringNoDuplicates(): Map<Signature, T> {
            val result = mutableMapOf<Signature, T>()
            for ((signature, value) in this) {
                if (result.containsKey(signature)) {
                    throw IllegalArgumentException("Repeated entry: $signature")
                }
                result[signature] = value
            }
            return result
        }

        @JvmStatic
        @JsName("iterableToMapEnsuringNoDuplicates")
        fun <T> Iterable<Pair<Signature, T>>.toMapEnsuringNoDuplicates(): Map<Signature, T> =
            asSequence().toMapEnsuringNoDuplicates()

        /** Creates an instance of [Library] with given parameters */
        @JvmStatic
        @JsName("of")
        @JvmOverloads
        fun of(
            alias: String,
            primitives: Map<Signature, Primitive> = emptyMap(),
            theory: Theory = Theory.empty(),
            operators: OperatorSet = OperatorSet(),
            functions: Map<Signature, LogicFunction> = emptyMap()
        ): Library {
            require(ALIAS_PATTERN.matches(alias)) {
                "Aliases should match the pattern $ALIAS_PATTERN"
            }
            return LibraryImpl(alias, operators, theory, primitives, functions)
        }

        @JvmStatic
        @JsName("unaliased")
        @JvmOverloads
        fun of(
            primitives: Map<Signature, Primitive> = emptyMap(),
            theory: Theory = Theory.empty(),
            operators: OperatorSet = OperatorSet(),
            functions: Map<Signature, LogicFunction> = emptyMap()
        ): Library = of(DEFAULT_ALIAS, primitives, theory, operators, functions)

        @JvmStatic
        @JsName("changingAlias")
        @JvmOverloads
        fun of(
            alias: String = DEFAULT_ALIAS,
            library: Library
        ): Library = of(alias, library.primitives, library.theory, library.operators, library.functions)
    }
}
