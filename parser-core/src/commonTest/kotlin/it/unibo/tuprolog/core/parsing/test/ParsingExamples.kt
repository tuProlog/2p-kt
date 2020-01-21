package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.dsl.unify.prolog

object ParsingExamples {
    val canonicalTerms: Sequence<Pair<String, Term>> = sequenceOf(
        "f(X)" to prolog { "f"("X") },
        "[]" to prolog { emptyList() },
        "[ ]" to prolog { emptyList() },
        "[1]" to prolog { listOf(1) },
        "[a]" to prolog { listOf("a") }
    )

    val expressions: Sequence<Pair<String, Term>> = sequenceOf(
        "1 + 2" to prolog {
            1.toTerm() + 2
        },
        "A; _" to prolog {
            "A" or `_`
        }
    )

    val all = canonicalTerms + expressions
}