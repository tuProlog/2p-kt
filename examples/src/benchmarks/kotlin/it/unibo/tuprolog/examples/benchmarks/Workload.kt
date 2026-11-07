package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.TrackVariables
import it.unibo.tuprolog.theory.Theory

private val STRIPS = listOf("strips.pl")

private val TRACKING = FlagStore.DEFAULT + (TrackVariables.name to TrackVariables.ON)

/**
 * A query over the theory loaded from [resources], solved to exhaustion by the timing benchmarks (#1012), by a
 * solver with [flags].
 * Names are kebab-case, as they appear in the JMH `workload` parameter.
 */
enum class Workload(
    val id: String,
    query: String,
    private val resources: List<String> = listOf(BenchmarkTheory.WORKLOADS),
    val flags: FlagStore = FlagStore.DEFAULT,
) {
    /** A single trivial goal: measures the fixed cost of a resolution (set-up, first transitions, tear-down). */
    TINY("tiny", "true"),

    /** A deterministic recursion, with one arithmetic evaluation per step. */
    DEEP_RECURSION("deep-recursion", "count(0, 2000)"),

    /** Naive reverse of a 30-element list: pure unification and clause selection. */
    NREV("nrev", "nrev_list(30, _)"),

    /** Naive reverse of a 100-element list: long non-ground lists flowing through substitutions (#1075). */
    NREV_LONG("nrev-100", "nrev_list(100, _)"),

    /** Binds a 200-variable list one variable per step: substitutions over long non-ground lists (#1075). */
    BIND_VARS("bind-vars", "bind_vars(200)"),

    /**
     * STRIPS planning for the Sussman anomaly, with states as lists (#1075). Like the other `strips-*` workloads, it
     * needs `TrackVariables` on, as rule bodies may lose bindings otherwise (#1073).
     */
    STRIPS_LIST("strips-list", "sussman_checked", STRIPS + "strips-sets-list.pl", TRACKING),

    /** STRIPS planning for the Sussman anomaly, with states as tuples (#1075). */
    STRIPS_TUPLE("strips-tuple", "sussman_checked", STRIPS + "strips-sets-tuple.pl", TRACKING),

    /** STRIPS planning for the Sussman anomaly, with states as blocks (#1075). */
    STRIPS_BLOCK("strips-block", "sussman_checked", STRIPS + "strips-sets-block.pl", TRACKING),

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

    /** The theory [goal] is solved against. */
    val theory: Theory by lazy { BenchmarkTheory.load(*resources.toTypedArray()) }

    /** Checks the expected outcome, including the deliberate failure of [FAILING_SEARCH]. */
    fun checkSolution(solution: Solution) {
        require(if (this == FAILING_SEARCH) solution.isNo else solution.isYes) {
            "Unexpected result for workload $id: $solution"
        }
    }

    companion object {
        /** The [Workload] whose [id] is [id]. */
        fun byId(id: String): Workload = entries.first { it.id == id }
    }
}
