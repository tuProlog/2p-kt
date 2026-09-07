package it.unibo.tuprolog.solve

import kotlin.reflect.KClass

/**
 * A parsed reference to a JS value (e.g. a [SolverFactory] object instance) reachable as `require(module)` followed
 * by the dotted [qualifiedName] path, as used by the JS `actual` lookups of `classicSolverFactory()` and friends.
 */
data class JsClassName(
    val module: String,
    val qualifiedName: String,
) {
    companion object {
        /** Parses [fullName] (formatted as `module:qualified.name`) into a [JsClassName]. */
        fun parse(fullName: String): JsClassName {
            val splitted = fullName.split(":")
            return JsClassName(
                splitted.subList(0, splitted.lastIndex).joinToString(""),
                splitted.last(),
            )
        }

        private val require: (String) -> dynamic by lazy { js("require") }
    }

    /** [qualifiedName], split on `.` into the sequence of property accesses [resolve] performs. */
    val path: List<String> by lazy {
        qualifiedName.split('.')
    }

    /** Resolves this reference via JS `require`, returning `null` if [module] or any segment of [path] can't be found. */
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

    /** The [KClass] of the value [resolve]d by this reference. */
    val kClass: KClass<*> by lazy {
        resolve()::class
    }
}
