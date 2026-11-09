package it.unibo.tuprolog.solve

import java.util.Spliterator
import java.util.Spliterators
import java.util.stream.Stream
import java.util.stream.StreamSupport

actual interface PlatformSolutionStream {
    /**
     * This [SolutionStream] as a sequential, single-pass [Stream]; closing the [Stream] (e.g. in a
     * try-with-resources) closes this stream.
     */
    fun stream(): Stream<Solution> {
        val self = this as SolutionStream
        return StreamSupport
            .stream(Spliterators.spliteratorUnknownSize(self.iterator(), Spliterator.ORDERED), false)
            .onClose(self::close)
    }
}

internal actual fun installPlatformHooks(stream: AbstractSolutionStream) = Unit
