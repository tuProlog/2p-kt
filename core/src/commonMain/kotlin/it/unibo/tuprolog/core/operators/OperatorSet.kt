package it.unibo.tuprolog.core.operators

class OperatorSet(operators: Sequence<Operator>) : Set<Operator> by operators.toHashSet() {

    constructor(vararg operator: Operator) : this(operator.asSequence())

    constructor(operators: Iterable<Operator>) : this(operators.asSequence())

    operator fun plus(other: OperatorSet): OperatorSet =
            OperatorSet(this.asSequence() + other.asSequence())

    operator fun minus(other: OperatorSet): OperatorSet =
            OperatorSet(((this as Set<Operator>) - (other as Set<Operator>)))
}