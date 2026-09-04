package it.unibo.tuprolog.parser.operators

internal class OperatorDefinitionSet {
    private val priorities: IntArray = IntArray(Associativity.entries.size)

    fun get(specifier: Associativity): Int? = priorities[specifier.ordinal].takeIf { it != 0 }

    fun set(
        specifier: Associativity,
        priority: Int,
    ) {
        priorities[specifier.ordinal] = priority
    }

    fun remove(specifier: Associativity) {
        priorities[specifier.ordinal] = 0
    }

    fun isEmpty(): Boolean = priorities.all { it == 0 }

    fun definitions(name: String): List<OperatorDefinition> =
        buildList {
            for (specifier in Associativity.entries) {
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
