@file:Suppress("UNCHECKED_CAST")

package it.unibo.tuprolog.utils

import kotlin.js.JsName

@JsName("addKeyValueTag")
fun <T : U, U : Taggable<U>> T.addTag(name: String, value: Any): T = addTag(name to value)

@JsName("addTag")
fun <T : U, U : Taggable<U>> T.addTag(tag: Pair<String, Any>): T = addTags(mapOf(tag))

@JsName("plusTag")
operator fun <T : U, U : Taggable<U>> T.plus(tag: Pair<String, Any>): T = addTag(tag)

@JsName("addTags")
fun <T : U, U : Taggable<U>> T.addTags(tags: Map<String, Any>): T = setTags(this.tags + tags) as T

@JsName("plusTags")
operator fun <T : U, U : Taggable<U>> T.plus(tags: Map<String, Any>): T = addTags(tags)

@JsName("setTags")
fun <T : U, U : Taggable<U>> T.setTags(tags: Map<String, Any>): T = replaceTags(tags) as T

@JsName("clearTags")
fun <T : U, U : Taggable<U>> T.clearTags(): T = setTags(emptyMap())