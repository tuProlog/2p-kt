package it.unibo.tuprolog.examples.benchmarks.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/**
 * The theory implementations compared by the `:theory` benchmarks (#1019), by JMH `impl` parameter. `immutable` and
 * `mutable` are whatever [Theory.of] and [MutableTheory.of] build, so the same benchmark sources measure both the
 * implementation before #1019 and the one after it. Before #1019, `listed` and `mutable-listed` measured
 * linear-scan theories too (see the baseline results), removed since.
 */
enum class TheoryImpl(
    val id: String,
    private val factory: (Unificator, List<Clause>) -> Theory,
) {
    IMMUTABLE("immutable", { u, cs -> Theory.of(u, cs) }),
    MUTABLE("mutable", { u, cs -> MutableTheory.of(u, cs) }),
    ;

    val isMutable: Boolean get() = this == MUTABLE

    fun build(
        clauses: List<Clause>,
        unificator: Unificator = Unificator.default,
    ): Theory = factory(unificator, clauses)

    companion object {
        fun byId(id: String): TheoryImpl = entries.first { it.id == id }
    }
}
