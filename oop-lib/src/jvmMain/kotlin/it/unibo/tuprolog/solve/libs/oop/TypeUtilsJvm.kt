package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Optional
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance

actual val KClass<*>.companionObjectRef: Optional<out Any>
    get() = Optional.of(companionObjectInstance)

actual val KClass<*>.companionObjectType: Optional<out KClass<*>>
    get() = Optional.of(companionObject)

actual fun kClassFromName(qualifiedName: String): Optional<out KClass<*>> {
    require(CLASS_NAME_PATTERN.matches(qualifiedName)) {
        "`$qualifiedName` must match ${CLASS_NAME_PATTERN.pattern} while it doesn't"
    }
    return try {
        Optional.of(Class.forName(qualifiedName).kotlin)
    } catch (e: ClassNotFoundException) {
        Optional.none()
    }
}

private val classNamePattern = "^$id(\\.$id)*$".toRegex()

actual val CLASS_NAME_PATTERN: Regex
    get() = classNamePattern

actual val KClass<*>.allSupertypes: Sequence<KClass<*>>
    get() = supertypes.asSequence()
        .map { it.classifier }
        .filterIsInstance<KClass<*>>()
        .flatMap { sequenceOf(it) + it.allSupertypes }
        .distinct()

actual val KCallable<*>.formalParameterTypes: List<KClass<*>>
    get() = parameters.filterNot { it.kind == KParameter.Kind.INSTANCE }.map {
        it.type.classifier as? KClass<*> ?: Any::class
    }

private fun List<KParameter>.match(types: List<Set<KClass<*>>>): Boolean {
    if (size != types.size) return false
    for (i in this.indices) {
        val possible = types[i]
        when (val formal = this[i].type.classifier) {
            is KClass<*> -> {
                if (possible.none { formal isSupertypeOf it }) return false
            }
            is KTypeParameter -> return true
            else -> return false
        }
    }
    return true
}

actual fun KClass<*>.findMethod(methodName: String, admissibleTypes: List<Set<KClass<*>>>): KCallable<*> =
    members.filter { it.name == methodName }
        .firstOrNull { method ->
            method.parameters.filterNot { it.kind == KParameter.Kind.INSTANCE }.match(admissibleTypes)
        } ?: throw MethodInvocationException(this, methodName, admissibleTypes)

actual fun KClass<*>.findProperty(propertyName: String, admissibleTypes: Set<KClass<*>>): KMutableProperty<*> =
    members.filter { it.name == propertyName }
        .filterIsInstance<KMutableProperty<*>>()
        .firstOrNull { property ->
            property.parameters.filterNot { it.kind == KParameter.Kind.INSTANCE }.match(listOf(admissibleTypes))
        } ?: throw PropertyAssignmentException(this, propertyName, admissibleTypes)

actual fun KClass<*>.findConstructor(admissibleTypes: List<Set<KClass<*>>>): KCallable<*> =
    constructors.firstOrNull { constructor ->
        constructor.parameters.filterNot { it.kind == KParameter.Kind.INSTANCE }.match(admissibleTypes)
    } ?: throw MethodInvocationException(this, admissibleTypes)

actual val KClass<*>.fullName: String
    get() = qualifiedName!!

actual val KClass<*>.name: String
    get() = simpleName!!

private fun KCallable<*>.ensureArgumentsListIsOfSize(actualArguments: List<Term>): List<KClass<*>> {
    return formalParameterTypes.also { formalArgumentsTypes ->
        require(formalParameterTypes.size == actualArguments.size) {
            """
            |
            |Error while invoking $name the expected argument types 
            |   ${formalArgumentsTypes.map { it.name }} 
            |are not as many as the as the actual parameters (${formalArgumentsTypes.size} vs. ${actualArguments.size}):
            |   $actualArguments
            |
            """.trimMargin()
        }
    }
}

actual fun KClass<*>.invoke(
    methodName: String,
    arguments: List<Term>,
    instance: Any?
): Result {
    val converter = TermToObjectConverter.default
    val methodRef = findMethod(methodName, arguments.map { converter.admissibleTypes(it) })
    return methodRef.callWithPrologArguments(converter, arguments, instance)
}

private fun KCallable<*>.callWithPrologArguments(
    converter: TermToObjectConverter,
    arguments: List<Term>,
    instance: Any? = null
): Result {
    val formalArgumentsTypes = ensureArgumentsListIsOfSize(arguments)
    val args = arguments.mapIndexed { i, it ->
        converter.convertInto(formalArgumentsTypes[i], it)
    }.toTypedArray()
    val result = if (instance == null) call(*args) else call(instance, *args)
    return Result.Value(result)
}

actual fun KClass<*>.assign(
    propertyName: String,
    value: Term,
    instance: Any?
): Result {
    val converter = TermToObjectConverter.default
    val setterRef = findProperty(propertyName, converter.admissibleTypes(value)).setter
    return setterRef.callWithPrologArguments(converter, listOf(value), instance)
}

actual fun KClass<*>.create(
    arguments: List<Term>
): Result {
    val converter = TermToObjectConverter.default
    val constructorRef = findConstructor(arguments.map { converter.admissibleTypes(it) })
    return constructorRef.callWithPrologArguments(converter, arguments)
}
