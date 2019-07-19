package it.unibo.tuprolog.theory.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.assertTrue

/**
 * Utils singleton for testing [ClauseDatabase]
 *
 * @author Enrico
 */
internal object ClauseDatabaseUtils {

    /** Contains well formed clauses that will need to be rewritten, because they contain variables in body top level */
    internal val toBeRewrittenWellFormedClauses by lazy {
        listOf(
                Clause.of(Atom.of("a"), Var.of("A"), Var.of("A")),
                Clause.of(Atom.of("a"), Var.anonymous()),
                Rule.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
                Rule.of(Struct.of("f", Atom.of("a")), Var.of("Variable"))
        )
    }

    /** Contains well formed clauses (the head is a [Struct] and the body doesn't contain [Numeric] values) */
    internal val wellFormedClauses by lazy {
        listOf(
                Clause.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())),
                Clause.of(Struct.of("p", Atom.of("john"))),
                Directive.of(Atom.of("execute_this")),
                Rule.of(Struct.of("f", Atom.of("a")), Atom.of("do_something")),
                Rule.of(Struct.of("f", Atom.of("a"), Struct.of("b", Var.of("X")), Atom.of("do_something_else"))),
                Fact.of(Struct.of("g", Struct.of("c", Var.anonymous(), Var.anonymous()))),
                with(Scope.empty()) {
                    ruleOf(structOf("g", structOf("c", varOf("A"), varOf("B"))), varOf("A"))
                },
                Fact.of(Struct.of("g", Struct.of("c", Var.anonymous(), Var.anonymous())))
        ) + toBeRewrittenWellFormedClauses
    }

    /** Contains a pair which has in its first element half clauses from [wellFormedClauses] in the second element the other half */
    internal val wellFormedClausesHelves by lazy {
        wellFormedClauses.withIndex().partition { it.index % 2 == 0 }.run {
            Pair(first.map { it.value }, second.map { it.value })
        }
    }

    /** Contains not well formed clauses (with [Numeric] values in body) */
    internal val notWellFormedClauses by lazy {
        listOf(
                Clause.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous()), Integer.of(1)),
                Directive.of(Atom.of("execute_this"), Real.of(1.5)),
                Rule.of(Struct.of("f", Atom.of("a")), Atom.of("do_something"), Numeric.of(1.5f))
        )
    }

    /** Asserts that the two collections contain the same elements */
    internal fun <E> assertContentsEquals(expected: Collection<E>, actual: Collection<E>) {
        assertTrue(expected.containsAll(actual))
        assertTrue(actual.containsAll(expected))
    }

    // TODO enable after solving Issue #29, deleting above method
//    /** Asserts that the two collections contain the same elements */
//    internal fun <E : Comparable<E>> assertContentsEquals(expected: Collection<E>, actual: Collection<E>) {
//        assertEquals(expected.sorted(), actual.sorted())
//    }
}
