package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

@Suppress("EqualsOrHashCode")
internal class RealImpl(
    override val value: BigDecimal,
    tags: Map<String, Any> = emptyMap(),
) : NumericImpl(tags),
    Real {
    override val decimalValue: BigDecimal
        get() = value

    override val intValue: BigInteger by lazy { value.toBigInteger() }

    override fun toString(): String = Real.toStringEnsuringDecimal(value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val real = asTerm(other)?.asReal()
        if (real === null) return false
        return equalsToReal(real)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun equalsToReal(other: Real) = value.compareTo(other.value) == 0

    override fun equals(
        other: Term,
        useVarCompleteName: Boolean,
    ): Boolean = other.isReal && equalsToReal(other.castToReal())

    override val hashCodeCache: Int by lazy { value.stripTrailingZeros().hashCode() }

    override fun copyWithTags(tags: Map<String, Any>): Real = RealImpl(value, tags)

    override fun freshCopy(): Real = this

    override fun freshCopy(scope: Scope): Real = this

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitReal(this)
}
