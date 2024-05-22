package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmStatic
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty

interface OverloadSelector {
    val type: KClass<*>

    val objectifier: Objectifier

    fun findMethod(
        name: String,
        arguments: List<Term>,
    ): KCallable<*>

    fun findProperty(
        name: String,
        value: Term,
    ): KMutableProperty<*>

    fun findConstructor(arguments: List<Term>): KCallable<*>

    companion object {
        @JvmStatic
        fun of(
            type: KClass<*>,
            objectifier: Objectifier = Objectifier.default,
        ): OverloadSelector = overloadSelector(type, objectifier)
    }
}
