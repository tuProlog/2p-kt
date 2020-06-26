package it.unibo.tuprolog.serialize

fun emptyJsObject() = jsObject()

fun jsObject(config: dynamic.() -> Unit): Any {
    val obj = object {}
    config(obj)
    return obj
}

fun jsObject(vararg properties: Pair<String, dynamic>, config: dynamic.() -> Unit = {}): Any {
    return jsObject {
        for ((k, v) in properties) {
            this[k] = v
        }
        config(this)
    }
}

fun jsObject(properties: Iterable<Pair<String, dynamic>>, config: dynamic.() -> Unit = {}): Any {
    return jsObject {
        for ((k, v) in properties) {
            this[k] = v
        }
        config(this)
    }
}

fun jsObject(properties: Sequence<Pair<String, dynamic>>, config: dynamic.() -> Unit = {}): Any {
    return jsObject(properties.asIterable(), config)
}

fun hasProperty(obj: dynamic, name: String): Boolean =
    obj[name] != undefined