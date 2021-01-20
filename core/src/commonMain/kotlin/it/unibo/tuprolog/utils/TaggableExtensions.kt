@file:Suppress("UNCHECKED_CAST")
@file:JvmName("TaggableExtensions")

package it.unibo.tuprolog.utils

import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("addKeyValueTag")
fun <T : U, U : Taggable<U>, X : Any> T.addTag(name: String, value: X): T = addTag(name to value)

@JsName("addTag")
fun <T : U, U : Taggable<U>, X : Any> T.addTag(tag: Pair<String, X>): T = addTags(mapOf(tag))

@JsName("plusTag")
operator fun <T : U, U : Taggable<U>, X : Any> T.plus(tag: Pair<String, X>): T = addTag(tag)

@JsName("addTags")
fun <T : U, U : Taggable<U>, X : Any> T.addTags(tags: Map<String, X>): T = setTags(this.tags + tags)

@JsName("plusTags")
operator fun <T : U, U : Taggable<U>, X : Any> T.plus(tags: Map<String, X>): T = addTags(tags)

@JsName("setTags")
fun <T : U, U : Taggable<U>, X : Any> T.setTags(tags: Map<String, X>): T = replaceTags(tags) as T

@JsName("setTag")
fun <T : U, U : Taggable<U>, X : Any> T.setTag(key: String, value: X): T =
    setTags(tags.toMutableMap().also { it[key] = value })

@JsName("clearTags")
fun <T : U, U : Taggable<U>> T.clearTags(): T = setTags(emptyMap())
