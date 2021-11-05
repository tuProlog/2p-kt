package it.unibo.tuprolog.solve

import kotlin.reflect.KClass
import kotlin.test.assertEquals

actual fun internalsOf(x: () -> Any): String {
    return x().toString()
}

actual fun log(x: () -> Any) {
    System.err.println(x())
}

actual fun <T : Any> assertClassNameIs(`class`: KClass<T>, name: String) {
    assertEquals(name, `class`.qualifiedName)
}
