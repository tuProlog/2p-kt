package it.unibo.tuprolog.core.parsing

import org.antlr.v4.runtime.Token

class ParseException(
    var input: Any?,
    var offendingSymbol: String,
    var line: Int,
    var column: Int,
    message: String?,
    throwable: Throwable?
) :
    RuntimeException(message, throwable) {
    var clauseIndex = -1

    constructor(
        input: Any?,
        token: Token,
        message: String?,
        throwable: Throwable?
    ) : this(input, token.text, token.line, token.charPositionInLine, message, throwable) {
    }

    constructor(token: Token, message: String?, throwable: Throwable?) : this(
        null,
        token.text,
        token.line,
        token.charPositionInLine,
        message,
        throwable
    ) {
    }

    constructor(token: Token, message: String?) : this(
        null,
        token.text,
        token.line,
        token.charPositionInLine,
        message,
        null
    ) {
    }

    constructor(token: Token, throwable: Throwable?) : this(
        null,
        token.text,
        token.line,
        token.charPositionInLine,
        "",
        throwable
    ) {
    }

    override fun toString(): String {
        return "ParseException{" +
                "message='" + message!!.replace("\\n", "\\\\n") + '\'' +
                ", line=" + line +
                ", column=" + column +
                ", offendingSymbol='" + offendingSymbol + '\'' +
                '}'
    }

}
