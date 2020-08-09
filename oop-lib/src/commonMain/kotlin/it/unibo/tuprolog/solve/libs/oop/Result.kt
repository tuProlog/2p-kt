package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.ToTermConvertible

sealed class Result {

    abstract fun toTerm(): Term?

    abstract fun asObjectRef(): ObjectRef?

    object None : Result() {
        override fun toTerm(): Term? = null

        override fun asObjectRef(): ObjectRef? = ObjectRef.NULL
    }

    data class Value(val value: Any?) : Result(), ToTermConvertible {

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

    }
}