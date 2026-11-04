package it.unibo.tuprolog.examples.benchmarks.theory

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth

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
