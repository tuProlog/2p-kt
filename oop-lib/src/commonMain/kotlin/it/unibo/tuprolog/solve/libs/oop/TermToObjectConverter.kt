package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.impl.TermToObjectConverterImpl
import kotlin.reflect.KClass

interface TermToObjectConverter {
    fun possibleConversions(term: Term): Sequence<Any?>

    fun admissibleTypes(term: Term): Set<KClass<*>>

    companion object {
        val default: TermToObjectConverter = TermToObjectConverterImpl()
    }
}