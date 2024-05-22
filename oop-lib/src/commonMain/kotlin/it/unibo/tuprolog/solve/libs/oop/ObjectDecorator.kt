package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Term

interface ObjectDecorator : OopLpBidirectionalBridge, ObjectRef {
    val ref: ObjectRef

    fun invoke(
        methodName: String,
        vararg arguments: Term,
    ): Result

    fun invoke(
        methodName: String,
        arguments: List<Term>,
    ): Result

    fun invoke(
        methodName: String,
        arguments: Iterable<Term>,
    ): Result

    fun invoke(
        methodName: String,
        arguments: Sequence<Term>,
    ): Result

    fun assign(
        propertyName: String,
        value: Term,
    ): Boolean
}
