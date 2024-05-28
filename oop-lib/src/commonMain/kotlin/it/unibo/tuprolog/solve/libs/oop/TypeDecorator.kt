package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Termificator
import it.unibo.tuprolog.solve.libs.oop.impl.ObjectDecoratorImpl
import it.unibo.tuprolog.solve.libs.oop.impl.TypeDecoratorImpl

interface TypeDecorator : OOPContext, TypeRef {
    fun create(vararg arguments: Term): Result = create(arguments.toList())

    fun create(arguments: List<Term>): Result

    fun create(arguments: Iterable<Term>): Result = create(arguments.toList())

    fun create(arguments: Sequence<Term>): Result = create(arguments.toList())

    companion object {
        fun of(
            value: TypeRef,
            termificator: Termificator = Termificator.default(),
            objectifier: Objectifier = Objectifier.default,
            typeFactory: TypeFactory = TypeFactory.default,
        ): TypeDecorator = TypeDecoratorImpl(value, termificator, objectifier, typeFactory)

        fun of(
            value: TypeRef,
            oopContext: OOPContext,
        ): TypeDecorator = of(value, oopContext.termificator, oopContext.objectifier, oopContext.typeFactory)
    }
}
