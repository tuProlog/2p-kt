package it.unibo.tuprolog.utils

import kotlin.js.JsName

/**
 * A type for immutable objects that carry an arbitrary, named bag of metadata (their [tags]), attachable
 * without polluting the type's own API with a case-by-case property for every possible piece of metadata.
 *
 * This is used, for instance, by `it.unibo.tuprolog.core.Term` and `it.unibo.tuprolog.core.Substitution` (in
 * the `:core` module) to let solvers and libraries stash solver-specific bookkeeping data (e.g. provenance
 * information) onto terms and substitutions, without both those types and every one of their many
 * implementations needing to know about it. Since implementations are expected to be immutable,
 * "mutating" the tags of a [Self] does not happen in place: it always goes through [replaceTags] (see the
 * [addTag]/[addTags]/[setTag]/[setTags]/[clearTags] extension functions in `Taggables.kt` for convenient,
 * higher-level ways to derive a new, differently-tagged instance).
 * @param Self is the (CRTP) concrete type implementing this interface, i.e. the type returned by [replaceTags]
 */
interface Taggable<Self : Taggable<Self>> {
    /** The (possibly empty) map of tags currently attached to this object, keyed by tag name. */
    @JsName("tags")
    val tags: Map<String, Any>

    /**
     * Retrieves the tag named [name], cast to [T], or `null` if no such tag is attached. The cast is
     * unchecked (erased generics): if a tag named [name] is attached but is not actually an instance of
     * [T], this call will not fail, but a [ClassCastException] is likely to surface later, wherever the
     * caller uses the returned value as a [T].
     */
    @Suppress("UNCHECKED_CAST")
    @JsName("getTag")
    fun <T : Any> getTag(name: String): T? = tags[name] as T?

    /**
     * Returns a new instance, otherwise equivalent to this one, whose [tags] are entirely replaced by
     * [tags] (i.e. this is not a merge: any tag not present in [tags] is dropped).
     */
    @JsName("resetTags")
    fun replaceTags(tags: Map<String, Any>): Self

    /** Whether a tag named [name] is currently attached to this object. */
    @JsName("containsTag")
    fun containsTag(name: String): Boolean = name in tags
}
