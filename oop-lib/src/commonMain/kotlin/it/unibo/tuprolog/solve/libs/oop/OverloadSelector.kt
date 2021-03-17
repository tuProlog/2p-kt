package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.impl.OverloadSelectorImpl
import kotlin.jvm.JvmStatic
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty

interface OverloadSelector {
    val type: KClass<*>

    val termToObjectConverter: TermToObjectConverter

    fun findMethod(name: String, arguments: List<Term>): KCallable<*>

    fun findProperty(name: String, value: Term): KMutableProperty<*>

    fun findConstructor(arguments: List<Term>): KCallable<*>

    companion object {
        @JvmStatic
        fun of(
            type: KClass<*>,
            termToObjectConverter: TermToObjectConverter = TermToObjectConverter.default
        ): OverloadSelector = OverloadSelectorImpl(type, termToObjectConverter)
    }
}
