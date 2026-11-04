package it.unibo.tuprolog.utils

import kotlin.js.JsName

/**
 * A typed name for a tag of [Taggable] objects, so that well-known metadata (e.g. where a theory was loaded from) can
 * be read and written without casts, while still being stored, and propagated, as an ordinary tag:
 *
 * ```kotlin
 * val Source = TagKey<String>("source")
 * val theory = Theory.empty().withTag(Source, "file.pl")
 * theory[Source] // "file.pl"
 * ```
 *
 * Keys are equal when their names are, whatever their type parameter: use distinct names for distinct metadata.
 */
data class TagKey<T : Any>(
    val name: String,
)

/** The value of the tag named by [key], or `null` if absent. */
@Suppress("UNCHECKED_CAST")
@JsName("getTagByKey")
operator fun <T : Any> Taggable<*>.get(key: TagKey<T>): T? = tags[key.name] as? T

/** A copy of this object with the tag named by [key] set to [value]. */
@JsName("withTagByKey")
fun <T : U, U : Taggable<U>, X : Any> T.withTag(
    key: TagKey<X>,
    value: X,
): T = addTag(key.name, value)
