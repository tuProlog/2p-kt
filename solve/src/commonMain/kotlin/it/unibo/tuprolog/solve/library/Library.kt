package it.unibo.tuprolog.solve.library

import it.unibo.tuprolog.core.Clause
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
interface Library : Pluggable {

    /** The alias identifying this library */
    @JsName("alias")
    val alias: String

    override fun equals(other: Any?): Boolean // Leave this here to allow delegation in `: ... by`

    override fun hashCode(): Int // Leave this here to allow delegation in `: ... by`

    override fun toString(): String // Leave this here to allow delegation in `: ... by`

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
            clauses: Iterable<Clause> = emptyList(),
            operators: OperatorSet = OperatorSet(),
            functions: Map<Signature, LogicFunction> = emptyMap()
        ): Library {
            require(ALIAS_PATTERN.matches(alias)) {
                "Aliases should match the pattern $ALIAS_PATTERN"
            }
            return LibraryImpl(alias, operators, clauses.toList(), primitives, functions)
        }

        @JvmStatic
        @JsName("unaliased")
        @JvmOverloads
        fun of(
            primitives: Map<Signature, Primitive> = emptyMap(),
            clauses: Iterable<Clause> = emptyList(),
            operators: OperatorSet = OperatorSet(),
            functions: Map<Signature, LogicFunction> = emptyMap()
        ): Library = of(DEFAULT_ALIAS, primitives, clauses, operators, functions)

        @JvmStatic
        @JsName("changingAlias")
        @JvmOverloads
        fun of(
            alias: String = DEFAULT_ALIAS,
            library: Library
        ): Library = of(alias, library.primitives, library.clauses, library.operators, library.functions)
    }
}
