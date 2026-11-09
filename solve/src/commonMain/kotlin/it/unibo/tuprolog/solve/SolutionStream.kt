package it.unibo.tuprolog.solve

import kotlin.concurrent.Volatile
import kotlin.js.JsName

/**
 * The solutions of one query, as [Solver.solve] returns them (#1010): a single-pass, lazy [Sequence] of [Solution]s
 * that can be cancelled, and closed to release what the resolution holds.
 *
 * Use it in a `use { }` block (Kotlin), a try-with-resources (Java, via [PlatformSolutionStream]'s `stream()` or
 * [asIterable]), or a `for...of` loop (JavaScript, where leaving the loop early closes it):
 * ```kotlin
 * solver.solve(goal).use { solutions ->
 *     for (solution in solutions) { ... }
 * }
 * ```
 * A stream that is abandoned without being closed, e.g. after `take(1)`, is only left to the garbage collector:
 * resources that primitives hold for their pending answers (files, cursors, connections) are never released.
 *
 * - [cancel] asks the resolution to stop. It may be called from any thread, or from the resolution's own callbacks
 *   (an interceptor, a primitive), and more than once. The solution being computed, or the next one asked for, is a
 *   [Solution.Halt] whose exception is a [it.unibo.tuprolog.solve.exception.ResolutionCancelledException]; then the
 *   stream ends. How soon the resolution notices depends on the solver: `:solve-prolog` checks at every inference
 *   step, other solvers may only check between solutions, and some end without the [Solution.Halt]. Cancellation
 *   cannot interrupt a primitive that blocks.
 * - [close] stops the resolution without a further solution, and releases what it holds right away, such as the
 *   alternatives primitives have yet to produce (see [it.unibo.tuprolog.solve.primitive.Primitive]). Call it from the
 *   thread consuming the stream; from other threads, call [cancel].
 *
 * The stream can be iterated only once: a second [iterator] call throws [IllegalStateException]. Operations such as
 * `map` or `take` are lazy, as on any [Sequence], but what they return is a plain [Sequence]: close the stream itself.
 *
 * Solvers implement it by extending [AbstractSolutionStream].
 */
interface SolutionStream :
    Sequence<Solution>,
    AutoCloseable,
    PlatformSolutionStream {
    /** Whether [cancel] or [close] was called. */
    @JsName("isCancelled")
    val isCancelled: Boolean

    /** Asks the resolution to stop: the next solution is a [Solution.Halt], then the stream ends. */
    @JsName("cancel")
    fun cancel()

    /** Stops the resolution without a further solution, releasing what it holds. */
    override fun close()

    /** This stream as a single-pass [Iterable], e.g. for Java's for-each loop. */
    @JsName("asIterable")
    fun asIterable(): Iterable<Solution> = Iterable { iterator() }

    /** Every remaining solution, in order; then this stream is closed. Never returns on an infinite stream. */
    @JsName("toList")
    fun toList(): List<Solution> =
        use {
            val solutions = mutableListOf<Solution>()
            iterator().forEach { solutions += it }
            solutions
        }
}

/**
 * Base class of [SolutionStream]s: it makes the stream single-pass, and adds what each platform needs (e.g.
 * `[Symbol.iterator]` on JavaScript). Subclasses provide the solutions through [hasNextSolution] and [nextSolution].
 */
abstract class AbstractSolutionStream : SolutionStream {
    private var iterated = false

    init {
        @Suppress("LeakingThis")
        installPlatformHooks(this)
    }

    /** Whether there is a further solution; called by [iterator]'s `hasNext`. */
    protected abstract fun hasNextSolution(): Boolean

    /** The next solution; called by [iterator]'s `next`. */
    protected abstract fun nextSolution(): Solution

    final override fun iterator(): Iterator<Solution> {
        check(!iterated) { "A ${SolutionStream::class.simpleName} can be iterated only once" }
        iterated = true
        return object : Iterator<Solution> {
            override fun hasNext(): Boolean = hasNextSolution()

            override fun next(): Solution = nextSolution()
        }
    }
}

/**
 * Members [SolutionStream] has on one platform only: `stream()` on the JVM, a `java.util.stream.Stream` that closes
 * this one when it is closed.
 */
expect interface PlatformSolutionStream

internal expect fun installPlatformHooks(stream: AbstractSolutionStream)

/**
 * A [SolutionStream] over [solutions] that checks cancellation between solutions: once cancelled, the next solution
 * is [onCancel]'s, if any, then the stream ends.
 */
internal class IteratorSolutionStream(
    private val solutions: Iterator<Solution>,
    private val onCancel: () -> Solution? = { null },
) : AbstractSolutionStream() {
    @Volatile
    private var cancelled = false

    private var closed = false
    private var halted = false

    override val isCancelled: Boolean
        get() = cancelled

    override fun hasNextSolution(): Boolean =
        when {
            closed || halted -> false
            cancelled -> onCancel() != null
            else -> solutions.hasNext()
        }

    override fun nextSolution(): Solution {
        if (!closed && !halted && cancelled) {
            halted = true
            return onCancel() ?: throw NoSuchElementException()
        }
        if (!hasNextSolution()) throw NoSuchElementException()
        return solutions.next()
    }

    override fun cancel() {
        cancelled = true
    }

    override fun close() {
        cancelled = true
        closed = true
        (solutions as? AutoCloseable)?.close()
    }
}
