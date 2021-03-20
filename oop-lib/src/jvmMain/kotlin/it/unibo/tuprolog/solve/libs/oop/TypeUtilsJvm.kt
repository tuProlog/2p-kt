package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.libs.oop.exceptions.OopRuntimeException
import it.unibo.tuprolog.solve.libs.oop.exceptions.RuntimePermissionException
import it.unibo.tuprolog.utils.Optional
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.IllegalCallableAccessException
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
    val kotlinKlass = KotlinToJavaTypeMap[qualifiedName]
    return if (kotlinKlass != null) {
        Optional.of(kotlinKlass)
    } else try {
        Optional.of(Class.forName(qualifiedName).kotlin)
    } catch (e: ClassNotFoundException) {
        Optional.none()
    }
}

private val classNamePattern = "^$id(\\.$id)*$".toRegex()

actual val CLASS_NAME_PATTERN: Regex
    get() = classNamePattern

actual val Any.identifier: String
    get() = System.identityHashCode(this).toString(16)

internal actual fun <T> KCallable<*>.catchingPlatformSpecificException(
    instance: Any?,
    action: () -> T
): T = try {
    action()
} catch (e: IllegalCallableAccessException) {
    throw RuntimePermissionException(this, instance, e)
} catch (e: InvocationTargetException) {
    throw OopRuntimeException(this, instance, e.cause ?: e)
}
