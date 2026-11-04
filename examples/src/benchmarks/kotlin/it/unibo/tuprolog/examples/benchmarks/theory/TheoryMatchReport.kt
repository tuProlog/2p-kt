package it.unibo.tuprolog.examples.benchmarks.theory

/**
 * Deterministic counts behind `TheoryLookupBenchmark` (#1019): for each implementation and [TheoryQuery], how many
 * `match` calls the theory performs to find the first match (`first`) and every match (`all`), with
 * [TheoryWorkloads.standard] of size [SIZE]. Prints a Markdown table.
 *
 * Run with `./gradlew :examples:theoryMatchReport`.
 */
object TheoryMatchReport {
    const val SIZE = 1000

    @JvmStatic
    fun main(args: Array<String>) {
        val impls = TheoryImpl.entries
        println("| Query | Matches | " + impls.joinToString(" | ") { it.id } + " |")
        println("|---|---|" + impls.joinToString("") { "---:|" })
        for (query in TheoryQuery.entries) {
            for (mode in listOf("first", "all")) {
                val counts =
                    impls.map { impl ->
                        val unificator = CountingUnificator()
                        val theory = impl.build(TheoryWorkloads.standard(SIZE), unificator)
                        val pattern = query.query(SIZE)
                        unificator.reset()
                        if (mode == "first") theory[pattern].firstOrNull() else theory[pattern].count()
                        unificator.matches
                    }
                println("| ${query.id} | $mode | " + counts.joinToString(" | ") + " |")
            }
        }
    }
}
