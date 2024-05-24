package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Termificator
import it.unibo.tuprolog.solve.libs.oop.impl.ObjectDecoratorImpl

interface ObjectDecorator : OOPContext, ObjectRef {
    val ref: ObjectRef

    fun invoke(
        method: String,
        vararg arguments: Term,
    ): Result = invoke(method, arguments.toList())

    fun invoke(
        method: String,
        arguments: List<Term>,
    ): Result

    fun invoke(
        method: String,
        arguments: Iterable<Term>,
    ): Result = invoke(method, arguments.toList())

    fun invoke(
        method: String,
        arguments: Sequence<Term>,
    ): Result = invoke(method, arguments.toList())

    fun assign(
        property: String,
        value: Term,
    ): Boolean

    companion object {
        fun of(
            value: ObjectRef,
            termificator: Termificator = Termificator.default(),
            objectifier: Objectifier = Objectifier.default,
            typeFactory: TypeFactory = TypeFactory.default,
        ): ObjectDecorator = ObjectDecoratorImpl(value, termificator, objectifier, typeFactory)

        fun of(
            value: ObjectRef,
            oopContext: OOPContext,
        ) = of(value, oopContext.termificator, oopContext.objectifier, oopContext.typeFactory)
    }
}
