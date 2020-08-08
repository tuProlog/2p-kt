package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.utils.Optional
import kotlin.reflect.KClass

actual val KClass<*>.companionObjectRef: Optional<out Any>
    get() = Optional.none()

actual val KClass<*>.companionObjectType: Optional<out KClass<*>>
    get() = Optional.none()

actual fun kClassFromName(qualifiedName: String): Optional<out KClass<*>> {
    val match = CLASS_NAME_PATTERN.matchEntire(qualifiedName)
    require(match != null) {
        "`$qualifiedName` should match ${CLASS_NAME_PATTERN.pattern}, while is doesn't"
    }
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