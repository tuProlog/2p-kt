package it.unibo.tuprolog.parser.operators

object OperatorTables {
    fun empty(): OperatorTable = ImmutableOperatorTable(emptyList())

    fun of(vararg definitions: OperatorDefinition): OperatorTable = ImmutableOperatorTable(definitions.asList())

    fun mutableOf(vararg definitions: OperatorDefinition): MutableOperatorTable =
        MutableOperatorTableImpl(definitions.asList())
}
