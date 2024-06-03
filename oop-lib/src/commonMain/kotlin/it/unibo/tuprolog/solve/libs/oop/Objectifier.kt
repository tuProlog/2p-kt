package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.impl.ObjectifierImpl
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

interface Objectifier {
    val typeFactory: TypeFactory

    fun convertInto(
        type: KClass<*>,
        term: Term,
    ): Any?

    fun possibleConversions(term: Term): Sequence<Any?>

    fun admissibleTypes(term: Term): Set<KClass<*>>

    fun priorityOfConversion(
        type: KClass<*>,
        term: Term,
    ): Int?

    fun mostAdequateType(term: Term): KClass<*>

    fun convert(term: Term): Any? = convertInto(mostAdequateType(term), term)

    companion object {
        @JvmStatic
        fun of(
            typeFactory: TypeFactory = TypeFactory.default,
            dealiaser: (Struct) -> ObjectRef? = { null },
        ): Objectifier = ObjectifierImpl(typeFactory, dealiaser)

        @JvmStatic
        val default: Objectifier = of()
    }
}
