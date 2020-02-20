package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.exception.TuPrologException

open class ParseException(
    var input: Any?,
    var offendingSymbol: String,
    var line: Int,
    var column: Int,
    message: String?,
    throwable: Throwable?
) : TuPrologException(message, throwable) {

    var clauseIndex = -1

    override fun toString(): String {
        return "ParseException{" +
                "message='" + message!!.replace("\\n", "\\\\n") + '\'' +
                ", line=" + line +
                ", column=" + column +
                ", offendingSymbol='" + offendingSymbol + '\'' +
                '}'
    }

}
