package it.unibo.tuprolog.parser.operators

internal class MutableOperatorTableImpl(
    definitions: Collection<OperatorDefinition>,
) : MutableOperatorTable {
    private val entries: MutableMap<String, OperatorDefinitionSet> = mutableMapOf()

    init {
        definitions.forEach(::define)
    }

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

    override fun define(definition: OperatorDefinition) {
        entries
            .getOrPut(definition.name, ::OperatorDefinitionSet)
            .set(definition.specifier, definition.priority)
    }

    override fun remove(
        name: String,
        specifier: OperatorSpecifier,
    ) {
        val definitions = entries[name] ?: return
        definitions.remove(specifier)
        if (definitions.isEmpty()) {
            entries.remove(name)
        }
    }

    override fun removeAll(name: String) {
        entries.remove(name)
    }

    override fun clear() {
        entries.clear()
    }

    override fun snapshot(): OperatorTable = ImmutableOperatorTable(allDefinitions())
}
