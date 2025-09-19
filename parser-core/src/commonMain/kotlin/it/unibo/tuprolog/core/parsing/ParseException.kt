package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.exception.TuPrologException
import kotlin.js.JsName

open class ParseException(
    @JsName("input") var input: Any?,
    @JsName("offendingSymbol") var offendingSymbol: String,
    @JsName("line") var line: Int,
    @JsName("column") var column: Int,
    message: String?,
    throwable: Throwable?,
) : TuPrologException(message, throwable) {
    @JsName("clauseIndex")
    var clauseIndex = -1

    override fun toString(): String =
        "ParseException{" +
            "message='" + message!!.replace("\\n", "\\\\n") + '\'' +
            ", line=" + line +
            ", column=" + column +
            ", offendingSymbol='" + offendingSymbol + '\'' +
            '}'
}
