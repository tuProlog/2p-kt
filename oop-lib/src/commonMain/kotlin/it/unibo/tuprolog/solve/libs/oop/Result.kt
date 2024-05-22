package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.NullRef
import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermConvertible
import it.unibo.tuprolog.core.Termificator

sealed class Result(open val termificator: Termificator?) {
    abstract fun toTerm(): Term?

    abstract fun asObjectRef(): ObjectRef?

    data object None : Result(null) {
        override fun toTerm(): Term? = null

        override fun asObjectRef(): NullRef = NullRef.instance

        override fun isNone(): Boolean = true

        override fun asNone(): None = this
    }

    data class Value(val value: Any?, override val termificator: Termificator) : Result(termificator), TermConvertible {
        private val termValue by lazy {
            termificator.termify(value)
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
