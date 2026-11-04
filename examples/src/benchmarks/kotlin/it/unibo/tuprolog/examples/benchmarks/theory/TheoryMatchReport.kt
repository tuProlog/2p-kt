package it.unibo.tuprolog.examples.benchmarks.theory

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator

/** A [Unificator] counting its `match` calls: how many candidates a theory actually tried to unify. */
class CountingUnificator(
    private val delegate: Unificator = Unificator.default,
) : Unificator by delegate {
    var matches: Long = 0
        private set

    override fun match(
        term1: Term,
        term2: Term,
        occurCheckEnabled: Boolean,
    ): Boolean {
        matches++
        return delegate.match(term1, term2, occurCheckEnabled)
    }

    override fun match(
        term1: Term,
        term2: Term,
    ): Boolean = match(term1, term2, true)

    fun reset() {
        matches = 0
    }
}

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
