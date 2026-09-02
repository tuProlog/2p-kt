package it.unibo.tuprolog.parser

/** An element that can produce a normalized textual representation of itself. */
fun interface Representable {
    /** Returns the element's normalized representation, which need not preserve original trivia. */
    fun toRepresentation(): String
}
