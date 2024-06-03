package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Termificator
import it.unibo.tuprolog.solve.libs.oop.ObjectDecorator
import it.unibo.tuprolog.solve.libs.oop.Objectifier
import it.unibo.tuprolog.solve.libs.oop.Result
import it.unibo.tuprolog.solve.libs.oop.TypeFactory

internal class ObjectDecoratorImpl(
    override val ref: ObjectRef,
    termificator: Termificator,
    objectifier: Objectifier,
    typeFactory: TypeFactory,
) : AbstractDecoratorImpl(ref, termificator, objectifier, typeFactory), ObjectRef by ref, ObjectDecorator {
    override fun invoke(
        method: String,
        arguments: List<Term>,
    ): Result = ref.value.invoke(method, arguments)

    override fun assign(
        property: String,
        value: Term,
    ): Boolean = ref.value.assign(property, value) is Result.Value
}
