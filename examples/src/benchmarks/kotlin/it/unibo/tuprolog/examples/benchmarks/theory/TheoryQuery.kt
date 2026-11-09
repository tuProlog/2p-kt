package it.unibo.tuprolog.examples.benchmarks.theory

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var

/** The lookups measured by `TheoryLookupBenchmark`, against [TheoryWorkloads.lookup] of a given size. */
enum class TheoryQuery(
    val id: String,
    private val factory: (Int) -> Clause,
) {
    ARG0_BOUND("arg0-bound", { n ->
        Fact.of(Struct.of("p", TheoryWorkloads.c(n / 2), Var.anonymous(), Var.anonymous()))
    }),
    ARG1_BOUND("arg1-bound", { _ -> Fact.of(Struct.of("p", Var.anonymous(), TheoryWorkloads.d(7), Var.anonymous())) }),
    LAST_BOUND("last-bound", { n -> Fact.of(Struct.of("p", Var.anonymous(), Var.anonymous(), Integer.of(n / 2))) }),
    ALL_VARS("all-vars", { _ -> Fact.of(Struct.of("p", Var.anonymous(), Var.anonymous(), Var.anonymous())) }),
    ZERO_ARITY("zero-arity", { _ -> Rule.of(Atom.of("r"), Var.anonymous()) }),
    NESTED_ARG0(
        "nested-arg0",
        { n -> Fact.of(Struct.of("q", Struct.of("f", Struct.of("g", TheoryWorkloads.c(n / 2))), Var.anonymous())) },
    ),
    NESTED_ARG1(
        "nested-arg1",
        { n -> Fact.of(Struct.of("s", Struct.of("f", Var.anonymous(), TheoryWorkloads.c(n / 2)), Var.anonymous())) },
    ),
    JOINT_ARGS("joint-args", { n ->
        val r = TheoryWorkloads.gridSide(n)
        Fact.of(Struct.of("m", Atom.of("a_${(n / 2) % r}"), Atom.of("b_${(n / 2) / r}"), Var.anonymous()))
    }),
    NO_MATCH("no-match", { _ -> Fact.of(Struct.of("p", Atom.of("zzz"), Var.anonymous(), Var.anonymous())) }),
    ;

    fun query(size: Int): Clause = factory(size)

    companion object {
        fun byId(id: String): TheoryQuery = entries.first { it.id == id }
    }
}
