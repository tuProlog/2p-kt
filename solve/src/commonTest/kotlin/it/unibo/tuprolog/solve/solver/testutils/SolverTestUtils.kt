package it.unibo.tuprolog.solve.solver.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.primitiveimpl.Conjunction
import it.unibo.tuprolog.solve.primitiveimpl.Cut
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
     * f(a).
     * h(a).
     * h(only) :- !.
     * h(b).
     * g(A) :- e(A).
     * g(A) :- d(A).
     * e(a) :- !.
     * e(b).
     * d(c).
     * d(d).
     * ```
     */
    internal val databaseWithCutAlternatives = ClauseDatabase.of(listOf(
            Rule.of(Struct.of("f", Atom.of("only")), Atom.of("!")),
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("h", Atom.of("a"))),
            Rule.of(Struct.of("h", Atom.of("only")), Atom.of("!")),
            Fact.of(Struct.of("h", Atom.of("b"))),
            Scope.empty { ruleOf(structOf("g", varOf("A")), structOf("e", varOf("A"))) },
            Scope.empty { ruleOf(structOf("g", varOf("A")), structOf("d", varOf("A"))) },
            Rule.of(Struct.of("e", Atom.of("a")), Atom.of("!")),
            Fact.of(Struct.of("e", Atom.of("b"))),
            Fact.of(Struct.of("d", Atom.of("c"))),
            Fact.of(Struct.of("d", Atom.of("d")))
    ))

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

    /** Request for solving `?- g(V)` against [databaseWithCutAlternatives]; should result in substitution `V/a, V\c, V/d` */
    internal val threeResponseBecauseOfCut = createSolveRequest(
            Struct.of("g", Var.of("V")),
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
    internal val databaseWithMiddleCutAndConjunction = Scope.empty {
        ClauseDatabase.of(
                ruleOf(structOf("f", varOf("X"), varOf("Y")),
                        structOf("q", varOf("X")),
                        atomOf("!"),
                        structOf("r", varOf("Y"))
                ),
                factOf(structOf("q", atomOf("a"))),
                factOf(structOf("q", atomOf("b"))),
                factOf(structOf("r", atomOf("a1"))),
                factOf(structOf("r", atomOf("b1")))
        )
    }

    /** Request for solving `?- f(A, B)` against [databaseWithMiddleCutAndConjunction]; should result in substitution `(A/a, B/a1) and (A\a, B/b1)` */
    internal val twoResponseOnConjunctionAndMiddleCutDatabase = createSolveRequest(
            Struct.of("f", Var.of("A"), Var.of("B")),
            databaseWithMiddleCutAndConjunction,
            mapOf(Cut.descriptionPair, Conjunction.descriptionPair)
    )

    /**
     * A database containing the following rules:
     * ```prolog
     * a(X) :- b(X).
     * a(6).
     * b(X) :- c(X), d(X).
     * b(4) :- !.
     * b(5).
     * c(1).
     * c(2) :- !.
     * c(3).
     * d(2).
     * d(3).
     * ```
     */
    internal val databaseWithCutAndConjunction = Scope.empty {
        ClauseDatabase.of(
                ruleOf(structOf("a", varOf("X")), structOf("b", varOf("X"))),
                factOf(structOf("a", numOf(6))),
                ruleOf(structOf("b", varOf("X")), tupleOf(structOf("c", varOf("X")), structOf("d", varOf("X")))),
                ruleOf(structOf("b", numOf(4)), atomOf("!")),
                factOf(structOf("b", numOf(5))),
                factOf(structOf("c", numOf(1))),
                ruleOf(structOf("c", numOf(2)), atomOf("!")),
                factOf(structOf("c", numOf(3))),
                factOf(structOf("d", numOf(2))),
                factOf(structOf("d", numOf(3)))
        )
    }

    /** Request for solving `?- a(X)` against [databaseWithCutAndConjunction]; should result in substitution `X/2, X/4, X/6` */
    internal val threeResponseOnCutAndConjunctionDatabase = createSolveRequest(
            Struct.of("a", Var.of("X")),
            databaseWithCutAndConjunction,
            mapOf(Cut.descriptionPair, Conjunction.descriptionPair)
    )

    /** Creates a Solve.Request with provided goal, against provided database, loading given primitives */
    internal fun createSolveRequest(query: Struct, database: ClauseDatabase = ClauseDatabase.empty(), primitives: Map<Signature, Primitive> = mapOf()) = Solve.Request(
            query.extractSignature(),
            query.argsList,
            query,
            DummyInstances.executionContext.copy(libraries = Libraries(Library.of(
                    alias = "Test",
                    theory = database,
                    primitives = primitives
            )))
    )
}
