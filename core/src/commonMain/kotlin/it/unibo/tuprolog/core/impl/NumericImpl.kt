package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

@Suppress("EqualsOrHashCode")
internal abstract class NumericImpl(tags: Map<String, Any>) : TermImpl(tags), Numeric {

    override fun structurallyEquals(other: Term): Boolean =
        other is NumericImpl && decimalValue.compareTo(other.decimalValue) == 0

    abstract override val decimalValue: BigDecimal

    abstract override val intValue: BigInteger

    abstract override fun equals(other: Any?): Boolean

    abstract override fun equals(other: Term, useVarCompleteName: Boolean): Boolean

    abstract override fun copyWithTags(tags: Map<String, Any>): Numeric

    abstract override fun freshCopy(): Numeric

    abstract override fun freshCopy(scope: Scope): Numeric
}
