@file:JsModule("./Associativity")
@file:JsNonModule

package it.unibo.tuprolog.parser

//external enum class Associativity {
//    XF, YF, XFX, XFY, YFX, FX, FY;
//
//    companion object {
//        val PREFIX: Array<Associativity>
//        val NON_PREFIX: Array<Associativity>
//        val INFIX: Array<Associativity>
//        val POSTFIX: Array<Associativity>
//    }
//}

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

