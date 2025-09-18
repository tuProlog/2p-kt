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
    /** Returns terms that internally can have multiple times the provided [internalTerm] */
    internal fun termsWith(internalTerm: Term) =
        ktListOf(
            internalTerm,
            Struct.of("f", internalTerm, internalTerm),
            Struct.fold("f", internalTerm, Atom.of("ciao"), internalTerm),
        )

    /** Contains mixed groundSubstitutions and nonGroundSubstitutions */
    internal val mixedSubstitutions by lazy {
        ktListOf(
            mapOf(Var.of("X") to Atom.of("x")),
            mapOf(
                Var.of("A") to Struct.of("f", Atom.of("ciao")),
                Var.of("B") to Empty.list(),
            ),
            mapOf(Var.of("Var") to Struct.of("f", Var.of("A"), Var.of("B"))),
            mapOf(Var.of("Z") to Struct.of("f", Var.of("Z"))),
            mapOf(Var.anonymous() to Var.of("A")),
        )
    }

    /** Contains [mixedSubstitutions] represented as list of pairs */
    internal val mixedSubstitutionsAsPairs by lazy {
        mixedSubstitutions.map {
            it.entries.map { entry ->
                entry.toPair()
            }
        }
    }

    /** Contains a duplicated pair substitution, that should result in unique final substitution */
    internal val duplicatedPairSubstitution by lazy {
        Scope.empty {
            ktListOf(
                ktListOf(varOf("A") to atomOf("a"), varOf("A") to atomOf("a")),
                ktListOf(
                    varOf("A") to atomOf("a"),
                    varOf("A") to atomOf("a"),
                    varOf("B") to atomOf("b"),
                    varOf("B") to atomOf("b"),
                ),
            )
        }
    }

    /** Contains contradicting substitutions whose creation should result in [Substitution.Fail] */
    internal val contradictingSubstitutions by lazy {
        Scope.empty {
            ktListOf(
                ktListOf(varOf("A") to atomOf("a"), varOf("A") to atomOf("b")),
                ktListOf(varOf("A") to atomOf("a"), varOf("A") to varOf("C")),
                ktListOf(varOf("A") to atomOf("a"), varOf("A") to varOf("C"), varOf("C") to atomOf("a")),
                ktListOf(varOf("A") to atomOf("a"), varOf("A") to varOf("C"), varOf("C") to atomOf("b")),
            )
        }
    }
}
