package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term

interface NullRef : ObjectRef {
    override val `object`: Any
        get() = throw NullPointerException()

    override fun invoke(
        objectConverter: TermToObjectConverter,
        methodName: String,
        arguments: List<Term>,
    ): Result = throw NullPointerException()

    override fun assign(
        objectConverter: TermToObjectConverter,
        propertyName: String,
        value: Term,
    ): Boolean = throw NullPointerException()
}
