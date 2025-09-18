package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.utils.interleave
import it.unibo.tuprolog.utils.interleaveSequences
import it.unibo.tuprolog.utils.subsequences
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail
import it.unibo.tuprolog.core.List.Companion as LogicList
import kotlin.collections.List as KtList
import kotlin.collections.Set as KtSet

internal object ReteTreeAssertionUtils {
    fun <T> assertItemsAreEquals(
        expected: KtList<T>,
        actual: KtList<T>,
        message: (() -> String)? = null,
    ) {
        assertEquals(expected, actual, message?.invoke())
    }

    fun <T> assertItemMultisetsAreEqual(
        expected: KtSet<T>,
        actual: KtSet<T>,
        message: (() -> String)? = null,
    ) {
        assertEquals(expected, actual, message?.invoke())
    }

    fun <T> assertItemsAreEquals(
        expected: Iterable<T>,
        actual: Iterable<T>,
        message: (() -> String)? = null,
    ) = assertItemsAreEquals(
        expected.toList(),
        actual.toList(),
        message,
    )

    fun <T> assertItemMultisetsAreEqual(
        expected: Iterable<T>,
        actual: Iterable<T>,
        message: (() -> String)? = null,
    ) {
        assertEquals(expected.count(), actual.count())
        return assertItemMultisetsAreEqual(
            expected.toSet(),
            actual.toSet(),
            message,
        )
    }

    fun <T> assertItemsAreEquals(
        expected: Sequence<T>,
        actual: Sequence<T>,
        message: (() -> String)? = null,
    ) = assertItemsAreEquals(
        expected.toList(),
        actual.toList(),
        message,
    )

    fun <T> assertItemMultisetsAreEqual(
        expected: Sequence<T>,
        actual: Sequence<T>,
        message: (() -> String)? = null,
    ) {
        assertEquals(expected.count(), actual.count())
        return assertItemMultisetsAreEqual(
            expected.toSet(),
            actual.toSet(),
            message,
        )
    }

    fun <T> assertPartialOrderIsTheSame(
        expected: Iterator<T>,
        actual: Iterator<T>,
        message: ((T) -> String)? = null,
    ) {
        while (expected.hasNext()) {
            val e = expected.next()
            while (actual.hasNext()) {
                val a = actual.next()
                if (e == a) break
            }
            if (!actual.hasNext() && expected.hasNext()) {
                fail(message?.invoke(e) ?: "Item $e is out of sequence")
            }
        }
    }

    fun <T> assertPartialOrderIsTheSame(
        expected: Iterable<T>,
        actual: Iterable<T>,
        message: ((T) -> String)? = null,
    ) = assertPartialOrderIsTheSame(
        expected.iterator(),
        actual.iterator(),
        message,
    )

    fun <T> assertPartialOrderIsTheSame(
        expected: Sequence<T>,
        actual: Sequence<T>,
        message: ((T) -> String)? = null,
    ) = assertPartialOrderIsTheSame(
        expected.iterator(),
        actual.iterator(),
        message,
    )

    fun <T> assertSubMultisetOf(
        expected: MutableList<T>,
        actual: MutableList<T>,
        message: ((T) -> String)? = null,
    ) {
        val i = expected.listIterator()
        while (i.hasNext()) {
            val e = i.next()
            val j = actual.listIterator()
            while (j.hasNext()) {
                val a = j.next()
                if (a == e) {
                    i.remove()
                    j.remove()
                    break
                }
            }
            if (!j.hasNext() && i.hasNext()) {
                fail(message?.invoke(e) ?: "Item $e is not present")
            }
        }
    }

    fun <T> assertSubMultisetOf(
        expected: Sequence<T>,
        actual: Sequence<T>,
        message: ((T) -> String)? = null,
    ) = assertSubMultisetOf(
        expected.toMutableList(),
        actual.toMutableList(),
        message,
    )

    fun <T> assertSubMultisetOf(
        expected: Iterable<T>,
        actual: Iterable<T>,
        message: ((T) -> String)? = null,
    ) = assertSubMultisetOf(
        expected.toMutableList(),
        actual.toMutableList(),
        message,
    )

    fun <T> assertNotContainedIn(
        contained: KtList<T>,
        container: KtList<T>,
        message: ((T) -> String)? = null,
    ) {
        for (clause in contained) {
            if (container.any { it == clause }) {
                fail(message?.invoke(clause) ?: "Item $clause is already present")
            }
        }
    }

    fun <T> assertNotContainedIn(
        contained: Sequence<T>,
        container: Sequence<T>,
        message: ((T) -> String)? = null,
    ) = assertNotContainedIn(
        contained.toList(),
        container.toList(),
        message,
    )

    fun <T> assertNotContainedIn(
        contained: Iterable<T>,
        container: Iterable<T>,
        message: ((T) -> String)? = null,
    ) = assertNotContainedIn(
        contained.toList(),
        container.toList(),
        message,
    )

    fun assertIsEmptyAndOrdered(tree: ReteTree) {
        assertTrue(tree.isOrdered)
        assertIsEmpty(tree)
    }

    fun assertIsEmptyAndUnordered(tree: ReteTree) {
        assertFalse(tree.isOrdered)
        assertIsEmpty(tree)
    }

    fun assertIsEmpty(tree: ReteTree) {
        assertItemsAreEquals(
            emptySequence(),
            tree.clauses,
        )
        assertEquals(0, tree.size)
    }

    fun assertIsNonEmpty(tree: ReteTree) {
        val size = tree.size
        assertTrue { size > 0 }
        assertEquals(tree.size, tree.clauses.count())
    }

    fun assertMatches(
        expected: Term,
        actual: Term,
    ) {
        assertTrue("$actual should match $expected") {
            expected matches actual
        }
    }

    fun assertDoesNotMatch(
        expected: Term,
        actual: Term,
    ) {
        assertFalse("$actual should not match $expected") {
            expected matches actual
        }
    }

    private fun Struct.addArguments(
        term: Term,
        vararg terms: Term,
    ): Struct = Struct.of(functor, *args.toTypedArray(), term, *terms)

    private fun Struct.addArguments(terms: Iterable<Term>): Struct = Struct.of(functor, args + terms)

    private fun Fact.addArguments(
        term: Term,
        vararg terms: Term,
    ): Fact = Fact.of(head.addArguments(term, *terms))

    private fun Fact.addArguments(terms: Iterable<Term>): Fact = Fact.of(head.addArguments(terms))

    private fun Fact.addBody(term: Term): Rule = Rule.of(head, term)

    private fun KtList<Fact>.addArgumentsRandomlyFrom(args: KtList<Term>): KtList<Fact> =
        zip(args.shuffled()).map { (f, a) -> f.addArguments(a) }

    private fun KtList<Fact>.addBodyFrom(bodies: KtList<Term>): KtList<Rule> =
        asSequence()
            .flatMap { f ->
                bodies.asSequence().map { b -> f.addBody(b) }
            }.toList()

    fun <T> Iterable<T>.allChunksOfSize(size: Int): KtList<KtList<T>> =
        if (size == 0) {
            listOf(emptyList())
        } else {
            chunked(size).filter { it.size == size }
        }

    private val moreArguments =
        listOf(
            Atom.of("a"),
            Integer.of(2),
            Real.of(3.4),
            Var.of("Five"),
            Struct.of("six", Atom.of("seven"), Integer.of(8), Real.of(9.10), Var.of("Eleven")),
        )

    val simpleFacts =
        listOf("a", "b", "c", "d")
            .map(Atom.Companion::of)
            .map(Fact.Companion::of)

    val f1Facts =
        listOf(
            Struct.of("f", Integer.of(1)),
            Struct.of("f", Integer.of(2)),
            Struct.of("f", Integer.of(3)),
            Struct.of("f", Integer.of(4)),
            Struct.of("f", Real.of(1.0)),
            Struct.of("f", Real.of(2.0)),
            Struct.of("f", Real.of(3.0)),
            Struct.of("f", Real.of(4.0)),
        ).map(Fact.Companion::of)

    val g1Facts =
        listOf(
            Struct.of("g", Atom.of("a")),
            Struct.of("g", Atom.of("b")),
            Struct.of("g", Atom.of("c")),
            Struct.of("g", Atom.of("d")),
        ).map(Fact.Companion::of)

    val h1Facts =
        listOf(
            Struct.of("h", Var.of("A")),
            Struct.of("h", Var.of("B")),
            Struct.of("h", Var.of("C")),
            Struct.of("h", Var.of("D")),
        ).map(Fact.Companion::of)

    val i1Facts = f1Facts.map { Fact.of(Struct.of("i", it.head)) }

    val j1Facts = g1Facts.map { Fact.of(Struct.of("j", it.head)) }

    val l1Facts = h1Facts.map { Fact.of(Struct.of("l", it.head)) }

    val m1Facts = i1Facts.map { Fact.of(Struct.of("m", it.head)) }

    val n1Facts = j1Facts.map { Fact.of(Struct.of("n", it.head)) }

    val o1Facts = l1Facts.map { Fact.of(Struct.of("o", it.head)) }

    val f2Facts = f1Facts.addArgumentsRandomlyFrom(moreArguments)

    val g2Facts = g1Facts.addArgumentsRandomlyFrom(moreArguments)

    val h2Facts = h1Facts.addArgumentsRandomlyFrom(moreArguments)

    val i2Facts = i1Facts.addArgumentsRandomlyFrom(moreArguments)

    val l2Facts = l1Facts.addArgumentsRandomlyFrom(moreArguments)

    val m2Facts = m1Facts.addArgumentsRandomlyFrom(moreArguments)

    val n2Facts = n1Facts.addArgumentsRandomlyFrom(moreArguments)

    val o2Facts = o1Facts.addArgumentsRandomlyFrom(moreArguments)

    val higherArityFacts =
        interleaveSequences(
            sequenceOf(f2Facts, g2Facts, h2Facts, i2Facts, l2Facts, m2Facts, n2Facts, o2Facts)
                .flatMap { it.asSequence() }
                .map { fact ->
                    moreArguments.subsequences().map { fact.addArguments(it.toList()) }
                },
        ).toList()

    val otherFacts =
        listOf(
            Truth.TRUE,
            Truth.FAIL,
            Truth.FALSE,
            Empty.block(),
            Empty.list(),
            Atom.of("!"),
            Atom.of("1"),
            Atom.of(""),
            Atom.of("a b c"),
            Atom.of("A"),
            LogicList.of(Atom.of("a")),
            LogicList.of(Atom.of("a"), Atom.of("b")),
            LogicList.of(Atom.of("a"), Atom.of("b"), Atom.of("c")),
            LogicList.from(Atom.of("a"), last = Var.anonymous()),
            LogicList.from(Atom.of("a"), Atom.of("b"), last = Var.anonymous()),
            LogicList.from(Atom.of("a"), Atom.of("b"), Atom.of("c"), last = Var.anonymous()),
            Tuple.of(Atom.of("a"), Atom.of("b")),
            Tuple.of(Atom.of("a"), Atom.of("b"), Atom.of("c")),
            Block.of(Atom.of("a")),
            Block.of(Atom.of("a"), Atom.of("b")),
            Block.of(Atom.of("a"), Atom.of("b"), Atom.of("c")),
        ).map(Fact.Companion::of)

    val facts =
        interleave(
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
            otherFacts,
            higherArityFacts,
        ).toList()

    val factFamilies =
        mapOf(
            Fact.template("a", 0) to facts.filter { it.head.functor == "a" && it.head.arity == 0 },
            Fact.template("b", 0) to facts.filter { it.head.functor == "b" && it.head.arity == 0 },
            Fact.template("c", 0) to facts.filter { it.head.functor == "c" && it.head.arity == 0 },
            Fact.template("d", 0) to facts.filter { it.head.functor == "d" && it.head.arity == 0 },
            Fact.template("f", 1) to facts.filter { it.head.functor == "f" && it.head.arity == 1 },
            Fact.template("g", 1) to facts.filter { it.head.functor == "g" && it.head.arity == 1 },
            Fact.template("h", 1) to facts.filter { it.head.functor == "h" && it.head.arity == 1 },
            Fact.template("i", 1) to facts.filter { it.head.functor == "i" && it.head.arity == 1 },
            Fact.template("j", 1) to facts.filter { it.head.functor == "j" && it.head.arity == 1 },
            Fact.template("l", 1) to facts.filter { it.head.functor == "l" && it.head.arity == 1 },
            Fact.template("m", 1) to facts.filter { it.head.functor == "m" && it.head.arity == 1 },
            Fact.template("n", 1) to facts.filter { it.head.functor == "n" && it.head.arity == 1 },
            Fact.template("o", 1) to facts.filter { it.head.functor == "o" && it.head.arity == 1 },
            Fact.template("f", 2) to facts.filter { it.head.functor == "f" && it.head.arity == 2 },
            Fact.template("g", 2) to facts.filter { it.head.functor == "g" && it.head.arity == 2 },
            Fact.template("h", 2) to facts.filter { it.head.functor == "h" && it.head.arity == 2 },
            Fact.template("i", 2) to facts.filter { it.head.functor == "i" && it.head.arity == 2 },
            Fact.template("l", 2) to facts.filter { it.head.functor == "l" && it.head.arity == 2 },
            Fact.template("m", 2) to facts.filter { it.head.functor == "m" && it.head.arity == 2 },
            Fact.template("n", 2) to facts.filter { it.head.functor == "n" && it.head.arity == 2 },
            Fact.template("o", 2) to facts.filter { it.head.functor == "o" && it.head.arity == 2 },
        )

    val simpleRules = simpleFacts.addBodyFrom(moreArguments)

    val a0Rules = moreArguments.shuffled().map { Rule.of(Atom.of("a"), it) }

    val b0Rules = moreArguments.shuffled().map { Rule.of(Atom.of("b"), it) }

    val c0Rules = moreArguments.shuffled().map { Rule.of(Atom.of("c"), it) }

    val d0Rules = moreArguments.shuffled().map { Rule.of(Atom.of("d"), it) }
    val f1Rules = f1Facts.addBodyFrom(moreArguments)
    val g1Rules = g1Facts.addBodyFrom(moreArguments)
    val h1Rules = h1Facts.addBodyFrom(moreArguments)
    val i1Rules = i1Facts.addBodyFrom(moreArguments)
    val j1Rules = j1Facts.addBodyFrom(moreArguments)
    val l1Rules = l1Facts.addBodyFrom(moreArguments)
    val m1Rules = m1Facts.addBodyFrom(moreArguments)
    val n1Rules = n1Facts.addBodyFrom(moreArguments)
    val o1Rules = o1Facts.addBodyFrom(moreArguments)
    val f2Rules = f2Facts.addBodyFrom(moreArguments)
    val g2Rules = g2Facts.addBodyFrom(moreArguments)
    val h2Rules = h2Facts.addBodyFrom(moreArguments)
    val i2Rules = i2Facts.addBodyFrom(moreArguments)
    val l2Rules = l2Facts.addBodyFrom(moreArguments)
    val m2Rules = m2Facts.addBodyFrom(moreArguments)
    val n2Rules = n2Facts.addBodyFrom(moreArguments)
    val o2Rules = o2Facts.addBodyFrom(moreArguments)
    val otherRules = otherFacts.addBodyFrom(moreArguments)

    val higherArityRules = higherArityFacts.addBodyFrom(moreArguments)

    val rules =
        interleave(
            simpleRules,
            a0Rules,
            b0Rules,
            c0Rules,
            d0Rules,
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
            otherRules,
            higherArityRules,
        ).toList()

    val ruleFamilies =
        mapOf(
            Rule.template("a", 0) to rules.filter { it.head.functor == "a" && it.head.arity == 0 },
            Rule.template("b", 0) to rules.filter { it.head.functor == "b" && it.head.arity == 0 },
            Rule.template("c", 0) to rules.filter { it.head.functor == "c" && it.head.arity == 0 },
            Rule.template("d", 0) to rules.filter { it.head.functor == "d" && it.head.arity == 0 },
            Rule.template("f", 1) to rules.filter { it.head.functor == "f" && it.head.arity == 1 },
            Rule.template("g", 1) to rules.filter { it.head.functor == "g" && it.head.arity == 1 },
            Rule.template("h", 1) to rules.filter { it.head.functor == "h" && it.head.arity == 1 },
            Rule.template("i", 1) to rules.filter { it.head.functor == "i" && it.head.arity == 1 },
            Rule.template("j", 1) to rules.filter { it.head.functor == "j" && it.head.arity == 1 },
            Rule.template("l", 1) to rules.filter { it.head.functor == "l" && it.head.arity == 1 },
            Rule.template("m", 1) to rules.filter { it.head.functor == "m" && it.head.arity == 1 },
            Rule.template("n", 1) to rules.filter { it.head.functor == "n" && it.head.arity == 1 },
            Rule.template("o", 1) to rules.filter { it.head.functor == "o" && it.head.arity == 1 },
            Rule.template("f", 2) to rules.filter { it.head.functor == "f" && it.head.arity == 2 },
            Rule.template("g", 2) to rules.filter { it.head.functor == "g" && it.head.arity == 2 },
            Rule.template("h", 2) to rules.filter { it.head.functor == "h" && it.head.arity == 2 },
            Rule.template("i", 2) to rules.filter { it.head.functor == "i" && it.head.arity == 2 },
            Rule.template("l", 2) to rules.filter { it.head.functor == "l" && it.head.arity == 2 },
            Rule.template("m", 2) to rules.filter { it.head.functor == "m" && it.head.arity == 2 },
            Rule.template("n", 2) to rules.filter { it.head.functor == "n" && it.head.arity == 2 },
            Rule.template("o", 2) to rules.filter { it.head.functor == "o" && it.head.arity == 2 },
        )

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
            Directive.of(Struct.of("a", Atom.of("a")), Empty.block()),
            Directive.of(Struct.of("a", Atom.of("a")), Struct.of("other", Var.anonymous())),
            Directive.of(Struct.of("a", Atom.of("a")), Struct.of("a", Var.anonymous())),
            Directive.of(Struct.of("a", Atom.of("a")), Var.anonymous()),
        )

    val defaultClauses = interleave(facts + rules, directives).toList()

    val factsAndRules = defaultClauses.filterIsInstance<Rule>()

    val a0FactsAndRules = factsAndRules.filter { it.head.functor == "a" && it.head.arity == 0 }
    val b0FactsAndRules = factsAndRules.filter { it.head.functor == "b" && it.head.arity == 0 }
    val c0FactsAndRules = factsAndRules.filter { it.head.functor == "c" && it.head.arity == 0 }
    val d0FactsAndRules = factsAndRules.filter { it.head.functor == "d" && it.head.arity == 0 }
    val f1FactsAndRules = factsAndRules.filter { it.head.functor == "f" && it.head.arity == 1 }
    val g1FactsAndRules = factsAndRules.filter { it.head.functor == "g" && it.head.arity == 1 }
    val h1FactsAndRules = factsAndRules.filter { it.head.functor == "h" && it.head.arity == 1 }
    val i1FactsAndRules = factsAndRules.filter { it.head.functor == "i" && it.head.arity == 1 }
    val j1FactsAndRules = factsAndRules.filter { it.head.functor == "j" && it.head.arity == 1 }
    val l1FactsAndRules = factsAndRules.filter { it.head.functor == "l" && it.head.arity == 1 }
    val m1FactsAndRules = factsAndRules.filter { it.head.functor == "m" && it.head.arity == 1 }
    val n1FactsAndRules = factsAndRules.filter { it.head.functor == "n" && it.head.arity == 1 }
    val o1FactsAndRules = factsAndRules.filter { it.head.functor == "o" && it.head.arity == 1 }
    val f2FactsAndRules = factsAndRules.filter { it.head.functor == "f" && it.head.arity == 2 }
    val g2FactsAndRules = factsAndRules.filter { it.head.functor == "g" && it.head.arity == 2 }
    val h2FactsAndRules = factsAndRules.filter { it.head.functor == "h" && it.head.arity == 2 }
    val i2FactsAndRules = factsAndRules.filter { it.head.functor == "i" && it.head.arity == 2 }
    val l2FactsAndRules = factsAndRules.filter { it.head.functor == "l" && it.head.arity == 2 }
    val m2FactsAndRules = factsAndRules.filter { it.head.functor == "m" && it.head.arity == 2 }
    val n2FactsAndRules = factsAndRules.filter { it.head.functor == "n" && it.head.arity == 2 }
    val o2FactsAndRules = factsAndRules.filter { it.head.functor == "o" && it.head.arity == 2 }

    val factsAndRulesFamilies =
        mapOf(
            Rule.template("a", 0) to a0FactsAndRules,
            Rule.template("b", 0) to b0FactsAndRules,
            Rule.template("c", 0) to c0FactsAndRules,
            Rule.template("d", 0) to d0FactsAndRules,
            Rule.template("f", 1) to f1FactsAndRules,
            Rule.template("g", 1) to g1FactsAndRules,
            Rule.template("h", 1) to h1FactsAndRules,
            Rule.template("i", 1) to i1FactsAndRules,
            Rule.template("j", 1) to j1FactsAndRules,
            Rule.template("l", 1) to l1FactsAndRules,
            Rule.template("m", 1) to m1FactsAndRules,
            Rule.template("n", 1) to n1FactsAndRules,
            Rule.template("o", 1) to o1FactsAndRules,
            Rule.template("f", 2) to f2FactsAndRules,
            Rule.template("g", 2) to g2FactsAndRules,
            Rule.template("h", 2) to h2FactsAndRules,
            Rule.template("i", 2) to i2FactsAndRules,
            Rule.template("l", 2) to l2FactsAndRules,
            Rule.template("m", 2) to m2FactsAndRules,
            Rule.template("n", 2) to n2FactsAndRules,
            Rule.template("o", 2) to o2FactsAndRules,
        )
}
