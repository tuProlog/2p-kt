package it.unibo.tuprolog.parser

class ParsingException(
    val input: String,
    val offendingSymbol: String,
    val line: Int,
    val column: Int,
    message: String?,
    throwable: Throwable?,
) : RuntimeException(message, throwable) {
    override fun toString(): String =
        "ParsingException{" +
            "message='" + message?.replace("\\n", "\\\\n") + '\'' +
            ", line=" + line +
            ", column=" + column +
            ", offendingSymbol='" + offendingSymbol + '\'' +
            '}'
}
