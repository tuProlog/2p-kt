package it.unibo.tuprolog.solve

import kotlin.reflect.KClass

data class JsClassName(
    val module: String,
    val qualifiedName: String,
) {
    companion object {
        fun parse(fullName: String): JsClassName {
            val splitted = fullName.split(":")
            return JsClassName(
                splitted.subList(0, splitted.lastIndex).joinToString(""),
                splitted.last(),
            )
        }

        private val require: (String) -> dynamic by lazy { js("require") }
    }

    val path: List<String> by lazy {
        qualifiedName.split('.')
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    fun resolve(): dynamic {
        try {
            var resolved = require(module)
            for (key in path) {
                if (resolved == null) break
                resolved = resolved[key]
            }
            return resolved
        } catch (e: Throwable) {
            return null
        }
    }

    val kClass: KClass<*> by lazy {
        resolve()::class
    }
}
