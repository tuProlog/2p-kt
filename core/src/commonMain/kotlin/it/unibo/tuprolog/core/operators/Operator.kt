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
        fun fromTerm(struct: Struct): Operator? =
            with(struct) {
                when {
                    functor == FUNCTOR &&
                        arity == 3 &&
                        getArgAt(0).isInteger &&
                        getArgAt(1).isAtom &&
                        getArgAt(2).isAtom -> {
                        try {
                            Operator(
                                getArgAt(2).castToAtom().value,
                                Specifier.fromTerm(getArgAt(1)),
                                getArgAt(0).castToNumeric().intValue.toInt(),
                            )
                        } catch (ex: IllegalArgumentException) {
                            null
                        } catch (ex: IllegalStateException) {
                            // Enum.valueOf throws IllegalStateException instead of IllegalArgumentException
                            null
                        }
                    }
                    else -> null
                }
            }
    }
}
