package it.unibo.tuprolog.examples.benchmarks.theory

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/**
 * The theory implementations compared by the `:theory` benchmarks (#1019), by JMH `impl` parameter. `immutable` and
 * `mutable` are whatever [Theory.of] and [MutableTheory.of] build, so the same benchmark sources measure both the
 * implementation before #1019 and the one after it. `listed` and `mutable-listed` are the linear-scan baselines.
 */
enum class TheoryImpl(
    val id: String,
    private val factory: (Unificator, List<Clause>) -> Theory,
) {
    IMMUTABLE("immutable", { u, cs -> Theory.of(u, cs) }),
    MUTABLE("mutable", { u, cs -> MutableTheory.of(u, cs) }),

    // Baselines from before #1019, removed with the listed theories themselves.
    LISTED("listed", { u, cs -> Theory.listedOf(u, cs) }),
    MUTABLE_LISTED("mutable-listed", { u, cs -> MutableTheory.listedOf(u, cs) }),
    ;

    val isMutable: Boolean get() = this == MUTABLE || this == MUTABLE_LISTED

    fun build(
        clauses: List<Clause>,
        unificator: Unificator = Unificator.default,
    ): Theory = factory(unificator, clauses)

    companion object {
        fun byId(id: String): TheoryImpl = entries.first { it.id == id }
    }
}

/**
 * Deterministic clause generators for the `:theory` benchmarks (#1019).
 *
 * [standard] builds a theory with three procedures of `size` clauses each, so that every [TheoryQuery] has something
 * to look for:
 * - `p(c_i, d_(i mod 50), i)`: arg 0 unique, arg 1 with 50 distinct values, arg 2 numeric and unique;
 * - `q(f(g(c_i)), i)`: a nested compound first argument;
 * - `r :- true`: a zero-arity procedure;
 * plus one directive.
 */
object TheoryWorkloads {
    const val ARG1_VALUES = 50

    fun c(i: Int): Atom = Atom.of("c_$i")

    fun d(i: Int): Atom = Atom.of("d_${i % ARG1_VALUES}")

    fun p(i: Int): Fact = Fact.of(Struct.of("p", c(i), d(i), Integer.of(i)))

    fun q(i: Int): Fact = Fact.of(Struct.of("q", Struct.of("f", Struct.of("g", c(i))), Integer.of(i)))

    fun r(): Rule = Rule.of(Atom.of("r"), Truth.TRUE)

    fun standard(size: Int): List<Clause> =
        buildList(3 * size + 1) {
            add(Directive.of(Struct.of("dynamic", Struct.of("/", Atom.of("p"), Integer.of(3)))))
            for (i in 0 until size) add(p(i))
            for (i in 0 until size) add(q(i))
            repeat(size) { add(r()) }
        }

    /** `size` facts `k_j(c_i)`, spread round-robin over `predicates` functors `k_0`, …, `k_(predicates-1)`. */
    fun spread(
        size: Int,
        predicates: Int,
    ): List<Clause> = List(size) { i -> Fact.of(Struct.of("k_${i % predicates}", c(i))) }
}

/** The lookups measured by `TheoryLookupBenchmark`, against [TheoryWorkloads.standard] of a given size. */
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
    NO_MATCH("no-match", { _ -> Fact.of(Struct.of("p", Atom.of("zzz"), Var.anonymous(), Var.anonymous())) }),
    ;

    fun query(size: Int): Clause = factory(size)

    companion object {
        fun byId(id: String): TheoryQuery = entries.first { it.id == id }
    }
}
