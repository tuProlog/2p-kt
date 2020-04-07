package it.unibo.tuprolog.core

import kotlin.js.JsName

/**
 * An interface to be implemented by types convertible to Prolog [Term]s
 *
 * @author Enrico
 */
interface ToTermConvertible {

    /** Converts this instance to a Prolog [Term] */
    @JsName("toTerm")
    fun toTerm(): Term
}
