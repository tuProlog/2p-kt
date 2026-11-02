package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

actual fun internalsOf(x: () -> Any): String = x().toString()

actual fun log(x: () -> Any) {
    System.err.println(x())
}

actual fun <T : Any> assertClassNameIs(
    klass: KClass<T>,
    name: String,
) {
    klass.qualifiedName shouldBe name
}
