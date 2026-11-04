package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.ClausesParser

/** The theory every [Workload] is solved against, loaded from the bundled `workloads.pl` resource. */
object BenchmarkTheory {
    /** The parsed theory. */
    val theory: Theory by lazy {
        val text =
            BenchmarkTheory::class.java
                .getResource("workloads.pl")!!
                .readText()
        ClausesParser.withDefaultOperators().parseTheory(text)
    }
}
