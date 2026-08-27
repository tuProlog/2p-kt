package it.unibo.tuprolog.parser.operators

object OperatorTables {
    fun empty(): OperatorTable = ImmutableOperatorTable(emptyList())

    fun of(vararg definitions: OperatorDefinition): OperatorTable = ImmutableOperatorTable(definitions.asList())

    fun of(definitions: Iterable<OperatorDefinition>): OperatorTable = ImmutableOperatorTable(definitions.toList())

    fun mutableOf(vararg definitions: OperatorDefinition): MutableOperatorTable =
        MutableOperatorTableImpl(definitions.asList())

    fun mutableOf(definitions: Iterable<OperatorDefinition>): MutableOperatorTable =
        MutableOperatorTableImpl(definitions.toList())
}
