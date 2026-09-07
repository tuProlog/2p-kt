@file:Suppress("UNCHECKED_CAST")
@file:JvmName("Taggables")

package it.unibo.tuprolog.utils

import kotlin.js.JsName
import kotlin.jvm.JvmName

// Convenience extension functions deriving new, differently-tagged Taggable instances, without callers
// having to manually compute the resulting Taggable.tags map and invoke Taggable.replaceTags themselves.
// Every function here returns a *new* instance of the same concrete type as the receiver (leaving the
// receiver untouched), since Taggable implementations are meant to be immutable.

/**
 * Returns a new instance, otherwise equivalent to this one, with an additional tag named [name] holding
 * [value] merged into its existing [Taggable.tags] (overwriting any existing tag with the same [name]).
 */
@JsName("addTag")
fun <T : U, U : Taggable<U>, X : Any> T.addTag(
    name: String,
    value: X,
): T = addTags(name to value)

/**
 * Returns a new instance, otherwise equivalent to this one, with [tag] (and any [otherTags]) merged into
 * its existing [Taggable.tags] (overwriting any existing tags with the same names).
 */
@JsName("addTagPairs")
fun <T : U, U : Taggable<U>, X : Any> T.addTags(
    tag: Pair<String, X>,
    vararg otherTags: Pair<String, X>,
): T = this.addTags(mapOf(tag, *otherTags))

/**
 * Operator alias for [addTags], allowing tags to be merged in via the `+` operator, e.g. `term + ("k" to v)`.
 */
@JsName("plusTag")
operator fun <T : U, U : Taggable<U>, X : Any> T.plus(tag: Pair<String, X>): T = addTags(tag)

/**
 * Returns a new instance, otherwise equivalent to this one, with [tags] merged into its existing
 * [Taggable.tags] (overwriting any existing tags with the same names as one of [tags]' keys).
 */
@JsName("addTags")
fun <T : U, U : Taggable<U>, X : Any> T.addTags(tags: Map<String, X>): T = setTags(this.tags + tags)

/**
 * Operator alias for [addTags], allowing a whole tags map to be merged in via the `+` operator.
 */
@JsName("plusTags")
operator fun <T : U, U : Taggable<U>, X : Any> T.plus(tags: Map<String, X>): T = addTags(tags)

/**
 * Returns a new instance, otherwise equivalent to this one, whose [Taggable.tags] are entirely replaced by
 * [tags] (i.e. this is not a merge: any previously attached tag not present in [tags] is dropped). Thin,
 * type-preserving wrapper around [Taggable.replaceTags].
 */
@JsName("setTags")
fun <T : U, U : Taggable<U>, X : Any> T.setTags(tags: Map<String, X>): T = replaceTags(tags) as T

/**
 * Same as [setTags], taking the replacement tags as a [tag] (and any [otherTags]) rather than as a [Map].
 */
@JsName("setTagPairs")
fun <T : U, U : Taggable<U>, X : Any> T.setTags(
    tag: Pair<String, X>,
    vararg otherTags: Pair<String, X>,
): T = setTags(mapOf(tag, *otherTags))

/**
 * Returns a new instance, otherwise equivalent to this one, whose only tag is [key] holding [value] (any
 * other previously attached tag with a different name is preserved, but one named [key] is overwritten).
 */
@JsName("setTag")
fun <T : U, U : Taggable<U>, X : Any> T.setTag(
    key: String,
    value: X,
): T = setTags(tags.toMutableMap().also { it[key] = value })

/**
 * Returns a new instance, otherwise equivalent to this one, but with no tags attached at all.
 */
@JsName("clearTags")
fun <T : U, U : Taggable<U>> T.clearTags(): T = setTags(emptyMap())
