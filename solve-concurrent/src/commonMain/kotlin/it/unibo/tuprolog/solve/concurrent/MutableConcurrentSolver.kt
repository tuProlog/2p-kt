package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.getAllOperators
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory

internal class MutableConcurrentSolver : ConcurrentSolverImpl, MutableSolver {

    constructor(
        libraries: Libraries = Libraries.empty(),
        flags: FlagStore = FlagStore.empty(),
        staticKb: Theory = Theory.empty(),
        dynamicKb: Theory = MutableTheory.empty(),
        inputChannels: InputStore = InputStore.fromStandard(),
        outputChannels: OutputStore = OutputStore.fromStandard(),
        trustKb: Boolean = false
    ) : super(libraries, flags, staticKb, dynamicKb, inputChannels, outputChannels, trustKb)

    constructor(
        libraries: Libraries = Libraries.empty(),
        flags: FlagStore = FlagStore.empty(),
        staticKb: Theory = Theory.empty(),
        dynamicKb: Theory = MutableTheory.empty(),
        stdIn: InputChannel<String> = InputChannel.stdIn(),
        stdOut: OutputChannel<String> = OutputChannel.stdOut(),
        stdErr: OutputChannel<String> = OutputChannel.stdErr(),
        warnings: OutputChannel<Warning> = OutputChannel.warn(),
        trustKb: Boolean = false
    ) : super(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings, trustKb)

    override fun loadLibrary(library: AliasedLibrary) {
        updateContext {
            val newLibraries = libraries + library
            copy(
                libraries = newLibraries,
                operators = operators + getAllOperators(newLibraries).toOperatorSet()
            )
        }
    }

    override fun unloadLibrary(library: AliasedLibrary) {
        updateContext {
            val newLibraries = libraries + library
            copy(
                libraries = newLibraries,
                operators = getAllOperators(newLibraries, staticKb, dynamicKb).toOperatorSet()
            )
        }
    }

    override fun setLibraries(libraries: Libraries) {
        updateContext {
            copy(
                libraries = libraries,
                operators = getAllOperators(libraries, staticKb, dynamicKb).toOperatorSet()
            )
        }
    }

    override fun loadStaticKb(theory: Theory) {
        initializeKb(
            staticKb = theory,
            appendStatic = false
        )
    }

    override fun appendStaticKb(theory: Theory) {
        initializeKb(staticKb = theory)
    }

    override fun resetStaticKb() {
        updateContext {
            copy(
                staticKb = Theory.empty(),
                operators = getAllOperators(libraries, dynamicKb).toOperatorSet()
            )
        }
    }

    override fun loadDynamicKb(theory: Theory) {
        initializeKb(
            dynamicKb = theory,
            appendDynamic = false
        )
    }

    override fun appendDynamicKb(theory: Theory) {
        initializeKb(dynamicKb = theory)
    }

    override fun resetDynamicKb() {
        updateContext {
            copy(
                dynamicKb = MutableTheory.empty(),
                operators = getAllOperators(libraries, staticKb).toOperatorSet()
            )
        }
    }

    override fun assertA(clause: Clause) {
        updateContext {
            copy(
                dynamicKb = dynamicKb.assertA(clause),
                operators = operators + listOf(clause).getAllOperators().toOperatorSet()
            )
        }
    }

    override fun assertA(fact: Struct) {
        updateContext {
            copy(dynamicKb = dynamicKb.assertA(fact))
        }
    }

    override fun assertZ(clause: Clause) {
        updateContext {
            copy(
                dynamicKb = dynamicKb.assertZ(clause),
                operators = operators + listOf(clause).getAllOperators().toOperatorSet()
            )
        }
    }

    override fun assertZ(fact: Struct) {
        updateContext {
            copy(dynamicKb = dynamicKb.assertZ(fact))
        }
    }

    override fun retract(clause: Clause): RetractResult<Theory> {
        val result = dynamicKb.retract(clause)
        updateContext {
            copy(
                dynamicKb = result.theory.toMutableTheory(),
                operators = operators - listOf(clause).getAllOperators().toOperatorSet()
            )
        }
        return result
    }

    override fun retract(fact: Struct): RetractResult<Theory> {
        val result = dynamicKb.retract(fact)
        updateContext {
            copy(dynamicKb = result.theory.toMutableTheory())
        }
        return result
    }

    override fun retractAll(clause: Clause): RetractResult<Theory> {
        val result = dynamicKb.retractAll(clause)
        updateContext {
            copy(
                dynamicKb = result.theory.toMutableTheory(),
                operators = operators - result.theory.getAllOperators().toOperatorSet()
            )
        }
        return result
    }

    override fun retractAll(fact: Struct): RetractResult<Theory> {
        val result = dynamicKb.retractAll(fact)
        updateContext {
            copy(dynamicKb = result.theory.toMutableTheory())
        }
        return result
    }

    override fun setFlag(name: String, value: Term) {
        updateContext {
            copy(flags = flags.set(name, value))
        }
    }

    override fun setFlag(flag: Pair<String, Term>) {
        updateContext {
            copy(flags = flags + flag)
        }
    }

    override fun setFlag(flag: NotableFlag) {
        updateContext {
            copy(flags = flags + flag)
        }
    }

    override fun setStandardInput(stdIn: InputChannel<String>) {
        updateContext {
            copy(inputChannels = inputChannels + (InputStore.STDIN to stdIn))
        }
    }

    override fun setStandardError(stdErr: OutputChannel<String>) {
        updateContext {
            copy(outputChannels = outputChannels + (OutputStore.STDERR to stdErr))
        }
    }

    override fun setStandardOutput(stdOut: OutputChannel<String>) {
        updateContext {
            copy(outputChannels = outputChannels + (OutputStore.STDOUT to stdOut))
        }
    }

    override fun setWarnings(warnings: OutputChannel<Warning>) {
        updateContext {
            copy(outputChannels = OutputStore.of(outputChannels, warnings))
        }
    }

    override fun copy(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>
    ) = MutableConcurrentSolver(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

    override fun clone(): MutableConcurrentSolver = copy()
}
