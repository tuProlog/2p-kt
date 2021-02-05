@file:Suppress("UNCHECKED_CAST")
@file:JvmName("TaggableExtensions")

package it.unibo.tuprolog.utils

import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("addTag")
fun <T : U, U : Taggable<U>, X : Any> T.addTag(name: String, value: X): T = addTags(name to value)

@JsName("addTagPairs")
fun <T : U, U : Taggable<U>, X : Any> T.addTags(tag: Pair<String, X>, vararg otherTags: Pair<String, X>): T =
    this.addTags(mapOf(tag, *otherTags))

@JsName("plusTag")
operator fun <T : U, U : Taggable<U>, X : Any> T.plus(tag: Pair<String, X>): T = addTags(tag)

@JsName("addTags")
fun <T : U, U : Taggable<U>, X : Any> T.addTags(tags: Map<String, X>): T = setTags(this.tags + tags)

@JsName("plusTags")
operator fun <T : U, U : Taggable<U>, X : Any> T.plus(tags: Map<String, X>): T = addTags(tags)

@JsName("setTags")
fun <T : U, U : Taggable<U>, X : Any> T.setTags(tags: Map<String, X>): T = replaceTags(tags) as T

@JsName("setTagPairs")
fun <T : U, U : Taggable<U>, X : Any> T.setTags(tag: Pair<String, X>, vararg otherTags: Pair<String, X>): T =
    setTags(mapOf(tag, *otherTags))

@JsName("setTag")
fun <T : U, U : Taggable<U>, X : Any> T.setTag(key: String, value: X): T =
    setTags(tags.toMutableMap().also { it[key] = value })

@JsName("clearTags")
fun <T : U, U : Taggable<U>> T.clearTags(): T = setTags(emptyMap())
