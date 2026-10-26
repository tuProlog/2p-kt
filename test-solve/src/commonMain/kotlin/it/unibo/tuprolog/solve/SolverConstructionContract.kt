package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.LastCallOptimization
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.solve.flags.invoke
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.library.toRuntime
import it.unibo.tuprolog.solve.stdlib.primitive.Sleep
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.reflect.KClass
import kotlin.test.assertEquals

/**
 * Conformance cases for [SolverFactory]'s construction APIs (`solverOf`, `mutableSolverOf`, the `...WithDefaultBuiltins`
 * variants and [SolverFactory.newBuilder]): every combination yields a solver of the expected type ([T] or [MT])
 * with exactly the requested or default properties.
 */
inline fun <reified T : Solver, reified MT : MutableSolver> solverConstructionContract(
    factory: SolverFactory,
    defaultBuiltins: Library,
    disabled: Set<String> = emptySet(),
) = solverConstructionContract(factory, defaultBuiltins, T::class, MT::class, disabled)

fun solverConstructionContract(
    factory: SolverFactory,
    defaultBuiltins: Library,
    solverType: KClass<out Solver>,
    mutableSolverType: KClass<out MutableSolver>,
    disabled: Set<String> = emptySet(),
) = contract(factory, disabled) {
    fun Solver.asserHasDefaultProperties(mutable: Boolean) {
        assertHas(
            libraries = Default.libraries,
            staticKb = Default.staticKb,
            dynamicKb = Default.dynamicKb,
            flags = Default.flags,
            inputs = Default.inputs,
            outputs = Default.outputs,
        )
        assertEquals(true, solverType.isInstance(this))
        assertEquals(mutable, mutableSolverType.isInstance(this))
    }

    fun Solver.asserHasCustomProperties(mutable: Boolean) {
        assertHas(
            libraries = Dummy.runtime,
            staticKb = Dummy.theory1,
            dynamicKb = Dummy.theory2,
            flags = Dummy.flags,
            inputs = Dummy.inputs,
            outputs = Dummy.outputs,
        )
        assertEquals(mutable, this is MutableSolver)
    }

    fun Solver.asserHasDefaultBultinsAndProperties(mutable: Boolean) {
        assertHas(
            libraries = defaultBuiltins.toRuntime(),
            staticKb = Default.staticKb,
            dynamicKb = Default.dynamicKb,
            flags = Default.flags,
            inputs = Default.inputs,
            outputs = Default.outputs,
        )
        assertEquals(mutable, this is MutableSolver)
    }

    fun Solver.asserHasDefaultBultinsAndCustomProperties(mutable: Boolean) {
        assertHas(
            libraries = Dummy.runtime + defaultBuiltins,
            staticKb = Dummy.theory1,
            dynamicKb = Dummy.theory2,
            flags = Dummy.flags,
            inputs = Dummy.inputs,
            outputs = Dummy.outputs,
        )
        assertEquals(mutable, this is MutableSolver)
    }

    // Tests that [SolverFactory.solverOf], called with no argument, returns a [T] with every default property.
    scenario("solver-construction/creating-empty-solver") {
        factory.solverOf().asserHasDefaultProperties(mutable = false)
    }

    // Tests that [SolverFactory.solverOf], called with an explicit custom library runtime, flags, static/dynamic
    // theory and I/O channels, returns a [T] configured with exactly those values (and no default built-ins).
    scenario("solver-construction/creating-custom-solver") {
        factory
            .solverOf(
                libraries = Dummy.runtime,
                flags = Dummy.flags,
                staticKb = Dummy.theory1,
                dynamicKb = Dummy.theory2,
                stdIn = Dummy.input,
                stdOut = Dummy.output,
                stdErr = Dummy.error,
                warnings = Dummy.warning,
            ).asserHasCustomProperties(mutable = false)
    }

    // Tests that `SolverFactory.solverWithDefaultBuiltins()`, called with no argument, returns a [T] whose only
    // library is the suite's `defaultBuiltins`, with every other property left at its default.
    scenario("solver-construction/creating-solver-with-default-builtins") {
        factory.solverWithDefaultBuiltins().asserHasDefaultBultinsAndProperties(mutable = false)
    }

    // Tests that `SolverFactory.solverWithDefaultBuiltins`, called with an explicit custom library runtime, flags,
    // static/dynamic theory and I/O channels, returns a [T] whose libraries are the custom runtime *plus* the
    // suite's `defaultBuiltins`, with every other property matching the custom configuration.
    scenario("solver-construction/creating-custom-solver-with-default-builtins") {
        factory
            .solverWithDefaultBuiltins(
                otherLibraries = Dummy.runtime,
                flags = Dummy.flags,
                staticKb = Dummy.theory1,
                dynamicKb = Dummy.theory2,
                stdIn = Dummy.input,
                stdOut = Dummy.output,
                stdErr = Dummy.error,
                warnings = Dummy.warning,
            ).asserHasDefaultBultinsAndCustomProperties(mutable = false)
    }

    // Same as [testCreatingEmptySolver], but for [SolverFactory.mutableSolverOf], expecting an [MT].
    scenario("solver-construction/creating-empty-mutable-solver") {
        factory.mutableSolverOf().asserHasDefaultProperties(mutable = true)
    }

    // Same as [testCreatingCustomSolver], but for `SolverFactory.mutableSolverOf`, expecting an [MT].
    scenario("solver-construction/creating-custom-mutable-solver") {
        factory
            .mutableSolverOf(
                libraries = Dummy.runtime,
                flags = Dummy.flags,
                staticKb = Dummy.theory1,
                dynamicKb = Dummy.theory2,
                stdIn = Dummy.input,
                stdOut = Dummy.output,
                stdErr = Dummy.error,
                warnings = Dummy.warning,
            ).asserHasCustomProperties(mutable = true)
    }

    // Same as [testCreatingSolverWithDefaultBuiltins], but for `SolverFactory.mutableSolverWithDefaultBuiltins`,
    // expecting an [MT].
    scenario("solver-construction/creating-mutable-solver-with-default-builtins") {
        factory.mutableSolverWithDefaultBuiltins().asserHasDefaultBultinsAndProperties(mutable = true)
    }

    // Same as [testCreatingCustomSolverWithDefaultBuiltins], but for `SolverFactory.mutableSolverWithDefaultBuiltins`,
    // expecting an [MT].
    scenario("solver-construction/creating-custom-mutable-solver-with-default-builtins") {
        factory
            .mutableSolverWithDefaultBuiltins(
                otherLibraries = Dummy.runtime,
                flags = Dummy.flags,
                staticKb = Dummy.theory1,
                dynamicKb = Dummy.theory2,
                stdIn = Dummy.input,
                stdOut = Dummy.output,
                stdErr = Dummy.error,
                warnings = Dummy.warning,
            ).asserHasDefaultBultinsAndCustomProperties(mutable = true)
    }

    // Tests that [SolverFactory.newBuilder]`().noBuiltins().build()` returns a [T] with every default property
    // (equivalent to [testCreatingEmptySolver], but through the builder API).
    scenario("solver-construction/building-empty-solver") {
        factory
            .newBuilder()
            .noBuiltins()
            .build()
            .asserHasDefaultProperties(mutable = false)
    }

    // Tests that [SolverFactory.newBuilder]`().noBuiltins()`, configured with a custom runtime, flags,
    // static/dynamic theory and I/O channels, then `.build()`, returns a [T] matching that configuration
    // (equivalent to [testCreatingCustomSolver], but through the builder API).
    scenario("solver-construction/building-custom-solver") {
        factory
            .newBuilder()
            .noBuiltins()
            .runtime(Dummy.runtime)
            .flags(Dummy.flags)
            .staticKb(Dummy.theory1)
            .dynamicKb(Dummy.theory2)
            .inputs(Dummy.inputs)
            .outputs(Dummy.outputs)
            .build()
            .asserHasCustomProperties(mutable = false)
    }

    // Tests that [SolverFactory.newBuilder]`().build()` (without calling `.noBuiltins()`) returns a [T] whose only
    // library is the suite's `defaultBuiltins` (equivalent to [testCreatingSolverWithDefaultBuiltins], but through
    // the builder API).
    scenario("solver-construction/building-solver-with-default-builtins") {
        factory
            .newBuilder()
            .build()
            .asserHasDefaultBultinsAndProperties(mutable = false)
    }

    // Tests that [SolverFactory.newBuilder]`()`, configured with a custom runtime, flags, static/dynamic theory and
    // I/O channels, then `.build()` (without calling `.noBuiltins()`), returns a [T] whose libraries are the custom
    // runtime plus `defaultBuiltins` (equivalent to [testCreatingCustomSolverWithDefaultBuiltins], but through the
    // builder API).
    scenario("solver-construction/building-custom-solver-with-default-builtins") {
        factory
            .newBuilder()
            .runtime(Dummy.runtime)
            .flags(Dummy.flags)
            .staticKb(Dummy.theory1)
            .dynamicKb(Dummy.theory2)
            .inputs(Dummy.inputs)
            .outputs(Dummy.outputs)
            .build()
            .asserHasDefaultBultinsAndCustomProperties(mutable = false)
    }

    // Same as [testBuildingEmptySolver], but calling `.buildMutable()`, expecting an [MT].
    scenario("solver-construction/building-empty-mutable-solver") {
        factory
            .newBuilder()
            .noBuiltins()
            .buildMutable()
            .asserHasDefaultProperties(mutable = true)
    }

    // Same as [testBuildingCustomSolver], but calling `.buildMutable()`, expecting an [MT].
    scenario("solver-construction/building-custom-mutable-solver") {
        factory
            .newBuilder()
            .noBuiltins()
            .runtime(Dummy.runtime)
            .flags(Dummy.flags)
            .staticKb(Dummy.theory1)
            .dynamicKb(Dummy.theory2)
            .inputs(Dummy.inputs)
            .outputs(Dummy.outputs)
            .buildMutable()
            .asserHasCustomProperties(mutable = true)
    }

    // Same as [testBuildingSolverWithDefaultBuiltins], but calling `.buildMutable()`, expecting an [MT].
    scenario("solver-construction/building-mutable-solver-with-default-builtins") {
        factory
            .newBuilder()
            .buildMutable()
            .asserHasDefaultBultinsAndProperties(mutable = true)
    }

    // Same as [testBuildingCustomSolverWithDefaultBuiltins], but calling `.buildMutable()`, expecting an [MT].
    scenario("solver-construction/building-custom-mutable-solver-with-default-builtins") {
        factory
            .newBuilder()
            .runtime(Dummy.runtime)
            .flags(Dummy.flags)
            .staticKb(Dummy.theory1)
            .dynamicKb(Dummy.theory2)
            .inputs(Dummy.inputs)
            .outputs(Dummy.outputs)
            .buildMutable()
            .asserHasDefaultBultinsAndCustomProperties(mutable = true)
    }
}

private object Dummy {
    val library = libraryOf("dummy", Sleep)
    val runtime = library.toRuntime()
    val flags = FlagStore.of(Unknown { ERROR }, LastCallOptimization { OFF })
    val theory1 =
        logicProgramming {
            theoryOf(fact { "a" }, fact { "b" }, fact { "c" })
        }
    val theory2 =
        logicProgramming {
            theoryOf(fact { "nat"("z") }, rule { "nat"("s"(X)) impliedBy "nat"(X) })
        }
    val input = InputChannel.of("")
    val output = OutputChannel.of<String> { }
    val error = OutputChannel.of<String> { }
    val warning = OutputChannel.of<Warning> { }
    val inputs = InputStore.fromStandard(input)
    val outputs = OutputStore.fromStandard(output, error, warning)
}

private object Default {
    val unificator: Unificator = Unificator.default
    val libraries = Runtime.empty()
    val staticKb = Theory.empty(unificator)
    val dynamicKb = Theory.empty(unificator)
    val flags = FlagStore.DEFAULT
    val inputs = InputStore.fromStandard(InputChannel.stdIn())
    val outputs =
        OutputStore.fromStandard(
            OutputChannel.stdOut(),
            OutputChannel.stdErr(),
            OutputChannel.warn(),
        )
}
