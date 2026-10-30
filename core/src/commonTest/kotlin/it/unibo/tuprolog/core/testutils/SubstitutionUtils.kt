package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.collections.listOf as ktListOf

/**
 * Utils object to test [Substitution]
 *
 * @author Enrico
 */
internal object SubstitutionUtils {
    /** A named way of building a term that internally can have multiple times a given term */
    internal data class TermWithCase(
        val id: String,
        val build: (Term) -> Term,
    )

    /** Builds terms that internally can have multiple times the provided term */
    internal val termsWithCases by lazy {
        ktListOf(
            TermWithCase("itself") { it },
            TermWithCase("struct-with-it-twice") { Struct.of("f", it, it) },
            TermWithCase("folded-struct-with-it-twice") { Struct.fold("f", it, Atom.of("ciao"), it) },
        )
    }

    /** A named mapping of variables to terms */
    internal data class SubstitutionCase(
        val id: String,
        val map: Map<Var, Term>,
    ) {
        val pairs: List<Pair<Var, Term>> get() = map.entries.map { it.toPair() }
    }

    /** Contains mixed groundSubstitutions and nonGroundSubstitutions */
    internal val mixedSubstitutionCases by lazy {
        ktListOf(
            SubstitutionCase("variable-to-atom", mapOf(Var.of("X") to Atom.of("x"))),
            SubstitutionCase(
                "two-variables-to-ground-terms",
                mapOf(
                    Var.of("A") to Struct.of("f", Atom.of("ciao")),
                    Var.of("B") to Empty.list(),
                ),
            ),
            SubstitutionCase(
                "variable-to-non-ground-struct",
                mapOf(
                    Var.of("Var") to Struct.of("f", Var.of("A"), Var.of("B")),
                ),
            ),
            SubstitutionCase("variable-to-struct-containing-it", mapOf(Var.of("Z") to Struct.of("f", Var.of("Z")))),
            SubstitutionCase("anonymous-variable-to-variable", mapOf(Var.anonymous() to Var.of("A"))),
        )
    }

    /** A named list of variable-term pairs */
    internal data class PairsCase(
        val id: String,
        val pairs: List<Pair<Var, Term>>,
    )

    /** Contains duplicated pair substitutions, that should result in unique final substitution */
    internal val duplicatedPairCases by lazy {
        Scope.empty {
            ktListOf(
                PairsCase("one-duplicated-pair", ktListOf(varOf("A") to atomOf("a"), varOf("A") to atomOf("a"))),
                PairsCase(
                    "two-duplicated-pairs",
                    ktListOf(
                        varOf("A") to atomOf("a"),
                        varOf("A") to atomOf("a"),
                        varOf("B") to atomOf("b"),
                        varOf("B") to atomOf("b"),
                    ),
                ),
            )
        }
    }

    /** Contains contradicting substitutions whose creation should result in [Substitution.Fail] */
    internal val contradictingPairCases by lazy {
        Scope.empty {
            ktListOf(
                PairsCase("two-atoms", ktListOf(varOf("A") to atomOf("a"), varOf("A") to atomOf("b"))),
                PairsCase("atom-and-variable", ktListOf(varOf("A") to atomOf("a"), varOf("A") to varOf("C"))),
                PairsCase(
                    "atom-and-variable-bound-to-same-atom",
                    ktListOf(varOf("A") to atomOf("a"), varOf("A") to varOf("C"), varOf("C") to atomOf("a")),
                ),
                PairsCase(
                    "atom-and-variable-bound-to-other-atom",
                    ktListOf(varOf("A") to atomOf("a"), varOf("A") to varOf("C"), varOf("C") to atomOf("b")),
                ),
            )
        }
    }
}
