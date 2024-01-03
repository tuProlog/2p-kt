package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.impl.TermToObjectConverterImpl
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

interface TermToObjectConverter {
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
            dealiaser: (Struct) -> Ref? = { null },
        ): TermToObjectConverter = TermToObjectConverterImpl(typeFactory, dealiaser)

        @JvmStatic
        val default: TermToObjectConverter = of()
    }
}
