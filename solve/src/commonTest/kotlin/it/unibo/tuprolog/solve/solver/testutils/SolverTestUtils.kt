package it.unibo.tuprolog.solve.solver.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.primitiveimpl.Conjunction
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

    /** Request for solving `?- f(A)` against [factDatabase]; should result in substitution `A\a` */
    internal val oneResponseRequest = createSolveRequest(Struct.of("f", Var.of("A")), factDatabase)
    /** Request for solving `?- h(A)` against [factDatabase]; should result in substitution `A\a, A\b, A\c` */
    internal val threeResponseRequest = createSolveRequest(Struct.of("h", Var.of("A")), factDatabase)

    /**
     * A database containing the following rules:
     * ```prolog
     * f(only) :- !.
     * g(only) :- !.
     * f(a).
     * g(a).
     * g(b).
     * h(a).
     * h(only) :- !.
     * h(b).
     * h(c).
     * ```
     */
    internal val databaseWithCutAlternatives = ClauseDatabase.of(listOf(
            Rule.of(Struct.of("f", Atom.of("only")), Atom.of("!")),
            Rule.of(Struct.of("g", Atom.of("only")), Atom.of("!"))
    ) + factDatabase.clauses.toList().dropLast(2) +
            Rule.of(Struct.of("h", Atom.of("only")), Atom.of("!")) +
            factDatabase.clauses.drop(4))

    /** Request for solving `?- f(A)` against [databaseWithCutAlternatives]; should result in substitution `A\only` */
    internal val oneResponseBecauseOfCut = createSolveRequest(
            Struct.of("f", Var.of("A")),
            databaseWithCutAlternatives,
            mapOf(Cut.descriptionPair)
    )
    /** Request for solving `?- h(A)` against [databaseWithCutAlternatives]; should result in substitution `A/a, A\only` */
    internal val twoResponseBecauseOfCut = createSolveRequest(
            Struct.of("h", Var.of("A")),
            databaseWithCutAlternatives,
            mapOf(Cut.descriptionPair)
    )

    /**
     * A database containing the following rules:
     * ```prolog
     * f(X, Y) :- q(X), !, r(Y).
     * q(a).
     * q(b).
     * r(a1).
     * r(b1).
     * ```
     */
    internal val databaseWithCutAndConjunction = ClauseDatabase.of(
            Scope.empty().run {
                ruleOf(structOf("f", varOf("X"), varOf("Y")),
                        structOf("q", varOf("X")),
                        atomOf("!"),
                        structOf("r", varOf("Y"))
                )
            },
            Fact.of(Struct.of("q", Atom.of("a"))),
            Fact.of(Struct.of("q", Atom.of("b"))),
            Fact.of(Struct.of("r", Atom.of("a1"))),
            Fact.of(Struct.of("r", Atom.of("b1")))
    )

    /** Request for solving `?- f(A, B)` against [databaseWithCutAndConjunction]; should result in substitution `(A/a, B/a1) and  (A\a, B/b1)` */
    internal val twoResponseOnConjunctionAndCutDatabase = createSolveRequest(
            Struct.of("f", Var.of("A"), Var.of("B")),
            databaseWithCutAndConjunction,
            mapOf(Cut.descriptionPair, Conjunction.descriptionPair)
    )

    /** Creates a Solve.Request with provided goal, against provided database, loading given primitives */
    internal fun createSolveRequest(query: Struct, database: ClauseDatabase = ClauseDatabase.of(), primitives: Map<Signature, Primitive> = mapOf()) = Solve.Request(
            Signature.fromIndicator(query.indicator)!!,
            query.argsList,
            DummyInstances.executionContext.copy(libraries = Libraries(Library.of(
                    alias = "Test",
                    theory = database,
                    primitives = primitives
            )))
    )
}
