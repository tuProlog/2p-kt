package it.unibo.tuprolog.serialize

@JsModule("yaml")
@JsNonModule
external object YAML {
    fun stringify(value: dynamic, options: dynamic): String
    fun stringify(value: dynamic): String

    fun parse(string: String, options: dynamic): dynamic
    fun parse(string: String): dynamic
}