package it.unibo.tuprolog.solve

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
