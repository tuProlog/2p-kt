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

/**
 * A type for logic libraries, i.e. _aliases_ aggregates of (possibly empty) collections of
 * - [Operator]s, grouped into an [OperatorSet],
 * - clauses, rules, or directives, grouped into a [Theory],
 * - [Primitive]s, indexed by [Signature],
 * - [LogicFunction]s, indexed by [Signature].
 *
 * A library alias is aimed at identifying the library in the eyes of a solver.
 * */
interface Library {

    /** The alias identifying this library */
    @JsName("alias")
    val alias: String

    /** Operators to be loaded by a solver when the library is used */
    @JsName("operators")
    val operators: OperatorSet

    /** Rules, facts, or directories to be loaded by a solver when the library is used */
    @JsName("theory")
    val theory: Theory

    /** [Primitive]s to be loaded by a solver when the library is used,
     * indexed by their [Signature] in the eyes of the solver */
    @JsName("primitives")
    val primitives: Map<Signature, Primitive>

    /** [LogicFunction]s to be loaded by a solver when the library is used,
     * indexed by their [Signature] in the eyes of the solver */
    @JsName("functions")
    val functions: Map<Signature, LogicFunction>

    /** Converts the current library into a [Runtime] containing a single library */
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

    override fun equals(other: Any?): Boolean // Leave this here to allow delegation in `: ... by`

    override fun hashCode(): Int // Leave this here to allow delegation in `: ... by`

    override fun toString(): String // Leave this here to allow delegation in `: ... by`

    companion object {

        /** The character used to separate library alias from original name */
        const val ALIAS_SEPARATOR = "."

        private const val DEFAULT_ALIAS = "default"

        private val ALIAS_PATTERN = "^\\w+(\\${ALIAS_SEPARATOR}\\w+)*$".toRegex()

        internal fun equals(library: Library, other: Library): Boolean {
            if (library === other) return true

            if (library.alias != other.alias) return false
            if (library.operators != other.operators) return false
            if (library.theory != other.theory) return false
            if (library.primitives != other.primitives) return false
            if (library.functions != other.functions) return false

            return true
        }

        internal fun hashCode(library: Library): Int {
            var result = library.alias.hashCode()
            result = 31 * result + library.operators.hashCode()
            result = 31 * result + library.theory.hashCode()
            result = 31 * result + library.primitives.hashCode()
            result = 31 * result + library.functions.hashCode()
            return result
        }

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
