package it.unibo.tuprolog.core.operators

import it.unibo.tuprolog.core.*
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/** Class representing a Prolog Operator */
class Operator(val functor: String, val specifier: Specifier, val priority: Int) :
    Comparable<Operator>, ToTermConvertible {

    override fun compareTo(other: Operator): Int =
        when {
            priority > other.priority -> 1
            priority < other.priority -> -1
            else -> specifier.compareTo(other.specifier).let { specifierCompareTo ->
                when (specifierCompareTo) {
                    0 -> functor.compareTo(other.functor)
                    else -> specifierCompareTo
                }
            }
        }

    override fun toTerm(): Struct =
        Struct.of(FUNCTOR, priority.toTerm(), specifier.toTerm(), functor.toAtom())

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

        /** Creates an Operator instance from a well-formed Struct, or returns `null` if it cannot be interpreted as Operator */
        @JvmStatic
        fun fromTerm(struct: Struct): Operator? = with(struct) {
            when {
                functor == FUNCTOR && arity == 3 &&
                        args[0] is Integer && args[1] is Atom && args[2] is Atom -> try {

                    Operator(
                        args[2].`as`<Atom>().value,
                        Specifier.fromTerm(args[1]),
                        args[0].`as`<Numeric>().intValue.toInt()
                    )

                } catch (ex: IllegalArgumentException) {
                    null
                } catch (ex: IllegalStateException) { // Enum.valueOf throws IllegalStateException instead of IllegalArgumentException
                    null
                }

                else -> null
            }
        }
    }
}
