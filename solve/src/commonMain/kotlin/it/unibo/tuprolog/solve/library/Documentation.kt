package it.unibo.tuprolog.solve.library

import it.unibo.tuprolog.solve.AbstractWrapper
import it.unibo.tuprolog.solve.Signature

private const val INTERNAL_HELP_FUNCTOR = "__help__"

internal fun defaultDocumentation(pluggable: Pluggable): Map<Signature, String> {
    val fragments = linkedMapOf<Signature, MutableList<String>>()

    fun add(
        signature: Signature,
        text: String,
    ) {
        if (signature.name == INTERNAL_HELP_FUNCTOR) return
        val trimmed = text.trim()
        if (trimmed.isNotEmpty()) {
            fragments.getOrPut(signature) { mutableListOf() }.add(trimmed)
        }
    }

    for ((signature, primitive) in pluggable.primitives) {
        add(signature, primitive.help.ifBlank { "`${signature.formatForHelp()}`\n\nPrimitive." })
    }
    for ((signature, function) in pluggable.functions) {
        add(signature, function.help.ifBlank { "`${signature.formatForHelp()}`\n\nFunction." })
    }
    for (signature in pluggable.rulesSignatures) {
        add(signature, "`${signature.formatForHelp()}`\n\nRule.")
    }
    for (operator in pluggable.operators) {
        val signature = Signature(operator.functor, if (operator.specifier.isInfix) 2 else 1)
        add(
            signature,
            "`${signature.formatForHelp()}`\n\n" +
                "Operator `${operator.functor}`: `${operator.specifier.name.lowercase()}`, " +
                "priority `${operator.priority}`.",
        )
    }

    return fragments.mapValues { (_, values) -> values.distinct().joinToString("\n\n---\n\n") }
}

internal fun documentationOf(wrappers: Sequence<AbstractWrapper<*>>): Map<Signature, String> {
    val fragments = linkedMapOf<Signature, MutableList<String>>()
    for (wrapper in wrappers) {
        val help = wrapper.help.trim()
        if (help.isNotEmpty()) {
            fragments.getOrPut(wrapper.signature) { mutableListOf() }.add(help)
        }
    }
    return fragments.mapValues { (_, values) -> values.distinct().joinToString("\n\n---\n\n") }
}

internal fun mergeDocumentation(vararg documentation: Map<Signature, String>): Map<Signature, String> {
    val fragments = linkedMapOf<Signature, MutableList<String>>()
    for (map in documentation) {
        for ((signature, help) in map) {
            val trimmed = help.trim()
            if (trimmed.isNotEmpty()) {
                fragments.getOrPut(signature) { mutableListOf() }.add(trimmed)
            }
        }
    }
    return fragments.mapValues { (signature, values) ->
        val unique = values.distinct()
        val hasSemanticDocumentation =
            unique.any {
                !signature.isGeneratedFallback(it) && !signature.isGeneratedOperatorDocumentation(it)
            }
        val normalized =
            if (hasSemanticDocumentation) {
                unique.filterNot(signature::isGeneratedFallback)
            } else {
                unique
            }
        normalized.joinToString("\n\n---\n\n")
    }
}

private fun Signature.isGeneratedFallback(help: String): Boolean =
    help == "`${formatForHelp()}`\n\nPrimitive." ||
        help == "`${formatForHelp()}`\n\nFunction." ||
        help == "`${formatForHelp()}`\n\nRule."

private fun Signature.isGeneratedOperatorDocumentation(help: String): Boolean =
    help.startsWith("`${formatForHelp()}`\n\nOperator ")

private fun Signature.formatForHelp(): String = "$name/$arity${if (vararg) "+" else ""}"
