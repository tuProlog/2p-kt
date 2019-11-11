package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.prologStandardExampleDatabase
import kotlin.collections.listOf as ktListOf

/**
 * An object containing the collection of Prolog Standard databases and requests, testing ISO functionality
 *
 * @author Enrico
 */
object PrologStandardExampleDatabases {

    /**
     * The clause database used in Prolog Standard reference manual, when explaining solver functionality and search-trees
     *
     * ```prolog
     * p(X, Y) :- q(X), r(X, Y).
     * p(X, Y) :- s(X).
     * s(d).
     * q(a).
     * q(b).
     * q(c).
     * r(b, b1).
     * r(c, c1).
     * ```
     */
    val prologStandardExampleDatabase by lazy {
        prolog {
            theory(
                    { "p"("X", "Y") `if` tupleOf("q"("X"), "r"("X", "Y")) },
                    { "p"("X", "Y") `if` "s"("X") },
                    { "s"("d") },
                    { "q"("a") },
                    { "q"("b") },
                    { "q"("c") },
                    { "r"("b", "b1") },
                    { "r"("c", "c1") }
            )
        }
    }

    /**
     * Notable [prologStandardExampleDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- p(U, V).
     * ```
     */
    val prologStandardExampleDatabaseNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                    "p"("U", "V").hasSolutions(
                            { yes(Substitution.of("U" to "b", "V" to "b1")) },
                            { yes(Substitution.of("U" to "c", "V" to "c1")) },
                            { yes(Substitution.of("U" to "d", "V" to "Y")) }
                    )
            )
        }
    }

    /**
     * Same as [prologStandardExampleDatabase] but first clause contains cut
     *
     * ```prolog
     * p(X, Y) :- q(X), !, r(X, Y).
     * p(X, Y) :- s(X).
     * s(d).
     * q(a).
     * q(b).
     * q(c).
     * r(b, b1).
     * r(c, c1).
     * ```
     */
    val prologStandardExampleWithCutDatabase by lazy {
        prolog {
            theory({ "p"("X", "Y") `if` tupleOf("q"("X"), "!", "r"("X", "Y")) }) +
                    theoryOf(*prologStandardExampleDatabase.clauses.drop(1).toTypedArray())
        }
    }

    /**
     * Notable [prologStandardExampleWithCutDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- p(U, V).
     * ```
     */
    val prologStandardExampleWithCutDatabaseNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                    "p"("U", "V").hasSolutions({ no() })
            )
        }
    }

    /**
     * The database used in Prolog standard while writing examples for Conjunction
     * ```prolog
     * legs(A, 6) :- insect(A).
     * legs(A, 4) :- animal(A).
     * insect(bee).
     * insect(ant).
     * fly(bee).
     * ```
     */
    val conjunctionStandardExampleDatabase by lazy {
        prolog {
            theory(
                    { "legs"("A", 6) `if` "insect"("A") },
                    { "legs"("A", 4) `if` "animal"("A") },
                    { "insect"("bee") },
                    { "insect"("ant") },
                    { "fly"("bee") }
            )
        }
    }

    /**
     * Notable [conjunctionStandardExampleDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- (insect(X) ; legs(X, 6)) , fly(X).
     * ```
     */
    val conjunctionStandardExampleDatabaseNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                    (("insect"("X") or "legs"("X", 6)) and "fly"("X")).hasSolutions(
                            { yes("X" to "bee") },
                            { yes("X" to "bee") },
                            { no() }
                    )
            )
        }
    }

    /** Collection of all Prolog Standard example databases and their respective callable goals with expected solutions */
    val allPrologStandardTestingDatabasesToRespectiveGoalsAndSolutions by lazy {
        mapOf(
                prologStandardExampleDatabase to prologStandardExampleDatabaseNotableGoalToSolution,
                prologStandardExampleWithCutDatabase to prologStandardExampleWithCutDatabaseNotableGoalToSolution,
                conjunctionStandardExampleDatabase to conjunctionStandardExampleDatabaseNotableGoalToSolution
        )
    }
}
