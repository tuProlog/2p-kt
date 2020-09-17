package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term

interface NullRef : ObjectRef {
    override val `object`: Any
        get() = throw NullPointerException()

    override fun invoke(methodName: String, arguments: List<Term>): Result =
        throw NullPointerException()

    override fun assign(propertyName: String, value: Term): Boolean =
        throw NullPointerException()

    companion object {
        const val NULL_FUNCTOR = "null"
    }
}
