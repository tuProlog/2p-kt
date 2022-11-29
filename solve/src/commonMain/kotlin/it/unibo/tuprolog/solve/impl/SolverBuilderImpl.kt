package it.unibo.tuprolog.solve.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverBuilder
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

internal class SolverBuilderImpl(private val factory: SolverFactory) : SolverBuilder {

    private inline fun returningThis(action: () -> Unit): SolverBuilder {
        action()
        return this
    }

    override var unificator: Unificator = factory.defaultUnificator

    override fun unificator(unificator: Unificator): SolverBuilder = returningThis {
        this.unificator = unificator
    }

    override var runtime: Runtime = factory.defaultRuntime

    override fun runtime(runtime: Runtime): SolverBuilder = returningThis {
        this.runtime = runtime
    }

    override var builtins: Library? = factory.defaultBuiltins

    override fun builtins(builtins: Library): SolverBuilder = returningThis {
        this.builtins = builtins
    }

    override fun noBuiltins(): SolverBuilder = returningThis {
        this.builtins = null
    }

    override var flags: FlagStore = factory.defaultFlags

    override fun flags(flags: FlagStore): SolverBuilder = returningThis {
        this.flags = flags
    }

    override fun flag(name: String, value: Term): SolverBuilder =
        flags(flags + (name to value))

    override fun flag(flag: Pair<String, Term>): SolverBuilder =
        flags(flags + flag)

    override fun flag(flag: NotableFlag): SolverBuilder =
        flags(flags.set(flag))

    override fun flag(flag: NotableFlag, value: Term): SolverBuilder =
        flags(flags.set(flag, value))

    override fun <T : NotableFlag> flag(flag: T, value: T.() -> Term): SolverBuilder =
        flags(flags.set(flag, flag.value()))

    override var staticKb: Theory = factory.defaultStaticKb

    override fun staticKb(theory: Theory): SolverBuilder = returningThis {
        staticKb = theory
    }

    override fun staticKb(vararg clauses: Clause): SolverBuilder = returningThis {
        staticKb = Theory.of(unificator, *clauses)
    }

    override fun staticKb(clauses: Iterable<Clause>): SolverBuilder = returningThis {
        staticKb = Theory.of(unificator, clauses)
    }

    override fun staticKb(clauses: Sequence<Clause>): SolverBuilder = returningThis {
        staticKb = Theory.of(unificator, clauses)
    }

    override var dynamicKb: Theory = factory.defaultDynamicKb

    override fun dynamicKb(theory: Theory): SolverBuilder = returningThis {
        dynamicKb = theory
    }

    override fun dynamicKb(vararg clauses: Clause): SolverBuilder = returningThis {
        dynamicKb = Theory.listedOf(unificator, *clauses)
    }

    override fun dynamicKb(clauses: Iterable<Clause>): SolverBuilder = returningThis {
        dynamicKb = Theory.listedOf(unificator, clauses)
    }

    override fun dynamicKb(clauses: Sequence<Clause>): SolverBuilder = returningThis {
        dynamicKb = Theory.listedOf(unificator, clauses)
    }

    override var inputs: InputStore = InputStore.fromStandard(factory.defaultInputChannel)

    override fun inputs(inputs: InputStore): SolverBuilder = returningThis {
        this.inputs = inputs
    }

    override fun input(alias: String, channel: InputChannel<String>): SolverBuilder = returningThis {
        this.inputs += (alias to channel)
    }

    override var standardInput: InputChannel<String>
        get() = inputs.stdIn
        set(value) {
            inputs = inputs.setStdIn(value)
        }

    override fun standardInput(channel: InputChannel<String>): SolverBuilder = returningThis {
        standardInput = channel
    }

    override var outputs: OutputStore = OutputStore.fromStandard(
        factory.defaultOutputChannel,
        factory.defaultErrorChannel,
        factory.defaultWarningsChannel
    )

    override fun outputs(outputs: OutputStore): SolverBuilder = returningThis {
        this.outputs = outputs
    }

    override fun output(alias: String, channel: OutputChannel<String>): SolverBuilder = returningThis {
        outputs += (alias to channel)
    }

    override var standardOutput: OutputChannel<String>
        get() = outputs.stdOut
        set(value) {
            outputs = outputs.setStdOut(value)
        }

    override fun standardOutput(channel: OutputChannel<String>): SolverBuilder = returningThis {
        standardOutput = channel
    }

    override var standardError: OutputChannel<String>
        get() = outputs.stdErr
        set(value) {
            outputs = outputs.setStdErr(value)
        }

    override fun standardError(channel: OutputChannel<String>): SolverBuilder = returningThis {
        standardError = channel
    }

    override var warnings: OutputChannel<Warning>
        get() = outputs.warnings
        set(value) {
            outputs = outputs.setWarnings(value)
        }

    override fun warnings(channel: OutputChannel<Warning>): SolverBuilder = returningThis {
        warnings = channel
    }

    private val runtimeWithBuiltins: Runtime
        get() = builtins?.let { runtime + it } ?: runtime

    override fun build(): Solver =
        factory.solverOf(
            unificator = unificator,
            libraries = runtimeWithBuiltins,
            staticKb = staticKb,
            dynamicKb = dynamicKb,
            flags = flags,
            inputs = inputs,
            outputs = outputs
        )

    override fun buildMutable(): MutableSolver =
        factory.mutableSolverOf(
            unificator = unificator,
            libraries = runtimeWithBuiltins,
            staticKb = staticKb,
            dynamicKb = dynamicKb,
            flags = flags,
            inputs = inputs,
            outputs = outputs
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SolverBuilderImpl

        if (unificator != other.unificator) return false
        if (runtime != other.runtime) return false
        if (builtins != other.builtins) return false
        if (flags != other.flags) return false
        if (staticKb != other.staticKb) return false
        if (dynamicKb != other.dynamicKb) return false
        if (inputs != other.inputs) return false
        if (outputs != other.outputs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = runtime.hashCode()
        result = 31 * result + builtins.hashCode()
        result = 31 * result + flags.hashCode()
        result = 31 * result + staticKb.hashCode()
        result = 31 * result + dynamicKb.hashCode()
        result = 31 * result + inputs.hashCode()
        result = 31 * result + outputs.hashCode()
        return result
    }

    override fun toString(): String =
        "SolverBuilderImpl(" +
            "runtime=$runtime, " +
            "builtins=$builtins, " +
            "flags=$flags, " +
            "staticKb=$staticKb, " +
            "dynamicKb=$dynamicKb, " +
            "inputs=$inputs, " +
            "outputs=$outputs)"

    override fun toFactory(): SolverFactory =
        object : SolverFactory by factory {
            override val defaultBuiltins: Library
                get() = builtins ?: factory.defaultBuiltins

            override val defaultRuntime: Runtime
                get() = runtime

            override val defaultFlags: FlagStore
                get() = flags

            override val defaultStaticKb: Theory
                get() = staticKb

            override val defaultDynamicKb: Theory
                get() = dynamicKb

            override val defaultInputChannel: InputChannel<String>
                get() = inputs.stdIn

            override val defaultOutputChannel: OutputChannel<String>
                get() = outputs.stdOut

            override val defaultErrorChannel: OutputChannel<String>
                get() = outputs.stdErr

            override val defaultWarningsChannel: OutputChannel<Warning>
                get() = outputs.warnings
        }
}
