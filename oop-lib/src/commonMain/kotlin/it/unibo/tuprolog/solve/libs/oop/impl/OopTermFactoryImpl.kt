package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.TermFactory
import it.unibo.tuprolog.solve.libs.oop.OopTermFactory
import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.libs.oop.exceptions.NoSuchTypeException
import kotlin.reflect.KClass

class OopTermFactoryImpl(
    override val typeFactory: TypeFactory,
    private val termFactory: TermFactory,
) : OopTermFactory, TermFactory by termFactory {
    override fun typeRef(name: String): TypeRef =
        typeFactory.typeFromName(name)?.let { typeRef(name) } ?: throw NoSuchTypeException(name)

    override fun typeRef(value: KClass<*>): TypeRef = TypeRefImpl(value)

    override fun objectRef(value: Any?): ObjectRef =
        if (value is KClass<*>) {
            typeRef(value)
        } else {
            termFactory.objectRef(value)
        }
}
