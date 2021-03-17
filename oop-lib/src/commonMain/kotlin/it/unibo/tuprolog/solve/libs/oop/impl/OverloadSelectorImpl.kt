package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.OverloadSelector
import it.unibo.tuprolog.solve.libs.oop.TermToObjectConverter
import it.unibo.tuprolog.solve.libs.oop.allSupertypes
import it.unibo.tuprolog.solve.libs.oop.exceptions.ConstructorInvocationException
import it.unibo.tuprolog.solve.libs.oop.exceptions.MethodInvocationException
import it.unibo.tuprolog.solve.libs.oop.exceptions.PropertyAssignmentException
import it.unibo.tuprolog.solve.libs.oop.isSupertypeOf
import it.unibo.tuprolog.utils.indexed
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVisibility

internal class OverloadSelectorImpl(
    override val type: KClass<*>,
    override val termToObjectConverter: TermToObjectConverter
) : OverloadSelector {

    override fun findMethod(name: String, arguments: List<Term>): KCallable<*> {
        val admissibleTypes = arguments.map { termToObjectConverter.admissibleTypes(it) }
        return try {
            type.members
                .filter { it.name == name }
                .filter { it.visibility == KVisibility.PUBLIC }
                .map { it to it.instanceParameters.score(admissibleTypes) }
                .minByOrNull { (_, score) -> score ?: Int.MAX_VALUE }
                ?.first
                ?: throw MethodInvocationException(type, name, admissibleTypes)
        } catch (e: IllegalStateException) {
            type.allSupertypes(strict = true).firstOrNull()?.let {
                OverloadSelector.of(it, termToObjectConverter)
            }?.findMethod(name, arguments) ?: throw MethodInvocationException(type, name, admissibleTypes)
        }
    }

    override fun findProperty(name: String, value: Term): KMutableProperty<*> {
        val admissibleTypes = termToObjectConverter.admissibleTypes(value)
        return type.members
            .filter { it.name == name }
            .filter { it.visibility == KVisibility.PUBLIC }
            .filterIsInstance<KMutableProperty<*>>()
            .map { it to it.instanceParameters.score(listOf(admissibleTypes)) }
            .minByOrNull { (_, score) -> score ?: Int.MAX_VALUE }
            ?.first
            ?: throw PropertyAssignmentException(type, name, admissibleTypes)
    }

    private val KCallable<*>.instanceParameters
        get() = parameters.filterNot { it.kind == KParameter.Kind.INSTANCE }

    override fun findConstructor(arguments: List<Term>): KCallable<*> {
        val admissibleTypes = arguments.map { termToObjectConverter.admissibleTypes(it) }
        return type.constructors
            .filter { it.visibility == KVisibility.PUBLIC }
            .map { it to it.instanceParameters.score(admissibleTypes) }
            .minByOrNull { (_, score) -> score ?: Int.MAX_VALUE }
            ?.first
            ?: throw ConstructorInvocationException(type, admissibleTypes)
    }

    private fun List<KParameter>.score(types: List<Set<KClass<*>>>): Int? {
        if (size != types.size) return null
        var score = 0
        for (i in this.indices) {
            val possible = types[i].asSequence().indexed()
            when (val formal = this[i].type.classifier) {
                is KClass<*> -> {
                    score += possible.firstOrNull { (_, it) -> formal isSupertypeOf it }?.index ?: return null
                }
                is KTypeParameter -> score += possible.count()
                else -> return null
            }
        }
        return score
    }
}
