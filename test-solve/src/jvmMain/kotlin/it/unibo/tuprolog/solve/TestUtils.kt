package it.unibo.tuprolog.solve

import kotlin.reflect.KClass
import kotlin.test.assertEquals

actual fun internalsOf(x: () -> Any): String = x().toString()

actual fun log(x: () -> Any) {
    System.err.println(x())
}

actual fun <T : Any> assertClassNameIs(
    klass: KClass<T>,
    name: String,
) {
    assertEquals(name, klass.qualifiedName)
}
