package it.unibo.tuprolog.core.parsing

class ParseException(
    var input: Any?,
    var offendingSymbol: String,
    var line: Int,
    var column: Int,
    message: String?,
    throwable: Throwable?
) : RuntimeException(message, throwable) {

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
