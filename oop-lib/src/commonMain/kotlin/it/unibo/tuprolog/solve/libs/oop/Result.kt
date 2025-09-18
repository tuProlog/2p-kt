package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermConvertible

sealed class Result {
    abstract fun toTerm(): Term?

    abstract fun asObjectRef(): ObjectRef?

    object None : Result() {
        override fun toTerm(): Term? = null

        override fun asObjectRef(): ObjectRef? = null

        override fun isNone(): Boolean = true

        override fun asNone(): None = this
    }

    data class Value(
        val value: Any?,
    ) : Result(),
        TermConvertible {
        private val termValue by lazy {
            ObjectToTermConverter.default.convert(value)
        }

        private val objectRef: ObjectRef by lazy {
            termValue.let {
                if (it is ObjectRef) it else ObjectRef.of(value)
            }
        }

        override fun toTerm(): Term = termValue

        override fun asObjectRef(): ObjectRef = objectRef

        override fun isValue(): Boolean = true

        override fun asValue(): Value = this
    }

    open fun isNone(): Boolean = false

    open fun isValue(): Boolean = false

    open fun asNone(): None? = null

    open fun asValue(): Value? = null
}
