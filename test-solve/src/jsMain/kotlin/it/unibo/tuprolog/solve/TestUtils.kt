package it.unibo.tuprolog.solve

import kotlin.reflect.KClass
import kotlin.test.assertEquals

actual fun internalsOf(x: () -> Any): String = JSON.stringify(x())

actual fun log(x: () -> Any) {
    console.log(x())
}

actual fun <T : Any> assertClassNameIs(
    klass: KClass<T>,
    name: String,
) {
    assertEquals(klass.simpleName, name.split('.').last())
}
