package it.unibo.tuprolog.collections.rete.nodes.custom

import it.unibo.tuprolog.core.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal object ReteTreeAssertionUtils {
    fun <T> assertItemsAreEquals(expected: Iterator<T>, actual: Iterator<T>) {
        TODO()
    }

    fun <T> assertItemsAreEquals(expected: Iterable<T>, actual: Iterable<T>) {
        return assertItemsAreEquals(expected.iterator(), actual.iterator())
    }

    fun <T> assertItemsAreEquals(expected: Sequence<T>, actual: Sequence<T>) {
        return assertItemsAreEquals(expected.iterator(), actual.iterator())
    }

    fun <T> assertPartialOrderIsTheSame(expected: Iterator<T>, actual: Iterator<T>) {
        TODO()
    }

    fun <T> assertPartialOrderIsTheSame(expected: Iterable<T>, actual: Iterable<T>) {
        return assertPartialOrderIsTheSame(expected.iterator(), actual.iterator())
    }

    fun <T> assertPartialOrderIsTheSame(expected: Sequence<T>, actual: Sequence<T>) {
        return assertPartialOrderIsTheSame(expected.iterator(), actual.iterator())
    }

    fun assertIsEmptyAndOrdered(tree: ReteTree) {
        assertTrue(tree.isOrdered)
        assertIsEmpty(tree)
    }

    fun assertIsEmptyAndUnordered(tree: ReteTree) {
        assertTrue(tree.isOrdered)
        assertIsEmpty(tree)
    }

    fun assertIsEmpty(tree: ReteTree) {
        assertEquals(0, tree.size)
        assertItemsAreEquals(emptySequence(), tree.clauses)
    }

    /** Contains some well-formed facts */
    val facts = listOf(
        Fact.of(Truth.TRUE),
        Fact.of(Truth.FAIL),
        Fact.of(Atom.of("a")),
        Fact.of(Atom.of("other")),
        Fact.of(Struct.of("a", Atom.of("other"))),
        Fact.of(Struct.of("other", Integer.of(1))),
        Fact.of(Tuple.of(Var.of("A"), Var.of("B"))),
        Fact.of(Atom.of("a")),
        Fact.of(Struct.of("other", Integer.of(2))),
        Fact.of(Tuple.of(Var.of("B"), Var.of("A"))),
        Fact.of(Struct.of("other", Integer.of(1)))
    )

    /** Contains some well-formed rules */
    val rules = facts + listOf(
        Rule.of(Atom.of("a"), Atom.of("other")),
        Rule.of(Tuple.of(Var.of("A"), Var.of("B")), Atom.of("a")),
        Rule.of(Struct.of("a", Atom.of("other")), Atom.of("a")),
        Rule.of(Struct.of("f", Atom.of("a"), Struct.of("b", Var.of("X")), Atom.of("do_something_else"))),
        Rule.of(Struct.of("a", Integer.of(22)), Var.anonymous()),
        Rule.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
        Rule.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
        Rule.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())),
        Rule.of(Struct.of("a", Atom.of("a")), Empty.set()),
        Rule.of(Struct.of("a", Atom.of("a")), Struct.of("other", Var.anonymous())),
        Rule.of(Struct.of("a", Atom.of("a")), Struct.of("a", Var.anonymous())),
        Rule.of(Struct.of("a", Atom.of("a")), Var.anonymous())
    )

    /** Contains some well-formed rules with no args head */
    val atomRules = rules.filter { it.head.isAtom }

    /** Contains some well-formed rules with "a" functor */
    val aFunctorRules = rules.filter { it.head.functor == "a" }

    /** Contains some well-formed rules with "other" functor and 1 arity */
    val other1FunctorRules = rules.filter { it.head.functor == "other" && it.head.arity == 1 }

    /** Contains some well-formed rules with "other" functor and 2 arity */
    val dot2FunctorRules = rules.filter { it.head.functor == "." && it.head.arity == 2 }

    /** Contains some well-formed rules with "f" functor */
    val fFunctorRules = rules.filter { it.head.functor == "f" }

    /** Contains some well-formed directives */
    val directives =
        listOf(
            Directive.of(Truth.TRUE),
            Directive.of(Truth.FAIL),
            Directive.of(Atom.of("a")),
            Directive.of(Atom.of("other")),
            Directive.of(Struct.of("a", Atom.of("other"))),
            Directive.of(Struct.of("other", Integer.of(1))),
            Directive.of(Atom.of("a"), Atom.of("other")),
            Directive.of(Struct.of("a", Atom.of("other")), Atom.of("a")),
            Directive.of(Struct.of("f", Atom.of("a"), Struct.of("b", Var.of("X")), Atom.of("do_something_else"))),
            Directive.of(Struct.of("a", Integer.of(22)), Var.anonymous()),
            Directive.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
            Directive.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
            Directive.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())),
            Directive.of(Struct.of("a", Atom.of("a")), Empty.set()),
            Directive.of(Struct.of("a", Atom.of("a")), Struct.of("other", Var.anonymous())),
            Directive.of(Struct.of("a", Atom.of("a")), Struct.of("a", Var.anonymous())),
            Directive.of(Struct.of("a", Atom.of("a")), Var.anonymous())
        )

    /** Contains well-formed mixed [rules] and [directives] */
    val clauses = rules + directives
}