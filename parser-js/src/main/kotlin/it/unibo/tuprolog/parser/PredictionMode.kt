@file:JsModule("antlr4/atn")
@file:JsNonModule
package it.unibo.tuprolog.parser

/**
 * https://github.com/antlr/antlr4/blob/98dc2c0f0249a67b797b151da3adf4ffbc1fd6a1/runtime/JavaScript/src/antlr4/atn/PredictionMode.js
 * */
external object PredictionMode {
    val SLL: Int
    val LL: Int
    val LL_EXACT_AMBIG_DETECTION: Int
}
