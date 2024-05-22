package it.unibo.tuprolog.utils

import kotlin.reflect.KClass

@Suppress("UnsafeCastFromDynamic")
actual fun <T> Any?.forceCast(): T {
    val it: dynamic = this
    return it
}

private val objectify: (dynamic) -> dynamic by lazy { js("global.Object") }

actual fun box(value: Any): Any = objectify(value) as Any

private val objectsIds = js("new WeakMap()")
private var lastId = 0

actual val Any.identifier: String
    get() {
        val obj = box(this)
        return if (objectsIds.has(obj) as Boolean) {
            objectsIds.get(obj)
        } else {
            val id = lastId++
            objectsIds.set(obj, id)
            id
        }.toString(HEX_RADIX) as String
    }

actual val KClass<*>.fullName: String
    get() = error("Cannot reflect full name of class $name on JS")

actual val KClass<*>.name: String
    get() = simpleName ?: toString().split(' ')[1]

actual val KClass<*>.safeName: String
    get() = name
