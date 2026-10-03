package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.library.Library
import kotlin.test.assertTrue

private const val FRAGMENT_SEPARATOR = "\n\n---\n\n"

private const val MIN_DESCRIPTION_LENGTH = 40

/**
 * Asserts that [library] and every predicate, function, rule and operator it contributes are documented
 * non-trivially: each one needs a fragment with a description of at least [MIN_DESCRIPTION_LENGTH] characters
 * following its signature line, generated placeholders do not count, and no fragment may be repeated.
 */
fun assertFullyDocumented(library: Library) {
    val signatures =
        library.primitives.keys +
            library.functions.keys +
            library.rulesSignatures +
            library.operators.map { Signature(it.functor, if (it.specifier.isInfix) 2 else 1) }
    val problems =
        signatures.filterNot { it.name.endsWith("__help__") }.mapNotNull { signature ->
            val fragments =
                library.documentation[signature]
                    ?.split(FRAGMENT_SEPARATOR)
                    ?.map { it.trim() }
                    .orEmpty()
            when {
                fragments.size != fragments.distinct().size -> "$signature: repeated fragments"
                fragments.filterNot { it.isGeneratedFor(signature) }.none { it.hasDescription() } ->
                    "$signature: missing or too minimal"
                else -> null
            }
        } + listOfNotNull("library help: missing or too minimal".takeUnless { library.help.hasDescription() })
    assertTrue(problems.isEmpty(), "Poorly documented items of ${library.alias}:\n${problems.joinToString("\n")}")
}

private fun String.hasDescription(): Boolean = substringAfter("\n\n", "").trim().length >= MIN_DESCRIPTION_LENGTH

private fun String.isGeneratedFor(signature: Signature): Boolean {
    val header = "`${signature.name}/${signature.arity}${if (signature.vararg) "+" else ""}`\n\n"
    return this in listOf("Primitive.", "Function.", "Rule.").map { header + it } || startsWith(header + "Operator ")
}
