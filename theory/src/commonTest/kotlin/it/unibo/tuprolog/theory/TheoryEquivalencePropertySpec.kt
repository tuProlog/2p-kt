package it.unibo.tuprolog.theory

import io.kotest.assertions.withClue
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.choose
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.AbstractWorklistUnificator
import it.unibo.tuprolog.unify.UnificationStep
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.setTags

/*
 * Every theory implementation must behave like a plain list of clauses (#1019): same clauses in the same order, same
 * lookup results in the same order (`candidates` being an ordered superset of them), same retracted clauses, with tags preserved, for any sequence of operations and
 * for unificators with different notions of equality (strict, naive, and one that also compares tags).
 */

@OptIn(ExperimentalKotest::class)
private val config = PropTestConfig(seed = 1019L, iterations = 150)

/** One operation on a theory; [observe] returns what it lets callers see besides the theory itself. */
private sealed interface Op {
    data class AssertA(
        val clause: Clause,
    ) : Op

    data class AssertZ(
        val clause: Clause,
    ) : Op

    data class Get(
        val query: Clause,
    ) : Op

    /** Observes `candidates`, filtered by unification: it must find the same clauses as `get`. */
    data class Candidates(
        val query: Clause,
    ) : Op

    data class Retract(
        val query: Clause,
    ) : Op

    data class RetractMany(
        val queries: List<Clause>,
    ) : Op

    data class RetractAll(
        val query: Clause,
    ) : Op

    data class Abolish(
        val indicator: Indicator,
    ) : Op
}

/** The reference model: a list scanned in order with the theory's unificator. */
private class Oracle(
    private val unificator: Unificator,
) {
    val clauses = mutableListOf<Clause>()

    private fun matches(query: Clause) = clauses.filter { unificator.match(query, it) }

    private fun retractFirst(query: Clause): Clause? =
        clauses.indexOfFirst { unificator.match(query, it) }.takeIf { it >= 0 }?.let { clauses.removeAt(it) }

    private fun retractAll(query: Clause): List<Clause>? =
        matches(query).takeIf { it.isNotEmpty() }?.also { removed ->
            val iterator = clauses.iterator()
            var next = 0
            while (iterator.hasNext() && next < removed.size) {
                if (iterator.next() === removed[next]) {
                    iterator.remove()
                    next++
                }
            }
        }

    fun apply(op: Op): List<Clause>? =
        when (op) {
            is Op.AssertA -> null.also { clauses.add(0, op.clause) }
            is Op.AssertZ -> null.also { clauses.add(op.clause) }
            is Op.Get -> matches(op.query)
            is Op.Candidates -> matches(op.query)
            is Op.Retract -> retractFirst(op.query)?.let { listOf(it) }
            is Op.RetractMany -> op.queries.mapNotNull { retractFirst(it) }.takeIf { it.isNotEmpty() }
            is Op.RetractAll -> retractAll(op.query)
            is Op.Abolish -> null.also { retractAll(abolishQuery(op.indicator)) }
        }
}

private fun abolishQuery(indicator: Indicator): Clause =
    Rule.of(Struct.template(indicator.indicatedName!!, indicator.indicatedArity!!), Var.anonymous())

private fun Theory.apply(op: Op): Pair<Theory, List<Clause>?> =
    when (op) {
        is Op.AssertA -> assertA(op.clause) to null
        is Op.AssertZ -> assertZ(op.clause) to null
        is Op.Get -> this to get(op.query).toList()
        is Op.Candidates ->
            this to
                candidates(op.query).toList().also { shouldBeInOrder(it) }.filter { unificator.match(op.query, it) }
        is Op.Retract -> retract(op.query).let { it.theory to it.clauses?.toList() }
        is Op.RetractMany -> retract(op.queries).let { it.theory to it.clauses?.toList() }
        is Op.RetractAll -> retractAll(op.query).let { it.theory to it.clauses?.toList() }
        is Op.Abolish -> abolish(op.indicator) to null
    }

/** Checks that [candidates] are clauses of this theory, in the theory's order. */
private fun Theory.shouldBeInOrder(candidates: List<Clause>) {
    val remaining = clauses.iterator()
    for (candidate in candidates) {
        withClue("candidate $candidate, in order") {
            remaining.asSequence().any { it === candidate } shouldBe true
        }
    }
}

/** Clauses together with their tags, as clause equality ignores tags. */
private fun Iterable<Clause>.withTags(): List<Pair<Clause, Map<String, Any>>> = map { it to it.tags }

private class ImplCase(
    val id: String,
    val build: (Unificator) -> Theory,
)

private class UnificatorCase(
    val id: String,
    val unificator: Unificator,
)

private val impls =
    listOf(
        ImplCase("indexed") { Theory.indexedOf(it, emptyList()) },
        ImplCase("mutable-indexed") { MutableTheory.indexedOf(it, emptyList()) },
    )

/** Restricts the default unification: two non-variable terms with different tags do not unify. */
private object TagSensitiveUnificator : AbstractWorklistUnificator(Substitution.empty()) {
    override fun handleComparison(step: UnificationStep) {
        if (!step.lhs.isVar && !step.rhs.isVar && step.lhs.tags != step.rhs.tags) step.reject()
    }
}

private val unificators =
    listOf(
        UnificatorCase("strict", Unificator.strict()),
        UnificatorCase("naive", Unificator.naive()),
        UnificatorCase("tag-sensitive", TagSensitiveUnificator),
    )

private val tags = mapOf("source" to "spec")

private fun <T : Term> Arb<T>.sometimesTagged(): Arb<T> =
    arbitrary { this@sometimesTagged.bind().let { if (Arb.int(0..4).bind() == 0) it.setTags(tags) else it } }

private val constants: Arb<Term> =
    Arb.element(Atom.of("a"), Atom.of("b"), Integer.of(1), Real.of(1.0), Integer.of(2))

private fun arguments(depth: Int): Arb<Term> =
    if (depth == 0) {
        Arb.choose(3 to constants, 1 to arbitrary { Var.of("X") }).sometimesTagged()
    } else {
        Arb
            .choose(
                3 to constants,
                2 to arbitrary { Var.of("X") },
                1 to arbitrary { Struct.of("f", arguments(depth - 1).bind()) },
                1 to arbitrary { Struct.of("g", arguments(depth - 1).bind(), arguments(depth - 1).bind()) },
            ).sometimesTagged()
    }

private val signatures = listOf("p" to 0, "p" to 1, "p" to 2, "q" to 2)

private val heads: Arb<Struct> =
    arbitrary {
        val (name, arity) = Arb.element(signatures).bind()
        if (arity == 0) Atom.of(name) else Struct.of(name, List(arity) { arguments(2).bind() })
    }

private val clauses: Arb<Clause> =
    Arb
        .choose(
            8 to
                arbitrary {
                    val body = if (Arb.boolean().bind()) Truth.TRUE else Struct.of("q", Var.of("X"), Var.of("Y"))
                    Rule.of(heads.bind(), body)
                },
            1 to arbitrary { Directive.of(Struct.of("d", arguments(1).bind())) },
        ).sometimesTagged()

private val queries: Arb<Clause> =
    Arb.choose(
        2 to clauses,
        3 to arbitrary { Rule.of(heads.bind(), Var.anonymous()) },
        1 to arbitrary { Directive.of(Var.anonymous()) },
    )

private val ops: Arb<Op> =
    Arb.choose(
        4 to arbitrary { Op.AssertZ(clauses.bind()) },
        2 to arbitrary { Op.AssertA(clauses.bind()) },
        3 to arbitrary { Op.Get(queries.bind()) },
        2 to arbitrary { Op.Candidates(queries.bind()) },
        2 to arbitrary { Op.Retract(queries.bind()) },
        1 to arbitrary { Op.RetractMany(Arb.list(queries, 1..3).bind()) },
        1 to arbitrary { Op.RetractAll(queries.bind()) },
        1 to arbitrary { Op.Abolish(Arb.element(signatures).bind().let { (n, a) -> Indicator.of(n, a) }) },
    )

private fun checkEquivalence(
    impl: ImplCase,
    unificator: Unificator,
    operations: List<Op>,
) {
    val oracle = Oracle(unificator)
    var theory = impl.build(unificator)
    operations.forEachIndexed { i, op ->
        val expected = oracle.apply(op)
        val (next, actual) = theory.apply(op)
        theory = next
        withClue("step $i: $op") {
            actual?.withTags() shouldBe expected?.withTags()
            theory.clauses.withTags() shouldBe oracle.clauses.withTags()
            theory.size shouldBe oracle.clauses.size.toLong()
        }
    }
}

private class RegressionCase(
    val id: String,
    val operations: () -> List<Op>,
)

/** Hand-picked sequences kept beside the property. */
private val regressions =
    listOf(
        RegressionCase("assert-a-before-z") {
            listOf(
                Op.AssertZ(Rule.of(Struct.of("p", Atom.of("a")), Truth.TRUE)),
                Op.AssertA(Rule.of(Struct.of("p", Var.of("X")), Truth.TRUE)),
                Op.Get(Rule.of(Struct.of("p", Atom.of("a")), Var.anonymous())),
                Op.Retract(Rule.of(Struct.of("p", Atom.of("a")), Var.anonymous())),
            )
        },
        RegressionCase("duplicates") {
            val clause = Rule.of(Struct.of("p", Atom.of("a")), Truth.TRUE)
            listOf(Op.AssertZ(clause), Op.AssertZ(clause), Op.Retract(clause), Op.Get(clause), Op.RetractAll(clause))
        },
        RegressionCase("integer-vs-real") {
            listOf(
                Op.AssertZ(Rule.of(Struct.of("p", Integer.of(1)), Truth.TRUE)),
                Op.AssertZ(Rule.of(Struct.of("p", Real.of(1.0)), Truth.TRUE)),
                Op.Get(Rule.of(Struct.of("p", Integer.of(1)), Var.anonymous())),
                Op.Get(Rule.of(Struct.of("p", Real.of(1.0)), Var.anonymous())),
            )
        },
        // enough clauses for argument indexes to kick in, with keys the naive unificator must not tell apart
        RegressionCase("indexed-integer-vs-real") {
            List(10) { Op.AssertZ(Rule.of(Struct.of("p", Integer.of(it), Atom.of("a")), Truth.TRUE)) } +
                listOf(
                    Op.AssertZ(Rule.of(Struct.of("p", Real.of(1.0), Var.of("X")), Truth.TRUE)),
                    Op.Get(Rule.of(Struct.of("p", Integer.of(1), Var.anonymous()), Var.anonymous())),
                    Op.Get(Rule.of(Struct.of("p", Real.of(1.0), Atom.of("a")), Var.anonymous())),
                    Op.Candidates(Rule.of(Struct.of("p", Real.of(1.0), Atom.of("a")), Var.anonymous())),
                    Op.Retract(Rule.of(Struct.of("p", Var.anonymous(), Atom.of("a")), Var.anonymous())),
                    Op.RetractAll(Rule.of(Struct.of("p", Integer.of(1), Var.anonymous()), Var.anonymous())),
                    Op.Get(Rule.of(Struct.of("p", Var.anonymous(), Var.anonymous()), Var.anonymous())),
                )
        },
        // nested-argument indexes: deep keys, variables and non-compounds at every level, other functors and arities
        RegressionCase("indexed-nested-arguments") {
            fun f(t: Term) = Struct.of("f", t)

            fun g(t: Term) = Struct.of("g", t)

            fun p(t: Term) = Rule.of(Struct.of("p", t, Atom.of("a")), Truth.TRUE)

            fun q(t: Term) = Rule.of(Struct.of("p", t, Var.anonymous()), Var.anonymous())
            List(10) { Op.AssertZ(p(f(g(Integer.of(it))))) } +
                listOf(
                    Op.AssertZ(p(f(Var.of("X")))),
                    Op.AssertZ(p(Var.of("X"))),
                    Op.AssertA(p(f(Atom.of("b")))),
                    Op.AssertZ(p(Struct.of("f", g(Integer.of(3)), Atom.of("c")))),
                    Op.AssertZ(p(Struct.of("h", g(Integer.of(3))))),
                    Op.AssertZ(p(f(g(Var.of("Y"))))),
                    Op.Get(q(f(g(Integer.of(3))))),
                    Op.Candidates(q(f(g(Integer.of(3))))),
                    Op.Get(q(f(g(Real.of(3.0))))),
                    Op.Candidates(q(f(g(Real.of(3.0))))),
                    Op.Get(q(f(Var.of("Z")))),
                    Op.Get(q(f(g(Struct.of("k", Atom.of("c")))))),
                    Op.Retract(q(f(g(Integer.of(3))))),
                    Op.RetractAll(q(f(g(Integer.of(5))))),
                    Op.Get(q(f(g(Integer.of(5))))),
                    Op.AssertA(p(f(g(Integer.of(5))))),
                    Op.Get(q(f(g(Integer.of(5))))),
                )
        },
        // deep indexes on any argument of compound arguments (here, the second one of g/2), mixed with other shapes
        RegressionCase("indexed-deep-arguments") {
            fun g(
                a: Term,
                b: Term,
            ) = Struct.of("g", a, b)

            fun p(t: Term) = Rule.of(Struct.of("p", t, Atom.of("a")), Truth.TRUE)

            fun q(t: Term) = Rule.of(Struct.of("p", t, Var.anonymous()), Var.anonymous())
            List(10) { Op.AssertZ(p(g(Atom.of("k"), Struct.of("f", Integer.of(it))))) } +
                listOf(
                    Op.AssertZ(p(g(Var.of("X"), Struct.of("f", Integer.of(3))))),
                    Op.AssertZ(p(g(Atom.of("k"), Var.of("Y")))),
                    Op.AssertA(p(Struct.of("g", Integer.of(3)))),
                    Op.AssertZ(p(Var.of("Z"))),
                    Op.AssertZ(p(g(Atom.of("k"), Struct.of("f", Real.of(3.0))))),
                    Op.Get(q(g(Var.anonymous(), Struct.of("f", Integer.of(3))))),
                    Op.Candidates(q(g(Var.anonymous(), Struct.of("f", Integer.of(3))))),
                    Op.Get(q(g(Atom.of("k"), Struct.of("f", Real.of(3.0))))),
                    Op.Get(q(g(Atom.of("j"), Struct.of("f", Integer.of(4))))),
                    Op.Retract(q(g(Var.anonymous(), Struct.of("f", Integer.of(5))))),
                    Op.AssertZ(p(g(Atom.of("k"), Struct.of("h", Integer.of(5))))),
                    Op.Get(q(g(Var.anonymous(), Struct.of("f", Integer.of(5))))),
                    Op.RetractAll(q(g(Atom.of("k"), Var.anonymous()))),
                    Op.Get(q(g(Var.anonymous(), Struct.of("f", Integer.of(3))))),
                )
        },
        RegressionCase("abolish-keeps-other-arities") {
            listOf(
                Op.AssertZ(Rule.of(Atom.of("p"), Truth.TRUE)),
                Op.AssertZ(Rule.of(Struct.of("p", Atom.of("a")), Truth.TRUE)),
                Op.Abolish(Indicator.of("p", 1)),
            )
        },
        RegressionCase("tagged-clauses") {
            listOf(
                Op.AssertZ(Rule.of(Struct.of("p", Atom.of("a").setTags(tags)), Truth.TRUE)),
                Op.AssertZ(Rule.of(Struct.of("p", Atom.of("a")), Truth.TRUE).setTags(tags)),
                Op.Get(Rule.of(Struct.of("p", Atom.of("a")), Var.anonymous())),
                Op.Get(Rule.of(Struct.of("p", Atom.of("a").setTags(tags)), Var.anonymous())),
            )
        },
    )

class TheoryEquivalencePropertySpec :
    FunSpec({
        for (impl in impls) {
            withTests(nameFn = { "${impl.id}/${it.id}/random-operations" }, unificators) { unificator ->
                checkAll(config, Arb.list(ops, 1..60)) { checkEquivalence(impl, unificator.unificator, it) }
            }
            for (unificator in unificators) {
                withTests(nameFn = { "${impl.id}/${unificator.id}/regressions/${it.id}" }, regressions) { case ->
                    checkEquivalence(impl, unificator.unificator, case.operations())
                }
            }
        }
    })
