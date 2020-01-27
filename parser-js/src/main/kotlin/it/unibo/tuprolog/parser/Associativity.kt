@file:JsModule("./Associativity")
@file:JsNonModule

package it.unibo.tuprolog.parser

external enum class Associativity {
    XF, YF, XFX, XFY, YFX, FX, FY
}