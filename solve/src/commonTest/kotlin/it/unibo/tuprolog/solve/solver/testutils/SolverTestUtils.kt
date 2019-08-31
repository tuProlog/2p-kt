package it.unibo.tuprolog.solve.solver.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.primitiveimpl.Cut
import it.unibo.tuprolog.solve.solver.Solver
import it.unibo.tuprolog.solve.testutils.DummyInstances
import it.unibo.tuprolog.theory.ClauseDatabase

/**
 * Utils singleton for helping test [Solver] behaviour
 *
 * @author Enrico
 */
internal object SolverTestUtils {

    /** A signature specifying a term like `f(_)` */
    internal val fSignature = Signature("f", 1)
    /** A signature specifying a term like `h(_)` */
    internal val hSignature = Signature("h", 1)
    /** List of single variable named `A` to use as arguments */
    internal val oneArityVarArgumentList = listOf(Var.of("A"))

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
    internal val factDatabase = ClauseDatabase.of(
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("g", Atom.of("a"))),
            Fact.of(Struct.of("g", Atom.of("b"))),
            Fact.of(Struct.of("h", Atom.of("a"))),
            Fact.of(Struct.of("h", Atom.of("b"))),
            Fact.of(Struct.of("h", Atom.of("c")))
    )

    /**
     * A database containing the following facts:
     * ```prolog
     * f(only) :- !.
     * g(only) :- !.
     * h(only) :- !.
     * f(a).
     * g(a).
     * g(b).
     * h(a).
     * h(b).
     * h(c).
     * ```
     */
    internal val databaseWithCutAlternatives = ClauseDatabase.of(
            Rule.of(Struct.of("f", Atom.of("only")), Atom.of("!")),
            Rule.of(Struct.of("g", Atom.of("only")), Atom.of("!")),
            Rule.of(Struct.of("h", Atom.of("only")), Atom.of("!"))
    ) + factDatabase

    /** Request for solving `?- f(A)` against [factDatabase]; should result in substitution `A\a` */
    internal val oneResponseRequest = Solve.Request(
            fSignature,
            oneArityVarArgumentList,
            executionContextWithLibraryDatabase(factDatabase)
    )

    /** Request for solving `?- h(A)` against [factDatabase]; should result in substitution `A\a, A\b, A\c` */
    internal val threeResponseRequest = Solve.Request(
            hSignature,
            oneArityVarArgumentList,
            executionContextWithLibraryDatabase(factDatabase)
    )

    /** Request for solving `?- f(A)` against [databaseWithCutAlternatives]; should result in substitution `A\only` */
    internal val oneResponseBecauseOfCut = Solve.Request(
            fSignature,
            oneArityVarArgumentList,
            executionContextWithLibraryDatabase(databaseWithCutAlternatives)
    )

    /** Utility function to create an ExecutionContext with provided [clauseDatabase] as a library theory */
    private fun executionContextWithLibraryDatabase(clauseDatabase: ClauseDatabase) =
            DummyInstances.executionContext.copy(
                    libraries = Libraries(Library.of(
                            alias = "aTest",
                            theory = clauseDatabase,
                            primitives = mapOf(Cut.signature to Cut.primitive)
                    ))
            )

}
