package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.library.Runtime

/**
 * Builds a [Solver] wired to [IOLib] (plus the default builtins), with [stdIn] as the contents of
 * the standard input stream, [namedInputs] as extra pre-opened input streams (keyed by alias), and
 * output collected into [output] (standard output) and [namedOutputs] (extra pre-opened output
 * streams, keyed by alias).
 *
 * This lets tests exercise stream-taking primitives (e.g. `get_char/2`, `put_char/2`) without going
 * through `open/3,4` and a real (or fake) file system.
 */
fun SolverFactory.ioSolver(
    stdIn: String = "",
    namedInputs: Map<String, String> = emptyMap(),
    output: StringBuilder = StringBuilder(),
    namedOutputs: Map<String, StringBuilder> = emptyMap(),
): Solver {
    val stdInChannel = InputChannel.of(stdIn)
    val inputs =
        InputStore.of(
            mapOf(InputStore.STDIN to stdInChannel, "user_input" to stdInChannel) +
                namedInputs.mapValues { (_, contents) -> InputChannel.of(contents) },
        )
    val stdOutChannel = OutputChannel.of<String> { output.append(it) }
    val outputs =
        OutputStore.of(
            mapOf(OutputStore.STDOUT to stdOutChannel, "user_output" to stdOutChannel) +
                namedOutputs.mapValues { (_, sb) -> OutputChannel.of<String> { sb.append(it) } },
        )
    return solverOf(
        libraries = Runtime.of(IOLib) + defaultBuiltins,
        inputs = inputs,
        outputs = outputs,
    )
}
