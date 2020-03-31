package node

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

fun String.toJson(): JsonPrimitive = JsonPrimitive(this)

fun jsonArray(items: Iterable<JsonElement>): JsonArray {
    val array = JsonArray()
    for (x in items) {
        array.add(x)
    }
    return array
}

fun jsonObject(vararg keys: Pair<String, String>): JsonObject =
    jsonObject(mapOf(*keys))

fun jsonObject(keys: Map<String, String>): JsonObject {
    val obj = JsonObject()
    for (kv in keys) {
        obj.addProperty(kv.key, kv.value)
    }
    return obj
}

fun JsonElement.getPropertyOrNull(key: String): String? {
    return getOrNull(key)?.asString
}

fun JsonElement.getOrNull(key: String): JsonElement? {
    if (this is JsonObject) {
        if (has(key)) {
            return get(key)
        }
    }
    return null
}

fun JsonElement.asPropertyMapOrNull(): MutableMap<String, String>? {
    if (this is JsonObject) {
        return keySet().map { it to this@asPropertyMapOrNull[it].asString }.toMap(mutableMapOf())
    }
    return null
}

fun JsonElement.asListOrNull(): MutableList<JsonElement>? {
    if (this is JsonArray) {
        return this.map { it }.toMutableList()
    }
    return null
}

fun JsonElement.asPropertyListOrNull(): MutableList<String>? {
    return asListOrNull()?.map { it.asString }?.toMutableList()
}

