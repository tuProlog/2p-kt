package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.parsing.parse

/**
 * A query over [BenchmarkTheory.theory], solved to exhaustion by the timing benchmarks (#1012).
 * Names are kebab-case, as they appear in the JMH `workload` parameter.
 */
enum class Workload(
    val id: String,
    query: String,
) {
    /** A single trivial goal: measures the fixed cost of a resolution (set-up, first transitions, tear-down). */
    TINY("tiny", "true"),

    /** A deterministic recursion, with one arithmetic evaluation per step. */
    DEEP_RECURSION("deep-recursion", "count(0, 2000)"),

    /** Naive reverse of a 30-element list: pure unification and clause selection. */
    NREV("nrev", "nrev_list(30, _)"),

    /** All solutions of 6-queens via generate-and-test: heavy backtracking. */
    BACKTRACKING("backtracking", "queens(6, _)"),

    /** A search failing after enumerating 2000 candidates. */
    FAILING_SEARCH("failing-search", "failing_search(2000)"),

    /** 100 `findall/3` sub-resolutions. */
    NESTED_SOLVING("nested-solving", "nested(0, 100)"),

    /** 300 rounds of relatively expensive built-ins. */
    PRIMITIVES("primitives", "primitives(0, 300)"),
    ;

    /** The query, parsed. */
    val goal: Struct = Struct.parse(query)

    companion object {
        /** The [Workload] whose [id] is [id]. */
        fun byId(id: String): Workload = entries.first { it.id == id }
    }
}
