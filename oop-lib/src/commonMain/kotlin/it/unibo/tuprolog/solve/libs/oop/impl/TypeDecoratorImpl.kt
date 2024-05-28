package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Termificator
import it.unibo.tuprolog.solve.libs.oop.Objectifier
import it.unibo.tuprolog.solve.libs.oop.Result
import it.unibo.tuprolog.solve.libs.oop.TypeDecorator
import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.solve.libs.oop.TypeRef

internal class TypeDecoratorImpl(
    override val ref: TypeRef,
    override val termificator: Termificator,
    override val objectifier: Objectifier,
    override val typeFactory: TypeFactory,
) : AbstractDecoratorImpl(ref, termificator, objectifier, typeFactory), TypeRef by ref, TypeDecorator {
    override fun create(arguments: List<Term>): Result = ref.value.create(arguments)
}
