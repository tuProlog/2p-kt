package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.parser.Token

class ParseException(
    var input: Any?,
    var offendingSymbol: String,
    var line: Int,
    message: String?,
    throwable: Throwable?
) : RuntimeException(message, throwable) {

    var clauseIndex = -1

    constructor(
        input: Any?,
        token: Token,
        message: String?,
        throwable: Throwable?
    ) : this(input, token.text, token.line, message, throwable)

    constructor(token: Token, message: String?, throwable: Throwable?) : this(
        null,
        token.text,
        token.line,
        message,
        throwable
    )

    constructor(token: Token, message: String?) : this(
        null,
        token.text,
        token.line,
        message,
        null
    )

    constructor(token: Token, throwable: Throwable?) : this(
        null,
        token.text,
        token.line,
        "",
        throwable
    )

    override fun toString(): String {
        return "ParseException{" +
                "message='" + message!!.replace("\\n", "\\\\n") + '\'' +
                ", line=" + line +
                ", offendingSymbol='" + offendingSymbol + '\'' +
                '}'
    }

}
