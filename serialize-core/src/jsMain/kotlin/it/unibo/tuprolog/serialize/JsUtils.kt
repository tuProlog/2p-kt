package it.unibo.tuprolog.serialize

internal fun jsObject(config: dynamic.() -> Unit): Any {
    val obj: dynamic = object {}
    config(obj)
    return obj
}

internal fun jsObject(
    vararg properties: Pair<String, dynamic>,
    config: dynamic.() -> Unit = {},
): Any =
    jsObject {
        for ((k, v) in properties) {
            this[k] = v
        }
        config(this)
    }

internal fun jsObject(
    properties: Iterable<Pair<String, dynamic>>,
    config: dynamic.() -> Unit = {},
): Any =
    jsObject {
        for ((k, v) in properties) {
            this[k] = v
        }
        config(this)
    }

internal fun hasProperty(
    obj: dynamic,
    name: String,
): Boolean = obj[name] != undefined
