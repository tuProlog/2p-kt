package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.NullRef
import it.unibo.tuprolog.core.Ref
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermConvertible

/**
 * The outcome of reflectively invoking a member via [invoke], [assign], or [create]:
 * either [None] -- there was no return value, e.g. a `void`/`Unit`-returning method -- or a
 * [Value] wrapping whatever was returned (including `null`).
 *
 * This distinction exists because `invoke_method/3`
 * ([it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod]) needs to unify its result argument
 * with the returned [Term] only when one is actually available: unifying against "no value" would
 * make invoking a `void` method deterministically fail rather than simply succeed without binding
 * anything.
 *
 * @see it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod
 * @see it.unibo.tuprolog.solve.libs.oop.primitives.InvokeStrict
 */
sealed class Result {
    /** This result's value as a [Term] (via [ObjectToTermConverter.default]), or `null` if [isNone]. */
    abstract fun toTerm(): Term?

    /**
     * This result's value wrapped into a [Ref] (even if it converts to a non-reference [Term]): a [NullRef] for
     * `null`; `null` if [isNone]. Logic terms cannot be wrapped: their reference is the term itself, see [toTerm].
     */
    abstract fun asRef(): Ref?

    /** The result of invoking a member that returns no value. */
    object None : Result() {
        override fun toTerm(): Term? = null

        override fun asRef(): Ref? = null

        override fun isNone(): Boolean = true

        override fun asNone(): None = this
    }

    /** The result of invoking a member that returned [value] (`null` included). */
    data class Value(
        val value: Any?,
    ) : Result(),
        TermConvertible {
        private val termValue by lazy {
            ObjectToTermConverter.default.convert(value)
        }

        private val ref: Ref by lazy {
            termValue.let {
                if (it is Ref) it else Ref.of(value)
            }
        }

        override fun toTerm(): Term = termValue

        override fun asRef(): Ref = ref

        override fun isValue(): Boolean = true

        override fun asValue(): Value = this
    }

    /** Whether this is [None]. */
    open fun isNone(): Boolean = false

    /** Whether this is a [Value]. */
    open fun isValue(): Boolean = false

    /** This, cast to [None], or `null` if this is not [None]. */
    open fun asNone(): None? = null

    /** This, cast to [Value], or `null` if this is not a [Value]. */
    open fun asValue(): Value? = null
}
