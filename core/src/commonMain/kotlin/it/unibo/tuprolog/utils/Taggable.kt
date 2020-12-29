package it.unibo.tuprolog.utils

import kotlin.js.JsName

interface Taggable<Self : Taggable<Self>> {
    @JsName("tags")
    val tags: Map<String, Any>

    @Suppress("UNCHECKED_CAST")
    @JsName("getTag")
    fun <T : Any> getTag(name: String): T? = tags[name] as T?

    @JsName("resetTags")
    fun replaceTags(tags: Map<String, Any>): Self

    @JsName("containsTag")
    fun containsTag(name: String): Boolean = name in tags
}
