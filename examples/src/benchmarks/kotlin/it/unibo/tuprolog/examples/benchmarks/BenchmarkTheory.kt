package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.ClausesParser

/** The theories [Workload]s are solved against, loaded from bundled resources. */
object BenchmarkTheory {
    /** The resource most [Workload]s are solved against. */
    const val WORKLOADS = "workloads.pl"

    /** The theory parsed out of [WORKLOADS]. */
    val theory: Theory by lazy { load(WORKLOADS) }

    /** The theory parsed out of the concatenation of [resources]. */
    fun load(vararg resources: String): Theory =
        ClausesParser.withDefaultOperators().parseTheory(
            resources.joinToString("\n") { BenchmarkTheory::class.java.getResource(it)!!.readText() },
        )
}
