@file:JsModule("@tuprolog/parser-utils")
@file:JsNonModule

package it.unibo.tuprolog.parser

external object Associativity {
    val XF: String
    val YF: String
    val XFX: String
    val XFY: String
    val YFX: String
    val FX: String
    val FY: String

    val PREFIX: Array<String>
    val NON_PREFIX: Array<String>
    val INFIX: Array<String>
    val POSTFIX: Array<String>
}
