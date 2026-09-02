package it.unibo.tuprolog.parser.operators

/**
 * Read-only lookup of runtime Prolog operator definitions.
 *
 * Lexing never consults this table. Parsers resolve candidate atom spellings against it at the
 * point where their syntactic role is known.
 */
interface OperatorTable {
    /** Returns the definition for [name] and [specifier], or `null` if absent. */
    fun definition(
        name: String,
        specifier: Associativity,
    ): OperatorDefinition?

    /** Returns all definitions for [name] in stable [Associativity] order. */
    fun definitions(name: String): List<OperatorDefinition>

    /** Returns whether at least one definition exists for [name]. */
    fun isOperator(name: String): Boolean

    /** Returns every definition in stable name and [Associativity] order. */
    fun allDefinitions(): List<OperatorDefinition>
}
