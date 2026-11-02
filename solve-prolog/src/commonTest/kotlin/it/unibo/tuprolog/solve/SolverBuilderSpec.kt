package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.LastCallOptimization
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.stdlib.primitive.Natural
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

private fun p(n: Int): Clause = Fact.of(Struct.of("p", Integer.of(n)))

private val custom = Library.of("custom")
private val input: InputChannel<String> = InputChannel.of("input")
private val output: OutputChannel<String> = OutputChannel.of {}
private val warningsChannel: OutputChannel<Warning> = OutputChannel.of {}
private val on = Atom.of("on")

/** One builder setting, and what every solver built with it must expose. */
private class Setting(
    val id: String,
    val configure: SolverBuilder.() -> SolverBuilder,
    val check: (Solver) -> Unit,
)

private val settings =
    listOf(
        Setting("unificator", { unificator(Unificator.strict()) }, { it.unificator shouldNotBe Unificator.default }),
        Setting("runtime", { runtime(Runtime.of(custom)) }, { it.libraries.aliases shouldContain "custom" }),
        Setting("library/aliased", { library("mine", Natural) }, { it.libraries.aliases shouldContain "mine" }),
        Setting("library/anonymous", { library(Natural) }, { it.libraries.aliases shouldContain "anonymous0" }),
        Setting("builtins", { builtins(custom) }, { it.libraries.aliases shouldBe setOf("custom") }),
        Setting("no-builtins", { noBuiltins() }, { it.libraries.aliases shouldBe emptySet() }),
        Setting("flags", { flags(FlagStore.of("f" to on)) }, { it.flags shouldBe FlagStore.of("f" to on) }),
        Setting("flag/name-and-value", { flag("f", on) }, { it.flags["f"] shouldBe on }),
        Setting("flag/pair", { flag("f" to on) }, { it.flags["f"] shouldBe on }),
        Setting(
            "flag/notable-default",
            { flags(FlagStore.empty()).flag(LastCallOptimization) },
            { it.flags[LastCallOptimization] shouldBe LastCallOptimization.defaultValue },
        ),
        Setting(
            "flag/notable-and-value",
            { flag(LastCallOptimization, LastCallOptimization.OFF) },
            { it.flags[LastCallOptimization] shouldBe LastCallOptimization.OFF },
        ),
        Setting(
            "flag/notable-and-lambda",
            { flag(LastCallOptimization) { OFF } },
            { it.flags[LastCallOptimization] shouldBe LastCallOptimization.OFF },
        ),
        Setting(
            "static-kb/theory",
            { staticKb(Theory.of(p(1))) },
            { it.staticKb.clauses.toList() shouldBe listOf(p(1)) },
        ),
        Setting("static-kb/vararg", { staticKb(p(1)) }, { it.staticKb.clauses.toList() shouldBe listOf(p(1)) }),
        Setting(
            "static-kb/iterable",
            { staticKb(listOf(p(1))) },
            { it.staticKb.clauses.toList() shouldBe listOf(p(1)) },
        ),
        Setting(
            "static-kb/sequence",
            { staticKb(sequenceOf(p(1))) },
            { it.staticKb.clauses.toList() shouldBe listOf(p(1)) },
        ),
        Setting(
            "dynamic-kb/theory",
            { dynamicKb(Theory.of(p(1))) },
            { it.dynamicKb.clauses.toList() shouldBe listOf(p(1)) },
        ),
        Setting("dynamic-kb/vararg", { dynamicKb(p(1)) }, { it.dynamicKb.clauses.toList() shouldBe listOf(p(1)) }),
        Setting(
            "dynamic-kb/iterable",
            { dynamicKb(listOf(p(1))) },
            { it.dynamicKb.clauses.toList() shouldBe listOf(p(1)) },
        ),
        Setting(
            "dynamic-kb/sequence",
            { dynamicKb(sequenceOf(p(1))) },
            { it.dynamicKb.clauses.toList() shouldBe listOf(p(1)) },
        ),
        Setting(
            "inputs",
            { inputs(InputStore.fromStandard() + ("in" to input)) },
            { it.inputChannels["in"] shouldBeSameInstanceAs input },
        ),
        Setting("input", { input("in", input) }, { it.inputChannels["in"] shouldBeSameInstanceAs input }),
        Setting("standard-input", { standardInput(input) }, { it.standardInput shouldBeSameInstanceAs input }),
        Setting(
            "outputs",
            { outputs(OutputStore.fromStandard() + ("out" to output)) },
            { it.outputChannels["out"] shouldBeSameInstanceAs output },
        ),
        Setting("output", { output("out", output) }, { it.outputChannels["out"] shouldBeSameInstanceAs output }),
        Setting("standard-output", { standardOutput(output) }, { it.standardOutput shouldBeSameInstanceAs output }),
        Setting("standard-error", { standardError(output) }, { it.standardError shouldBeSameInstanceAs output }),
        Setting("warnings", { warnings(warningsChannel) }, { it.warnings shouldBeSameInstanceAs warningsChannel }),
    )

private val builds: List<Pair<String, (SolverBuilder) -> Solver>> =
    listOf(
        "build" to { it.build() },
        "build-mutable" to { it.buildMutable() },
        "to-factory" to { it.toFactory().solverWithDefaultBuiltins() },
    )

/**
 * Settings [SolverBuilder.toFactory] can't carry over: a [SolverFactory] knows only the four standard channels, so
 * extra named ones are dropped, and its builtins can't be "none".
 */
private val notThroughFactories = setOf("inputs", "input", "outputs", "output", "no-builtins")

private val cases =
    settings.flatMap { setting ->
        builds
            .filter { (how) -> how != "to-factory" || setting.id !in notThroughFactories }
            .map { (how, build) -> Triple(setting, how, build) }
    }

class SolverBuilderSpec :
    FunSpec({
        withTests(nameFn = { (setting, how) -> "${setting.id}/$how" }, cases) { (setting, _, build) ->
            setting.check(build(Solver.prolog.newBuilder().(setting.configure)()))
        }

        test("build-mutable/builds-a-mutable-solver") {
            Solver.prolog
                .newBuilder()
                .buildMutable()
                .also { it.loadStaticKb(Theory.of(p(1))) }
                .staticKb
                .clauses
                .toList() shouldBe listOf(p(1))
        }

        test("channel-properties/read-back-what-was-set") {
            val builder = Solver.prolog.newBuilder()
            builder.standardInput = input
            builder.standardOutput = output
            builder.standardError = output
            builder.warnings = warningsChannel
            builder.standardInput shouldBeSameInstanceAs input
            builder.standardOutput shouldBeSameInstanceAs output
            builder.standardError shouldBeSameInstanceAs output
            builder.warnings shouldBeSameInstanceAs warningsChannel
        }

        test("equality/builders-with-the-same-settings-are-equal") {
            // each builder opens its own standard channels, so equal builders must share their stores
            val inputs = InputStore.fromStandard()
            val outputs = OutputStore.fromStandard()

            fun builder() =
                Solver.prolog
                    .newBuilder()
                    .inputs(inputs)
                    .outputs(outputs)
                    .staticKb(Theory.of(p(1)))
                    .flag("f", on)
            val first = builder()
            val second = builder()
            first shouldBe second
            first.hashCode() shouldBe second.hashCode()
            first shouldNotBe Solver.prolog.newBuilder()
            first.toString() shouldContain "flags="
        }

        test("to-factory/keeps-every-default-of-the-builder") {
            val factory =
                Solver.prolog
                    .newBuilder()
                    .builtins(custom)
                    .staticKb(p(1))
                    .standardOutput(output)
                    .toFactory()
            factory.defaultBuiltins shouldBe custom
            factory.defaultStaticKb.clauses.toList() shouldBe listOf(p(1))
            factory.defaultOutputChannel shouldBeSameInstanceAs output
            factory.defaultUnificator shouldBe Unificator.default
            // these have default bodies, which delegation would otherwise run against the original factory
            factory.mutableSolverWithDefaultBuiltins().libraries.aliases shouldBe setOf("custom")
            factory
                .newBuilder()
                .build()
                .staticKb.clauses
                .toList() shouldBe listOf(p(1))
        }
    })

private val prologBuiltins = Solver.prolog.defaultBuiltins.alias

/** [SolverFactory]'s conveniences, on the Prolog factory. */
class SolverFactorySpec :
    FunSpec({
        test("solver-with-default-builtins/adds-the-builtins-to-other-libraries") {
            Solver.prolog
                .solverWithDefaultBuiltins(otherLibraries = Runtime.of(custom))
                .libraries.aliases shouldBe
                setOf(prologBuiltins, "custom")
        }

        test("mutable-solver-with-default-builtins/adds-the-builtins-to-other-libraries") {
            Solver.prolog
                .mutableSolverWithDefaultBuiltins(otherLibraries = Runtime.of(custom))
                .libraries
                .aliases shouldBe setOf(prologBuiltins, "custom")
        }

        test("solver-of/channels-default-to-the-factory-ones") {
            Solver.prolog.solverOf(stdIn = input).standardInput shouldBeSameInstanceAs input
            Solver.prolog.mutableSolverOf(stdOut = output).standardOutput shouldBeSameInstanceAs output
        }
    })
