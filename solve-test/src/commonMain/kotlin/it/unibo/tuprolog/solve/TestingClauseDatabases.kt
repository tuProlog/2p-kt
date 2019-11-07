package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import kotlin.collections.listOf as ktListOf

/**
 * An object containing a collection of notable databases to be used when testing Solver functionality
 *
 * @author Enrico
 */
object TestingClauseDatabases {

    /**
     * A database containing the following facts:
     * ```prolog
     * f(a).
     * g(a).
     * g(b).
     * h(a).
     * h(b).
     * h(c).
     * ```
     */
    val simpleFactDatabase by lazy {
        prolog {
            theory(
                    { "f"("a") },
                    { "g"("a") },
                    { "g"("b") },
                    { "h"("a") },
                    { "h"("b") },
                    { "h"("c") }
            )
        }
    }

    /** Notable [simpleFactDatabase] request goals and respective expected [Solution]s */
    val simpleFactDatabaseNotableGoalToSolutions by lazy { // TODO: 07/11/2019 use this in testing
        prolog {
            ktListOf(
                    "f"("A").run {
                        to(ktListOf(
                                yesSolution(Substitution.of(varOf("A"), atomOf("a")))
                        ))
                    },
                    "g"("A").run {
                        to(ktListOf(
                                yesSolution(Substitution.of(varOf("A"), atomOf("a"))),
                                yesSolution(Substitution.of(varOf("A"), atomOf("b")))
                        ))
                    },
                    "h"("A").run {
                        to(ktListOf(
                                yesSolution(Substitution.of(varOf("A"), atomOf("a"))),
                                yesSolution(Substitution.of(varOf("A"), atomOf("b"))),
                                yesSolution(Substitution.of(varOf("A"), atomOf("c")))
                        ))
                    }
            )
        }
    }

}

/** Utility function to help writing tests; it creates a [Solution.Yes] with receiver query and provided substitution */
fun Struct.yesSolution(withSubstitution: Substitution.Unifier = Substitution.empty()) = Solution.Yes(this, withSubstitution)

/** Utility function to help writing tests; it creates a [Solution.No] with receiver query */
fun Struct.noSolution() = Solution.No(this)

/** Utility function to help writing tests; it creates a [Solution.Halt] with receiver query and provided exception */
fun Struct.haltSolution(withException: TuPrologRuntimeException) = Solution.Halt(this, withException)
