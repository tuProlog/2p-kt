package it.unibo.tuprolog.parser.operators

/** Factory methods for immutable and mutable [OperatorTable] instances. */
object OperatorTables {
    /** Returns an empty immutable table. */
    fun empty(): OperatorTable = ImmutableOperatorTable(emptyList())

    /** Returns an immutable table containing [definitions]. */
    fun of(vararg definitions: OperatorDefinition): OperatorTable = ImmutableOperatorTable(definitions.asList())

    /** Returns an immutable table containing [definitions]. */
    fun of(definitions: Iterable<OperatorDefinition>): OperatorTable = ImmutableOperatorTable(definitions.toList())

    /** Returns a mutable table initially containing [definitions]. */
    fun mutableOf(vararg definitions: OperatorDefinition): MutableOperatorTable =
        MutableOperatorTableImpl(definitions.asList())

    /** Returns a mutable table initially containing [definitions]. */
    fun mutableOf(definitions: Iterable<OperatorDefinition>): MutableOperatorTable =
        MutableOperatorTableImpl(definitions.toList())
}
