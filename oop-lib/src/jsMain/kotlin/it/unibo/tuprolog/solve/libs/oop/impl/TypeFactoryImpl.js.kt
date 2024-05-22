package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.solve.libs.oop.ID
import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.utils.Optional
import kotlin.reflect.KClass

internal actual class TypeFactoryImpl : TypeFactory {
    companion object {
        val CLASS_NAME_PATTERN: Regex = "^($ID):($ID(\\.$ID)*)$".toRegex()
    }

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

    actual override fun typeFromName(typeName: String): KClass<*>? = kClassFromName(typeName).value
}
