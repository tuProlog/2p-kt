@file:JvmName("TypeUtils")
package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Optional
import kotlin.jvm.JvmName
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

private val KClass<*>.allSupertypes: Sequence<KClass<*>>
    get() = supertypes.asSequence()
        .map { it.classifier }
        .filterIsInstance<KClass<*>>()
        .flatMap { sequenceOf(it) + it.allSupertypes }
        .distinct()

infix fun KClass<*>.isSupertypeOf(other: KClass<*>): Boolean =
    other.allSupertypes.any { it == this }

infix fun KClass<*>.isSubtypeOf(other: KClass<*>): Boolean =
    other isSupertypeOf this

val KCallable<*>.actualParameterTypes: List<KClass<*>>
    get() = parameters.filterNot { it.kind == KParameter.Kind.INSTANCE }.map { it.type.classifier as KClass<*> }

fun List<KParameter>.match(types: List<Set<KClass<*>>>): Boolean {
    if (size != types.size) return false
    for (i in this.indices) {
        val possible = types[i]
        when (val formal = this[i].type.classifier) {
            is KClass<*> -> {
                if (possible.none { formal isSupertypeOf it }) return false
            }
            else -> return false
        }
    }
    return true
}

fun KClass<*>.findMethod(methodName: String, admissibleTypes: List<Set<KClass<*>>>): KCallable<*> =
    members.filter { it.name == methodName }
        .firstOrNull { method ->
            method.parameters.filterNot { it.kind == KParameter.Kind.INSTANCE }.match(admissibleTypes)
        } ?: throw MethodInvocationException(this, methodName, admissibleTypes)

fun Any.invoke(methodName: String, arguments: List<Term>): Result =
    this::class.invoke(methodName, arguments, this)

fun KClass<*>.invoke(methodName: String, arguments: List<Term>, instance: Any? = null): Result {
    val converter = TermToObjectConverter.default
    val methodRef = findMethod(methodName, arguments.map { converter.admissibleTypes(it) })
    val argumentsExpectedTypes = methodRef.actualParameterTypes
    require(argumentsExpectedTypes.size == arguments.size) {
        """
            |
            |Error while invoking ${methodRef.name} the expected argument types 
            |   ${argumentsExpectedTypes.map { it.simpleName }} 
            |are not as many as the as the actual parameters (${argumentsExpectedTypes.size} vs. ${arguments.size}):
            |   $arguments
            |
            """.trimMargin()
    }
    val args = arguments.mapIndexed { i, it ->
        converter.convertInto(argumentsExpectedTypes[i], it)
    }.toTypedArray()
    val result = if (instance == null) methodRef.call(*args) else methodRef.call(instance, *args)
    return Result.Value(result)
}

internal const val id = "[a-zA-Z_][a-zA-Z0-9_]+"

expect val CLASS_NAME_PATTERN: Regex

expect val KClass<*>.companionObjectRef: Optional<out Any>

expect val KClass<*>.companionObjectType: Optional<out KClass<*>>

expect fun kClassFromName(qualifiedName: String): Optional<out KClass<*>>