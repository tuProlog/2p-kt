package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.Objectifier
import it.unibo.tuprolog.solve.libs.oop.OverloadSelector
import it.unibo.tuprolog.solve.libs.oop.allSupertypes
import it.unibo.tuprolog.solve.libs.oop.exceptions.ConstructorInvocationException
import it.unibo.tuprolog.solve.libs.oop.exceptions.MethodInvocationException
import it.unibo.tuprolog.solve.libs.oop.exceptions.PropertyAssignmentException
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVisibility

internal class OverloadSelectorImpl(
    override val type: KClass<*>,
    override val objectifier: Objectifier,
) : OverloadSelector {
    @Suppress("SwallowedException")
    override fun findMethod(
        name: String,
        arguments: List<Term>,
    ): KCallable<*> {
        KotlinTypesHacks[type, name, arguments]?.let { return it }
        return try {
            type.members
                .filter { it.name == name }
                .filter { it.visibility == KVisibility.PUBLIC }
                .map { it to it.instanceParameters.score(arguments) }
                .minByOrNull { (_, score) -> score ?: Int.MAX_VALUE }
                ?.first
                ?: throw MethodInvocationException(
                    type, name,
                    arguments.map {
                        objectifier.admissibleTypes(it)
                    },
                )
        } catch (e: IllegalStateException) {
            type.allSupertypes(strict = true)
                .firstOrNull()
                ?.let { OverloadSelector.of(it, objectifier) }
                ?.findMethod(name, arguments)
                ?: throw MethodInvocationException(
                    type, name,
                    arguments.map {
                        objectifier.admissibleTypes(it)
                    },
                )
        }
    }

    override fun findProperty(
        name: String,
        value: Term,
    ): KMutableProperty<*> {
        return type.members
            .filter { it.name == name }
            .filter { it.visibility == KVisibility.PUBLIC }
            .filterIsInstance<KMutableProperty<*>>()
            .map { it to it.instanceParameters.score(listOf(value)) }
            .minByOrNull { (_, score) -> score ?: Int.MAX_VALUE }
            ?.first
            ?: throw PropertyAssignmentException(type, name, objectifier.admissibleTypes(value))
    }

    private val KCallable<*>.instanceParameters
        get() = parameters.filterNot { it.kind == KParameter.Kind.INSTANCE }

    override fun findConstructor(arguments: List<Term>): KCallable<*> {
        return type.constructors
            .filter { it.visibility == KVisibility.PUBLIC }
            .map { it to it.instanceParameters.score(arguments) }
            .minByOrNull { (_, score) -> score ?: Int.MAX_VALUE }
            ?.first
            ?: throw ConstructorInvocationException(type, arguments.map { objectifier.admissibleTypes(it) })
    }

    @Suppress("ReturnCount")
    private fun List<KParameter>.score(arguments: List<Term>): Int? {
        if (size != arguments.size) return null
        var score = 0
        for (i in this.indices) {
            when (val formal = this[i].type.classifier) {
                is KClass<*> -> {
                    score += objectifier.priorityOfConversion(formal, arguments[i]) ?: return null
                }
                is KTypeParameter -> {
                    score += objectifier.admissibleTypes(arguments[i]).count()
                }
                else -> return null
            }
        }
        return score
    }
}
