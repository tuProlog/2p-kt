package it.unibo.tuprolog.core.operators

import kotlin.js.JsName
import kotlin.jvm.JvmField

/** Class representing a [Set] of [Operator]s */
class OperatorSet(
    operators: Sequence<Operator>,
) : Set<Operator> by operators.toHashSet() {
    /** Needed to support equals and hashCode */
    private val operators by lazy { operators.toSet() }

    constructor(vararg operator: Operator) : this(operator.asSequence())

    constructor(operators: Iterable<Operator>) : this(operators.asSequence())

    /** Creates a new OperatorSet adding to this the other Operator, overriding already present operator if one */
    @JsName("plus")
    operator fun plus(operator: Operator): OperatorSet = OperatorSet(sequenceOf(operator, *this.toTypedArray()))

    /** Creates a new OperatorSet adding to this the other operators, overriding already present operators */
    @JsName("plusOperatorSet")
    operator fun plus(other: OperatorSet): OperatorSet = OperatorSet(other.asSequence() + this.asSequence())

    /** Creates a new OperatorSet removing from this the other operators */
    @JsName("minus")
    operator fun minus(operator: Operator): OperatorSet {
        val thiz: Set<Operator> = this
        return OperatorSet(thiz - operator)
    }

    /** Creates a new OperatorSet removing from this the other operators */
    @JsName("minusOperatorSet")
    operator fun minus(other: OperatorSet): OperatorSet {
        val thiz: Set<Operator> = this
        val otherSet: Set<Operator> = other
        return OperatorSet(thiz - otherSet)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as OperatorSet

        if (operators != other.operators) return false

        return true
    }

    override fun hashCode(): Int = operators.hashCode()

    override fun toString(): String = "OperatorSet($operators)"

    companion object {
        @JvmField
        @JsName("EMPTY")
        val EMPTY = OperatorSet(emptySequence())

        /** Arithmetic Operator's OperatorSet */
        @JvmField
        @JsName("ARITHMETIC")
        val ARITHMETIC =
            OperatorSet(
                sequenceOf("+", "-", "\\").map { Operator(it, Specifier.FY, 200) } +
                    sequenceOf("^").map { Operator(it, Specifier.XFY, 200) } +
                    sequenceOf("**").map { Operator(it, Specifier.XFX, 200) } +
                    sequenceOf("*", "/", "//", "rem", "mod", "<<", ">>").map { Operator(it, Specifier.YFX, 400) } +
                    sequenceOf("+", "-", "\\/", "/\\").map { Operator(it, Specifier.YFX, 500) },
            )

        /** Arithmetic Comparison Operator's OperatorSet */
        @JvmField
        @JsName("ARITHMETIC_COMPARISON")
        val ARITHMETIC_COMPARISON =
            OperatorSet(
                sequenceOf("=:=", "=\\=", "<", "=<", ">", ">=")
                    .map { Operator(it, Specifier.XFX, 700) },
            )

        /** Term Comparison Operator's OperatorSet */
        @JvmField
        @JsName("TERM_COMPARISON")
        val TERM_COMPARISON =
            OperatorSet(
                sequenceOf("=", "\\=").map { Operator(it, Specifier.XFX, 700) } +
                    sequenceOf("==", "\\==", "@<", "@=<", "@>", "@>=").map { Operator(it, Specifier.XFX, 700) } +
                    sequenceOf("=..").map { Operator(it, Specifier.XFX, 700) } +
                    sequenceOf("is").map { Operator(it, Specifier.XFX, 700) },
            )

        /** Control Flow Operator's OperatorSet */
        @JvmField
        @JsName("CONTROL_FLOW")
        val CONTROL_FLOW =
            OperatorSet(
                sequenceOf(",").map { Operator(it, Specifier.XFY, 1000) } +
                    sequenceOf("->").map { Operator(it, Specifier.XFY, 1050) } +
                    sequenceOf(";").map { Operator(it, Specifier.XFY, 1100) } +
                    sequenceOf("\\+").map { Operator(it, Specifier.FY, 900) },
            )

        /** Clauses Operator's OperatorSet */
        @JvmField
        @JsName("CLAUSES")
        val CLAUSES =
            OperatorSet(
                sequenceOf(":-", "?-").map { Operator(it, Specifier.FX, 1200) } +
                    sequenceOf(":-", "-->").map { Operator(it, Specifier.XFX, 1200) },
            )

        /** Standard OperatorSet */
        @JvmField
        @JsName("STANDARD")
        val STANDARD =
            OperatorSet(
                ARITHMETIC.asSequence() +
                    ARITHMETIC_COMPARISON.asSequence() +
                    TERM_COMPARISON.asSequence() +
                    CONTROL_FLOW.asSequence() +
                    CLAUSES.asSequence(),
            )

        /** Default OperatorSet */
        @JvmField
        @JsName("DEFAULT")
        val DEFAULT = STANDARD
    }
}
