package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.utils.Optional
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
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

private val classNamePattern = "^($ID):($ID(\\.$ID)*)$".toRegex()

actual val CLASS_NAME_PATTERN: Regex
    get() = classNamePattern

internal actual fun <T> KCallable<*>.catchingPlatformSpecificException(
    instance: Any?,
    action: () -> T,
): T = action()

actual fun KClass<*>.allSupertypes(strict: Boolean): Sequence<KClass<*>> = throw TODO_EXCEPTION

actual val KCallable<*>.formalParameterTypes: List<KClass<*>>
    get() = throw TODO_EXCEPTION

actual fun KCallable<*>.pretty(): String = throw TODO_EXCEPTION

actual fun <T> KCallable<T>.invoke(
    instance: Any?,
    vararg args: Any?,
): T = throw TODO_EXCEPTION

actual val <T> KMutableProperty<T>.setterMethod: KFunction<Unit>
    get() = throw TODO_EXCEPTION

actual fun overloadSelector(
    type: KClass<*>,
    termToObjectConverter: TermToObjectConverter,
): OverloadSelector = throw TODO_EXCEPTION
