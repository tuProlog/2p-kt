package it.unibo.tuprolog.solve

import kotlin.reflect.KClass
import kotlin.test.assertEquals

actual fun internalsOf(x: () -> Any): String {
    return JSON.stringify(x())
}

actual fun log(x: () -> Any) {
    console.log(x())
}

actual fun <T : Any> assertClassNameIs(
    `class`: KClass<T>,
    name: String,
) {
    assertEquals(`class`.simpleName, name.split('.').last())
}
