package it.unibo.tuprolog.parser.operators

interface OperatorTable {
    fun definition(
        name: String,
        specifier: OperatorSpecifier,
    ): OperatorDefinition?

    fun definitions(name: String): List<OperatorDefinition>

    fun isOperator(name: String): Boolean

    fun allDefinitions(): List<OperatorDefinition>
}
