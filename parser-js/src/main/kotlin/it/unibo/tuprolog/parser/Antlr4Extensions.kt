package it.unibo.tuprolog.parser

fun Token.getNameAccordingTo(lexer: dynamic): String {
    return lexer.symbolicNames[this.type] as String
}

fun jsClassName(obj: dynamic): String? =
    obj?.__proto__?.constructor?.name as String?

fun isParseCancellationException(obj: dynamic): Boolean =
    jsClassName(obj) == "ParseCancellationException"

fun isRecognitionException(obj: dynamic): Boolean =
    jsClassName(obj) == "RecognitionException"
