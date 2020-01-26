package it.unibo.tuprolog.parser

fun Token.getNameAccordingTo(lexer: dynamic): String {
    return lexer.symbolicNames[this.type]
}