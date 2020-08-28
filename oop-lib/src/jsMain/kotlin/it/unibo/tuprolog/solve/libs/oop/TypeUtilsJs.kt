package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Optional
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty

private val TODO_EXCEPTION = NotImplementedError("OOP-Prolog integration is still not supported on JS")

actual val KClass<*>.companionObjectRef: Optional<out Any>
    get() = Optional.none()

actual val KClass<*>.companionObjectType: Optional<out KClass<*>>
    get() = Optional.none()

actual fun kClassFromName(qualifiedName: String): Optional<out KClass<*>> {
    val match = CLASS_NAME_PATTERN.matchEntire(qualifiedName)
    require(match != null) {
        "`$qualifiedName` should match ${CLASS_NAME_PATTERN.pattern}, while is doesn't"
    }
    @Suppress("UNUSED_VARIABLE")
    val module = match.groups[1]!!.value
    val packageSteps = match.groups[2]!!.value.split('.')
    var kClass: dynamic = js("require(module)")
    packageSteps.iterator().let {
        while (it.hasNext() && kClass != null && kClass != undefined) {
            kClass = kClass[it.next()]
        }
    }
    return if (kClass != null && kClass != undefined) {
        Optional.some(kClass.unsafeCast<JsClass<*>>().kotlin)
    } else {
        Optional.none()
    }
}

private val classNamePattern = "^($id):($id(\\.$id)*)$".toRegex()

actual val CLASS_NAME_PATTERN: Regex
    get() = classNamePattern

actual val KClass<*>.allSupertypes: Sequence<KClass<*>>
    get() = throw TODO_EXCEPTION

actual val KCallable<*>.formalParameterTypes: List<KClass<*>>
    get() = throw TODO_EXCEPTION

actual fun KClass<*>.findMethod(methodName: String, admissibleTypes: List<Set<KClass<*>>>): KCallable<*> =
    throw TODO_EXCEPTION

actual fun KClass<*>.findProperty(propertyName: String, admissibleTypes: Set<KClass<*>>): KMutableProperty<*> =
    throw TODO_EXCEPTION

actual fun KClass<*>.findConstructor(admissibleTypes: List<Set<KClass<*>>>): KCallable<*> =
    throw TODO_EXCEPTION

actual val KClass<*>.fullName: String
    get() = throw TODO_EXCEPTION

actual val KClass<*>.name: String
    get() = throw TODO_EXCEPTION

actual fun KClass<*>.invoke(
    methodName: String,
    arguments: List<Term>,
    instance: Any?
): Result {
    throw TODO_EXCEPTION
}

actual fun KClass<*>.create(
    arguments: List<Term>
): Result {
    throw TODO_EXCEPTION
}

actual fun KClass<*>.assign(
    propertyName: String,
    value: Term,
    instance: Any?
): Result {
    throw TODO_EXCEPTION
}