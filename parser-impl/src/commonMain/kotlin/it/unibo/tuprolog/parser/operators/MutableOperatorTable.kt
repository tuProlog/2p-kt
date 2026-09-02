package it.unibo.tuprolog.parser.operators

/**
 * Mutable operator lookup intended for clause-by-clause parse sessions.
 *
 * The type is stateful and not designed for concurrent access. Defining the same name/specifier
 * pair again replaces its priority; other specifiers for that name remain intact.
 */
interface MutableOperatorTable : OperatorTable {
    /** Adds [definition], replacing the priority of an existing name/specifier pair. */
    fun define(definition: OperatorDefinition)

    /**
     * Defines one operator from its components.
     *
     * @throws it.unibo.tuprolog.parser.exceptions.InvalidOperatorDefinitionException if [name] or
     * [priority] is invalid
     */
    fun define(
        name: String,
        specifier: Associativity,
        priority: Int,
    ): Unit = define(OperatorDefinition(name, specifier, priority))

    /** Removes only the [specifier] definition of [name], if present. */
    fun remove(
        name: String,
        specifier: Associativity,
    )

    /** Removes every definition of [name]. */
    fun removeAll(name: String)

    /** Removes every operator definition. */
    fun clear()

    /** Returns an immutable copy isolated from subsequent mutations. */
    fun snapshot(): OperatorTable
}
