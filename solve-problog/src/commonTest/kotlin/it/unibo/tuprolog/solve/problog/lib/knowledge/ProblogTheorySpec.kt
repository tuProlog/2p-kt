package it.unibo.tuprolog.solve.problog.lib.knowledge

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

private fun p(n: Int) = Struct.of("p", Integer.of(n))

/** The heads of [theory]'s clauses, without the explanation argument the mapping adds. */
private fun heads(theory: Theory): List<String> =
    theory.clauses.map { clause -> clause.head!!.let { Struct.of(it.functor, it.args.dropLast(1)) }.toString() }

private class Kind(
    val id: String,
    val create: () -> ProblogTheory,
)

private val kinds =
    listOf(
        Kind("immutable") { ProblogTheory.of(Unificator.default, Fact.of(p(1))) },
        Kind("mutable") { MutableProblogTheory.of(Unificator.default, listOf(Fact.of(p(1)))) },
    )

private class Edit(
    val id: String,
    val apply: (ProblogTheory) -> Theory,
    val expected: List<String>,
)

private val edits =
    listOf(
        Edit("assert-a/struct", { it.assertA(p(0)) }, listOf("p(0)", "p(1)")),
        Edit("assert-a/clause", { it.assertA(Fact.of(p(0))) }, listOf("p(0)", "p(1)")),
        Edit("assert-a/iterable", { it.assertA(listOf(Fact.of(p(0)))) }, listOf("p(0)", "p(1)")),
        Edit("assert-a/sequence", { it.assertA(sequenceOf(Fact.of(p(0)))) }, listOf("p(0)", "p(1)")),
        Edit("assert-z/struct", { it.assertZ(p(2)) }, listOf("p(1)", "p(2)")),
        Edit("assert-z/clause", { it.assertZ(Fact.of(p(2))) }, listOf("p(1)", "p(2)")),
        Edit("assert-z/iterable", { it.assertZ(listOf(Fact.of(p(2)))) }, listOf("p(1)", "p(2)")),
        Edit("assert-z/sequence", { it.assertZ(sequenceOf(Fact.of(p(2)))) }, listOf("p(1)", "p(2)")),
        Edit("plus/clause", { it + Fact.of(p(2)) }, listOf("p(1)", "p(2)")),
        Edit("plus/plain-theory", { it + Theory.of(Fact.of(p(2))) }, listOf("p(1)", "p(2)")),
        Edit(
            "plus/problog-theory",
            { it + ProblogTheory.of(Unificator.default, Fact.of(p(2))) },
            listOf("p(1)", "p(2)"),
        ),
        Edit("abolish", { it.abolish(Indicator.of("p", 1)) }, emptyList()),
        Edit("retract/clause", { it.retract(Fact.of(p(1))).theory }, emptyList()),
        Edit("retract/sequence", { it.retract(sequenceOf(Fact.of(p(1)))).theory }, emptyList()),
        Edit("retract/head", { it.retract(p(1)).theory }, emptyList()),
        Edit("retract-all/head", { it.retractAll(Struct.of("p", Var.anonymous())).theory }, emptyList()),
    )

class ProblogTheorySpec :
    FunSpec({
        withTests(
            nameFn = { (kind, edit) -> "${kind.id}/${edit.id}" },
            kinds.flatMap { kind -> edits.map { kind to it } },
        ) { (kind, edit) ->
            assertEquals(edit.expected, heads(edit.apply(kind.create())))
        }

        withTests(nameFn = { "${it.id}/retract/missing-clause-fails" }, kinds) { kind ->
            assertIs<RetractResult.Failure<*>>(kind.create().retract(Fact.of(p(9))))
        }

        withTests(nameFn = { "${it.id}/retract-all/missing-clause-fails" }, kinds) { kind ->
            assertIs<RetractResult.Failure<*>>(kind.create().retractAll(Fact.of(p(9))))
        }

        withTests(nameFn = { "${it.id}/mutability" }, kinds) { kind ->
            val theory = kind.create()
            assertEquals(kind.id == "mutable", theory.isMutable)
            assertTrue(theory.toMutableTheory().isMutable)
            assertFalse(theory.toImmutableTheory().isMutable)
            assertEquals(heads(theory), heads(theory.toMutableTheory()))
            assertEquals(heads(theory), heads(theory.toImmutableTheory()))
        }

        test("of/a-mutable-theory-stays-mutable") {
            assertTrue(ProblogTheory.of(Unificator.default, Theory.of(Fact.of(p(1))).toMutableTheory()).isMutable)
        }

        test("of/every-overload-maps-the-clauses") {
            val u = Unificator.default
            val expected = listOf("p(1)")
            assertEquals(expected, heads(ProblogTheory.of(u, sequenceOf(Fact.of(p(1))))))
            assertEquals(expected, heads(ProblogTheory.of(u, { Fact.of(p(1)) })))
            assertEquals(expected, heads(MutableProblogTheory.of(u, Fact.of(p(1)))))
            assertEquals(expected, heads(MutableProblogTheory.of(u, sequenceOf(Fact.of(p(1))))))
            assertEquals(expected, heads(MutableProblogTheory.of(u, { Fact.of(p(1)) })))
            assertEquals(emptyList(), heads(ProblogTheory.empty(u)))
            assertEquals(emptyList(), heads(MutableProblogTheory.empty(u)))
        }
    })
