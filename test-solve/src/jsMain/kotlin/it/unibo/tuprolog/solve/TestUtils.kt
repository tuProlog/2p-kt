package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

actual fun internalsOf(x: () -> Any): String = JSON.stringify(x())

actual fun log(x: () -> Any) {
    console.log(x())
}

actual fun <T : Any> assertClassNameIs(
    klass: KClass<T>,
    name: String,
) {
    name.split('.').last() shouldBe klass.simpleName
}
