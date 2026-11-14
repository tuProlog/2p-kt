package it.unibo.tuprolog.theory

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Compound
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator

private class RetrievalKind(
    val id: String,
    val of: (List<Clause>) -> Theory,
)

private val retrievalKinds =
    listOf(
        RetrievalKind("frozen") { Theory.indexedOf(Unificator.default, it) },
        RetrievalKind("mutable") { MutableTheory.indexedOf(Unificator.default, it) },
    )

private fun status(arg: Int) = Fact.of(Struct.of("status", Integer.of(arg)))

private fun metarule(guard: String) =
    Var.of("P").let { p ->
        Var.of("X").let { x -> Rule.of(Pattern.of(p, x), Struct.of(guard, p, x)) }
    }

/** More clauses than the threshold above which procedures index their arguments, interleaving metarules. */
private fun fixture(): List<Clause> =
    (1..10).map(::status) + metarule("cached") + Fact.of(Struct.of("other", Atom.of("z"))) + (11..20).map(::status) +
        metarule("extra") + status(5) + Fact.of(Struct.of("pair", Atom.of("a"), Atom.of("b"))) +
        Fact.of(Struct.of("cached", Atom.of("status"), Atom.of("a")))

private val queries: List<() -> Compound> =
    listOf(
        { Struct.of("status", Var.of("X")) },
        { Struct.of("status", Integer.of(5)) },
        { Struct.of("status", Integer.of(42)) },
        { Struct.of("other", Var.of("X")) },
        { Struct.of("unrelated", Var.of("X")) },
        { Pattern.of(Var.of("F"), Var.of("X")) },
        { Pattern.of(Var.of("F"), Integer.of(5)) },
        { Pattern.of(Var.of("F"), Atom.of("z")) },
        { Pattern.of(Var.of("F"), Var.of("X"), Var.of("Y")) },
        { Pattern.of(Var.of("F"), Atom.of("status"), Var.of("Y")) },
    )

/** The clauses of [this] theory whose heads unify with [head], found by scanning them all, in order. */
private fun Theory.scan(head: Compound): List<Clause> {
    val query = Rule.of(head, Var.anonymous())
    return clauses.filter { Unificator.default.match(query, it) }
}

private fun Theory.lookup(head: Compound): List<Clause> = get(Rule.of(head, Var.anonymous())).toList()

/** The positions of [found] among the clauses of this theory, by identity: duplicates are told apart. */
private fun Theory.positionsOf(found: List<Clause>): List<Int> {
    val all = clauses.toList()
    return found.map { clause -> all.indexOfFirst { it === clause } }
}

/** Every query finds, through the indexes, the very clauses that scanning finds, in the same order. */
private fun Theory.shouldAgreeWithScans() {
    for (query in queries) {
        val head = query()
        withClue("$head") { positionsOf(lookup(head)) shouldBe positionsOf(scan(head)) }
    }
}

/**
 * Indexed theories retrieve clauses by concrete heads and by patterns as scanning them would: same clauses, same order,
 * same duplicates, before and after their indexes are built, and after updates (MR-48 to MR-53).
 */
class MetaRuleRetrievalSpec :
    FunSpec({
        withTests(nameFn = { "mr-50-51/indexes-agree-with-scans/${it.id}" }, retrievalKinds) { kind ->
            val theory = kind.of(fixture())
            // the first lookups build the indexes the second ones use
            repeat(2) { theory.shouldAgreeWithScans() }
        }

        withTests(nameFn = { "mr-48-49/declaration-order/${it.id}" }, retrievalKinds) { kind ->
            val clauses = fixture()
            val found = kind.of(clauses).lookup(Struct.of("status", Integer.of(5)))
            found.map { clause -> clauses.indexOfFirst { it === clause } } shouldBe listOf(4, 10, 22, 23)
        }

        withTests(nameFn = { "mr-52/updates-agree-with-scans/${it.id}" }, retrievalKinds) { kind ->
            var theory = kind.of(fixture())
            theory.shouldAgreeWithScans()
            theory = theory.assertA(metarule("first")).assertZ(status(5)).assertZ(metarule("last"))
            theory.shouldAgreeWithScans()
            theory = theory.retract(Struct.of("status", Integer.of(5))).theory
            theory.shouldAgreeWithScans()
            theory = theory.retract(Rule.of(Pattern.of(Var.of("P"), Var.of("X")), Var.of("B"))).theory
            theory.shouldAgreeWithScans()
            theory = theory.abolish(Indicator.of("status", 1))
            theory.shouldAgreeWithScans()
        }

        test("mr-53/frozen-and-mutable-theories-agree") {
            val (frozen, mutable) = retrievalKinds.map { it.of(fixture()) }
            for (query in queries) {
                val head = query()
                withClue("$head") {
                    mutable.positionsOf(mutable.lookup(head)) shouldBe
                        frozen.positionsOf(frozen.lookup(head))
                }
            }
        }
    })
