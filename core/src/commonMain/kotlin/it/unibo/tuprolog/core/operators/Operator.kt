package it.unibo.tuprolog.core.operators

import it.unibo.tuprolog.core.*

class Operator(val functor: String, val associativity: Associativity, val priority: Int) : Comparable<Operator> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Operator

        if (functor != other.functor) return false
        if (associativity != other.associativity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = functor.hashCode()
        result = 31 * result + associativity.hashCode()
        return result
    }

    override fun toString(): String {
        return "Operator($priority, $associativity, '$functor')"
    }

    override fun compareTo(other: Operator): Int =
            when {
                priority > other.priority -> 1
                priority < other.priority -> -1
                else -> with(associativity.compareTo(other.associativity)) {
                    when {
                        this != 0 -> this
                        else -> functor.compareTo(other.functor)
                    }
                }
            }

    fun toTerm(): Struct =
            Struct.of("op", priority.toTerm(), associativity.toTerm(), functor.toAtom())

    companion object {

        val TEMPLATE = structOf("op", varOf("P"), varOf("A"), varOf("F"))

        fun fromTerm(struct: Struct): Operator =
                with(struct) {
                    if (functor == "op" && arity == 3 && args[0] is Integer && args[1] is Atom && args[2] is Atom) {
                        Operator(
                                args[2].castTo<Atom>().value,
                                Associativity.fromTerm(args[1]),
                                args[0].castTo<Numeric>().intValue.toInt()
                        )
                    } else {
                        throw IllegalArgumentException("Term `$struct` cannot be interpreted as an operator")
                    }
                }
    }
}