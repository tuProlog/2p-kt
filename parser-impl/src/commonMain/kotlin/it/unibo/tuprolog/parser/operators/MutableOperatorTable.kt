package it.unibo.tuprolog.parser.operators

interface MutableOperatorTable : OperatorTable {
    fun define(definition: OperatorDefinition)

    fun define(
        name: String,
        specifier: Associativity,
        priority: Int,
    ): Unit = define(OperatorDefinition(name, specifier, priority))

    fun remove(
        name: String,
        specifier: Associativity,
    )

    fun removeAll(name: String)

    fun clear()

    fun snapshot(): OperatorTable
}
