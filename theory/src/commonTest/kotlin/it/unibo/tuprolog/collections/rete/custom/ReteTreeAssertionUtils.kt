package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.utils.interleave
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.collections.List as KtList

internal object ReteTreeAssertionUtils {
    fun <T> assertItemsAreEquals(expected: KtList<T>, actual: KtList<T>) {
        assertEquals(expected, actual)
    }

    fun <T> assertItemsAreEquals(expected: Iterable<T>, actual: Iterable<T>) {
        return assertItemsAreEquals(
            expected.toList(),
            actual.toList()
        )
    }

    fun <T> assertItemsAreEquals(expected: Sequence<T>, actual: Sequence<T>) {
        return assertItemsAreEquals(
            expected.toList(),
            actual.toList()
        )
    }

    fun <T> assertPartialOrderIsTheSame(expected: Iterator<T>, actual: Iterator<T>) {
        while (expected.hasNext()) {
            val e = expected.next()
            while (actual.hasNext()) {
                val a = actual.next()
                if (e == a) break
            }
            if (!actual.hasNext() && expected.hasNext()) {
                fail("Item $e is out of sequence")
            }
        }
    }

    fun <T> assertPartialOrderIsTheSame(expected: Iterable<T>, actual: Iterable<T>) {
        return assertPartialOrderIsTheSame(
            expected.iterator(),
            actual.iterator()
        )
    }

    fun <T> assertPartialOrderIsTheSame(expected: Sequence<T>, actual: Sequence<T>) {
        return assertPartialOrderIsTheSame(
            expected.iterator(),
            actual.iterator()
        )
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
        assertItemsAreEquals(
            emptySequence(),
            tree.clauses
        )
        assertEquals(0, tree.size)
    }

    fun assertIsNonEmpty(tree: ReteTree) {
        val size = tree.size
        assertTrue { size > 0 }
        assertEquals(tree.size, tree.clauses.count())
    }

    private fun Struct.addArgument(term: Term): Struct =
        Struct.of(functor, *args, term)

    private fun Fact.addArgument(term: Term): Fact =
        Fact.of(head.addArgument(term))

    private fun Fact.addBody(term: Term): Rule =
        Rule.of(head, term)

    private fun KtList<Fact>.addArguments(args: KtList<Term>): KtList<Fact> {
        return zip(args.shuffled()).map { (f, a) -> f.addArgument(a) }
    }

    private fun KtList<Fact>.addBodies(bodies: KtList<Term>): KtList<Rule> {
        return asSequence().flatMap { f ->
            bodies.asSequence().map { b -> f.addBody(b)  }
        }.toList()
    }

    private val moreArguments = listOf(
        Atom.of("a"),
        Integer.of(2),
        Real.of(3.4),
        Var.of("Five"),
        Struct.of("six", Atom.of("seven"), Integer.of(8), Real.of(9.10), Var.of("Eleven"))
    )

    val simpleFacts = listOf("a", "b", "c", "d")
        .map(Atom.Companion::of)
        .map(Fact.Companion::of)

    val f1Facts = listOf(
        Struct.of("f", Integer.of(1)),
        Struct.of("f", Integer.of(2)),
        Struct.of("f", Integer.of(3)),
        Struct.of("f", Integer.of(4)),
        Struct.of("f", Real.of(1.0)),
        Struct.of("f", Real.of(2.0)),
        Struct.of("f", Real.of(3.0)),
        Struct.of("f", Real.of(4.0))
    ).map(Fact.Companion::of)

    val g1Facts = listOf(
        Struct.of("g", Atom.of("a")),
        Struct.of("g", Atom.of("b")),
        Struct.of("g", Atom.of("c")),
        Struct.of("g", Atom.of("d"))
    ).map(Fact.Companion::of)

    val h1Facts = listOf(
        Struct.of("h", Var.of("A")),
        Struct.of("h", Var.of("B")),
        Struct.of("h", Var.of("C")),
        Struct.of("h", Var.of("D"))
    ).map(Fact.Companion::of)

    val i1Facts = f1Facts.map { Fact.of(Struct.of("i", it.head)) }

    val j1Facts = g1Facts.map { Fact.of(Struct.of("j", it.head)) }

    val l1Facts = h1Facts.map { Fact.of(Struct.of("l", it.head)) }

    val m1Facts = i1Facts.map { Fact.of(Struct.of("m", it.head)) }

    val n1Facts = j1Facts.map { Fact.of(Struct.of("n", it.head)) }

    val o1Facts = l1Facts.map { Fact.of(Struct.of("o", it.head)) }

    val f2Facts = f1Facts.addArguments(
        moreArguments
    )

    val g2Facts = g1Facts.addArguments(
        moreArguments
    )

    val h2Facts = h1Facts.addArguments(
        moreArguments
    )

    val i2Facts = i1Facts.addArguments(
        moreArguments
    )

    val l2Facts = l1Facts.addArguments(
        moreArguments
    )

    val m2Facts = m1Facts.addArguments(
        moreArguments
    )

    val n2Facts = n1Facts.addArguments(
        moreArguments
    )

    val o2Facts = o1Facts.addArguments(
        moreArguments
    )

    val otherFacts = listOf(
        Fact.of(Truth.TRUE),
        Fact.of(Truth.FAIL),
//        Fact.of(Atom.of("a")),
        Fact.of(Atom.of("other")),
        Fact.of(Struct.of("a", Atom.of("other"))),
        Fact.of(Struct.of("other", Integer.of(1))),
        Fact.of(Tuple.of(Var.of("A"), Var.of("B"))),
//        Fact.of(Atom.of("a")),
        Fact.of(Struct.of("other", Integer.of(2))),
        Fact.of(Tuple.of(Var.of("B"), Var.of("A"))),
        Fact.of(Struct.of("other", Integer.of(1)))
    )

    val facts = interleave(
        simpleFacts,
        f1Facts,
        g1Facts,
        h1Facts,
        i1Facts,
        j1Facts,
        l1Facts,
        m1Facts,
        n1Facts,
        o1Facts,
        f2Facts,
        g2Facts,
        h2Facts,
        i2Facts,
        l2Facts,
        m2Facts,
        n2Facts,
        o2Facts,
        otherFacts
    ).toList()

    val simpleRules = simpleFacts.addBodies(moreArguments)
    val f1Rules = f1Facts.addBodies(moreArguments)
    val g1Rules = g1Facts.addBodies(moreArguments)
    val h1Rules = h1Facts.addBodies(moreArguments)
    val i1Rules = i1Facts.addBodies(moreArguments)
    val j1Rules = j1Facts.addBodies(moreArguments)
    val l1Rules = l1Facts.addBodies(moreArguments)
    val m1Rules = m1Facts.addBodies(moreArguments)
    val n1Rules = n1Facts.addBodies(moreArguments)
    val o1Rules = o1Facts.addBodies(moreArguments)
    val f2Rules = f2Facts.addBodies(moreArguments)
    val g2Rules = g2Facts.addBodies(moreArguments)
    val h2Rules = h2Facts.addBodies(moreArguments)
    val i2Rules = i2Facts.addBodies(moreArguments)
    val l2Rules = l2Facts.addBodies(moreArguments)
    val m2Rules = m2Facts.addBodies(moreArguments)
    val n2Rules = n2Facts.addBodies(moreArguments)
    val o2Rules = o2Facts.addBodies(moreArguments)

    val otherRules = listOf(
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

    val simpleFactsAndRules = simpleFacts + simpleRules
    val f1FactsAndRules = f1Facts + f1Rules
    val g1FactsAndRules = g1Facts + g1Rules
    val h1FactsAndRules = h1Facts + h1Rules
    val i1FactsAndRules = i1Facts + i1Rules
    val j1FactsAndRules = j1Facts + j1Rules
    val l1FactsAndRules = l1Facts + l1Rules
    val m1FactsAndRules = m1Facts + m1Rules
    val n1FactsAndRules = n1Facts + n1Rules
    val o1FactsAndRules = o1Facts + o1Rules
    val f2FactsAndRules = f2Facts + f2Rules
    val g2FactsAndRules = g2Facts + g2Rules
    val h2FactsAndRules = h2Facts + h2Rules
    val i2FactsAndRules = i2Facts + i2Rules
    val l2FactsAndRules = l2Facts + l2Rules
    val m2FactsAndRules = m2Facts + m2Rules
    val n2FactsAndRules = n2Facts + n2Rules
    val o2FactsAndRules = o2Facts + o2Rules

    val rules = interleave(
        simpleRules,
        f1Rules,
        g1Rules,
        h1Rules,
        i1Rules,
        j1Rules,
        l1Rules,
        m1Rules,
        n1Rules,
        o1Rules,
        f2Rules,
        g2Rules,
        h2Rules,
        i2Rules,
        l2Rules,
        m2Rules,
        n2Rules,
        o2Rules,
        otherRules
    ).toList()

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

    val clauses = interleave(facts + rules, directives).toList()
}