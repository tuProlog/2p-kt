package it.unibo.tuprolog.core

import kotlin.js.JsName

/**
 * A type for objects encapsulating some logic to convert any object of type [T] into a [String]
 * @param T is the type of the objects this formatter can handle
 */
interface Formatter<T> {
    /**
     * Converts an object of type [T] into a [String]
     * @param value is the object to be converted in [String]
     * @return a [String] representing the object provided as argument
     */
    @JsName("format")
    fun format(value: T): String
}

