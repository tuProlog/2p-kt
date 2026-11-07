package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Recursive
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import it.unibo.tuprolog.core.List as LogicList

/**
 * Construction, traversal, transformation, metadata and comparison costs of recursive terms (#1075), per kind
 * (list, tuple, block; eagerly built out of a list, or lazily out of a sequence), size and groundness.
 *
 * Run it with JMH's GC profiler (allocations per operation) via
 * `./gradlew :examples:timingBenchmarks -PjmhArgs='RecursiveTermsBenchmark -wi 3 -i 5 -f 1'`.
 */
@State(Scope.Benchmark)
class RecursiveTermsBenchmark {
    @Param("list-eager", "list-lazy", "tuple-eager", "tuple-lazy", "block-eager", "block-lazy")
    var kind: String = "list-eager"

    @Param("10", "99", "101", "1000", "100000")
    var size: Int = 10

    @Param("true", "false")
    var ground: Boolean = false

    private lateinit var items: List<Term>
    private lateinit var otherItems: List<Term>
    private lateinit var substitution: Substitution
    private lateinit var prebuilt: Recursive

    /** Like [prebuilt], but read in full once, so that lazy kinds are materialized. */
    private lateinit var read: Recursive

    @Setup
    fun setUp() {
        items = (0 until size).map { if (ground) Atom.of("a$it") else Var.of("X$it") }
        otherItems = items.dropLast(1) + Atom.of("different")
        val target = if (ground) Var.of("Unused") else items.first() as Var
        substitution = Substitution.of(target, Atom.of("a"))
        prebuilt = build(items)
        read = build(items).also { it.traverse() }
    }

    private fun build(items: List<Term>): Recursive =
        when (kind) {
            "list-eager" -> LogicList.of(items)
            "list-lazy" -> LogicList.of(items.asSequence())
            "tuple-eager" -> Tuple.of(items)
            "tuple-lazy" -> Tuple.of(items.asSequence().asIterable())
            "block-eager" -> Block.of(items)
            "block-lazy" -> Block.of(items.asSequence())
            else -> error("Unknown kind: $kind")
        }

    private fun Term.traverse(): Int = (this as Recursive).unfoldedSequence.count()

    /** The direct recursive suffix of [term], if any. */
    private fun suffixOf(term: Term): Term? =
        when {
            term.isCons -> term.castToCons().tail
            term.isTuple -> term.castToTuple().right
            term.isBlock && term.castToBlock().arity == 1 -> term.castToBlock()[0].takeIf { it.isTuple }
            else -> null
        }

    @Benchmark
    fun construct(): Any = build(items)

    @Benchmark
    fun constructConsumeOne(): Any = build(items).unfoldedSequence.first()

    @Benchmark
    fun constructConsumeTen(): Any = build(items).unfoldedSequence.take(10).count()

    @Benchmark
    fun constructConsumeAll(): Int = build(items).traverse()

    @Benchmark
    fun applySubstitution(): Int = prebuilt.apply(substitution).traverse()

    @Benchmark
    fun applySubstitutionTwice(): Int = prebuilt.apply(substitution).apply(substitution).traverse()

    @Benchmark
    fun freshCopy(): Int = prebuilt.freshCopy().traverse()

    @Benchmark
    fun applySubstitutionAfterRead(): Int = read.apply(substitution).traverse()

    @Benchmark
    fun freshCopyAfterRead(): Int = read.freshCopy().traverse()

    @Benchmark
    fun sizeFirst(): Int = build(items).size

    @Benchmark
    fun sizeRepeated(): Int = prebuilt.size

    @Benchmark
    fun sizeEverySuffix(): Int {
        var total = 0
        var current: Term? = build(items)
        while (current is Recursive) {
            total += current.size
            current = suffixOf(current)
        }
        return total
    }

    @Benchmark
    fun isGroundFirst(): Boolean = build(items).isGround

    @Benchmark
    fun isGroundRepeated(): Boolean = prebuilt.isGround

    @Benchmark
    fun equalsIdentical(): Boolean = prebuilt == prebuilt

    @Benchmark
    fun equalsIndependent(): Boolean = build(items) == build(items)

    @Benchmark
    fun equalsLateMismatch(): Boolean = build(items) == build(otherItems)

    @Benchmark
    fun structurallyEqualsIndependent(): Boolean = build(items).structurallyEquals(build(items))

    @Benchmark
    fun compareIndependent(): Int = build(items).compareTo(build(items))

    @Benchmark
    fun hashFirst(): Int = build(items).hashCode()
}
