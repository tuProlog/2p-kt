package it.unibo.tuprolog.solve

actual interface PlatformSolutionStream

private fun step(
    done: Boolean,
    value: Solution? = null,
): dynamic {
    val result: dynamic = js("({})")
    result.done = done
    result.value = value
    return result
}

/**
 * Makes [stream] a JavaScript iterable, so that `for (const solution of solver.solve(goal))` works: leaving the loop
 * early (`break`, `return`, an exception) closes the stream, as does reaching its end. It also gives the stream a
 * plain `close()` method, which Kotlin/JS would otherwise mangle (it overrides `AutoCloseable.close`).
 */
internal actual fun installPlatformHooks(stream: AbstractSolutionStream) {
    val iterate: () -> dynamic = {
        val solutions = stream.iterator()
        val adapter: dynamic = js("({})")
        adapter.next = {
            if (solutions.hasNext()) {
                step(false, solutions.next())
            } else {
                stream.close()
                step(true)
            }
        }
        adapter["return"] = {
            stream.close()
            step(true)
        }
        adapter
    }
    val self: dynamic = stream
    self[js("Symbol.iterator")] = iterate
    self.close = { stream.close() }
}
