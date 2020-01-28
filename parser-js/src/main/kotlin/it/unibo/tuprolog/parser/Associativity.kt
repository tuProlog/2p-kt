@file:JsModule("./Associativity")
@file:JsNonModule

package it.unibo.tuprolog.parser

external enum class Associativity {
    XF, YF, XFX, XFY, YFX, FX, FY;
    companion object{
        val PREFIX: Array<Associativity>
        val NON_PREFIX: Array<Associativity>
        val INFIX: Array<Associativity>
        val POSTFIX: Array<Associativity>
    }
}

