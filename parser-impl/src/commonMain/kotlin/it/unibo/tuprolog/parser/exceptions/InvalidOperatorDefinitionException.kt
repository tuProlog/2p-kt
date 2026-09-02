package it.unibo.tuprolog.parser.exceptions

/** Indicates that an operator has an empty name or a priority outside the Prolog range. */
class InvalidOperatorDefinitionException(
    message: String,
) : IllegalArgumentException(message)
