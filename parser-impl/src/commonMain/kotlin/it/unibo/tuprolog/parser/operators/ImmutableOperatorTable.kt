package it.unibo.tuprolog.parser.operators

internal class ImmutableOperatorTable(
    definitions: Collection<OperatorDefinition>,
) : OperatorTable {
    private val entries: Map<String, OperatorDefinitionSet> =
        buildMap<String, OperatorDefinitionSet> {
            for (definition in definitions) {
                getOrPut(definition.name, ::OperatorDefinitionSet)
                    .set(definition.specifier, definition.priority)
            }
        }.mapValues { it.value.copy() }

    override fun definition(
        name: String,
        specifier: OperatorSpecifier,
    ): OperatorDefinition? = entries[name]?.get(specifier)?.let { OperatorDefinition(name, specifier, it) }

    override fun definitions(name: String): List<OperatorDefinition> = entries[name]?.definitions(name).orEmpty()

    override fun isOperator(name: String): Boolean = entries.containsKey(name)

    override fun allDefinitions(): List<OperatorDefinition> =
        entries.entries
            .sortedBy { it.key }
            .flatMap { (name, definitions) -> definitions.definitions(name) }
}
