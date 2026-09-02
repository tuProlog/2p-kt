package it.unibo.tuprolog.core.operators

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.TermConvertible
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.toAtom
import it.unibo.tuprolog.core.toTerm
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/** Class representing a logic operator */
class Operator(
    val functor: String,
    val specifier: Specifier,
    val priority: Int,
) : Comparable<Operator>,
    TermConvertible {
    override fun compareTo(other: Operator): Int =
        when {
            priority > other.priority -> 1
            priority < other.priority -> -1
            else ->
                specifier.compareTo(other.specifier).let { specifierCompareTo ->
                    when (specifierCompareTo) {
                        0 -> functor.compareTo(other.functor)
                        else -> specifierCompareTo
                    }
                }
        }

    override fun toTerm(): Struct = Struct.of(FUNCTOR, priority.toTerm(), specifier.toTerm(), functor.toAtom())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Operator

        if (functor != other.functor) return false
        if (specifier != other.specifier) return false

        return true
    }

    override fun hashCode(): Int {
        var result = functor.hashCode()
        result = 31 * result + specifier.hashCode()
        return result
    }

    override fun toString(): String = "Operator($priority, $specifier, '$functor')"

    companion object {
        /** The Operator functor */
        const val FUNCTOR = "op"

        /** An operator template */
        @JvmField
        val TEMPLATE = Struct.of(FUNCTOR, Var.of("P"), Var.of("A"), Var.of("F"))

        @JvmStatic
        @JsName("fromTerms")
        fun fromTerms(
            priority: Integer,
            specifier: Atom,
            functor: Atom,
        ): Operator? = fromTerm(Struct.of(FUNCTOR, priority, specifier, functor))

        /** Creates an Operator instance from a well-formed Struct, or returns `null` if it cannot be interpreted as Operator */
        @JvmStatic
        @JsName("fromTerm")
        fun fromTerm(struct: Struct): Operator? = manyFromTerm(struct).singleOrNull()

        /** Creates an Operator instance from a well-formed Struct, or returns `null` if it cannot be interpreted as Operator */
        @JvmStatic
        @JsName("manyFromTerm")
        fun manyFromTerm(struct: Struct): Iterable<Operator> =
            with(struct) {
                if (functor == FUNCTOR && arity == 3) {
                    val arg1 = getArgAt(0)
                    val arg2 = getArgAt(1)
                    val arg3 = getArgAt(2)
                    if (arg1.isInteger && arg2.isAtom) {
                        val priority = arg1.castToNumeric().intValue.toInt()
                        val specifier = runCatching { Specifier.fromTerm(arg2) }.getOrNull()
                        if (specifier == null) {
                            return emptyList()
                        }
                        if (arg3.isAtom) {
                            return listOf(Operator(arg3.castToAtom().value, specifier, priority))
                        }
                        if (arg3.isList) {
                            val functors = arg3.castToList().items.toList()
                            if (functors.any { !it.isAtom }) {
                                return emptyList()
                            }
                            return functors.map { Operator(it.castToAtom().value, specifier, priority) }
                        }
                    }
                }
                return emptyList()
            }
    }
}
