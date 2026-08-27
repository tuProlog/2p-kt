package it.unibo.tuprolog.parser.operators

internal class OperatorDefinitionSet {
    private val priorities: IntArray = IntArray(OperatorSpecifier.entries.size)

    fun get(specifier: OperatorSpecifier): Int? = priorities[specifier.ordinal].takeIf { it != 0 }

    fun set(
        specifier: OperatorSpecifier,
        priority: Int,
    ) {
        priorities[specifier.ordinal] = priority
    }

    fun remove(specifier: OperatorSpecifier) {
        priorities[specifier.ordinal] = 0
    }

    fun isEmpty(): Boolean = priorities.all { it == 0 }

    fun definitions(name: String): List<OperatorDefinition> =
        buildList {
            for (specifier in OperatorSpecifier.entries) {
                val priority = priorities[specifier.ordinal]
                if (priority != 0) {
                    add(OperatorDefinition(name, specifier, priority))
                }
            }
        }

    fun copy(): OperatorDefinitionSet =
        OperatorDefinitionSet().also { target ->
            priorities.copyInto(target.priorities)
        }
}
