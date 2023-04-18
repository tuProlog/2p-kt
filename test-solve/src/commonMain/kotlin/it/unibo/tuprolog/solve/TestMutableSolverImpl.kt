package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import kotlin.test.assertNotSame
import kotlin.test.assertSame

internal class TestMutableSolverImpl(private val solverFactory: SolverFactory) : TestMutableSolver {
    override fun testSetStdIn() {
        val solver = solverFactory.mutableSolverOf()
        assertSame(solver.standardInput, solver.inputChannels.current)
        val channel = InputChannel.of("sample")
        assertNotSame(solver.standardInput, channel)
        assertNotSame(solver.inputChannels.current, channel)
        solver.setStandardInput(channel)
        assertSame(solver.standardInput, channel)
        assertSame(solver.inputChannels.current, channel)
    }

    override fun testSetStdOut() {
        val solver = solverFactory.mutableSolverOf()
        assertSame(solver.standardOutput, solver.outputChannels.current)
        val channel = OutputChannel.of<String> { }
        assertNotSame(solver.standardOutput, channel)
        assertNotSame(solver.outputChannels.current, channel)
        solver.setStandardOutput(channel)
        assertSame(solver.standardOutput, channel)
        assertSame(solver.outputChannels.current, channel)
    }

    override fun testSetStdErr() {
        val solver = solverFactory.mutableSolverOf()
        val initialOutput = solver.outputChannels.current
        assertNotSame(solver.standardError, initialOutput)
        val channel = OutputChannel.of<String> { }
        assertNotSame(solver.standardError, channel)
        assertNotSame(solver.outputChannels.current, channel)
        solver.setStandardError(channel)
        assertSame(solver.standardError, channel)
        assertNotSame(solver.outputChannels.current, channel)
        assertSame(solver.outputChannels.current, initialOutput)
    }

    override fun testSetStdWarn() {
        val solver = solverFactory.mutableSolverOf()
        val initialOutput: OutputChannel<*>? = solver.outputChannels.current
        assertNotSame<OutputChannel<*>?>(solver.warnings, initialOutput)
        val channel = OutputChannel.of<Warning> { }
        assertNotSame(solver.warnings, channel)
        assertNotSame<OutputChannel<*>?>(solver.outputChannels.current, channel)
        solver.setWarnings(channel)
        assertSame(solver.warnings, channel)
        assertNotSame<OutputChannel<*>?>(solver.outputChannels.current, channel)
        assertSame(solver.outputChannels.current, initialOutput)
    }
}
