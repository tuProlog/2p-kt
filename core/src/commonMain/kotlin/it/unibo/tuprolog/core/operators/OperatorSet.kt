package it.unibo.tuprolog.core.operators

/** Class representing a Set of [Operator]s */
class OperatorSet(operators: Sequence<Operator>) : Set<Operator> by operators.toHashSet() {

    /** Needed to support equals and hashCode */
    private val operators by lazy { operators.toSet() }

    constructor(vararg operator: Operator) : this(operator.asSequence())

    constructor(operators: Iterable<Operator>) : this(operators.asSequence())

    /** Creates a new OperatorSet adding to this the other Operator, overriding already present operator if one */
    operator fun plus(operator: Operator): OperatorSet =
            OperatorSet(sequenceOf(operator, *this.toTypedArray()))

    /** Creates a new OperatorSet adding to this the other operators, overriding already present operators */
    operator fun plus(other: OperatorSet): OperatorSet =
            OperatorSet(other.asSequence() + this.asSequence())

    /** Creates a new OperatorSet removing from this the other operators */
    operator fun minus(operator: Operator): OperatorSet =
            OperatorSet(this as Set<Operator> - operator)

    /** Creates a new OperatorSet removing from this the other operators */
    operator fun minus(other: OperatorSet): OperatorSet =
            OperatorSet(((this as Set<Operator>) - (other as Set<Operator>)))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as OperatorSet

        if (operators != other.operators) return false

        return true
    }

    override fun hashCode(): Int {
        return operators.hashCode()
    }
}