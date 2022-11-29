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

class TestSolverConstructionImpl<T : Solver, MT : MutableSolver>(
    private val factory: SolverFactory,
    private val defaultBuiltIns: Library,
    private val solverType: KClass<T>,
    private val mutableSolverType: KClass<MT>
) : TestSolverConstruction<T, MT> {

    private object Dummy {
        val library = libraryOf("dummy", Sleep)
        val runtime = library.toRuntime()
        val flags = FlagStore.of(Unknown { ERROR }, LastCallOptimization { OFF })
        val theory1 = logicProgramming {
            theoryOf(fact { "a" }, fact { "b" }, fact { "c" })
        }
        val theory2 = logicProgramming {
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
        val outputs = OutputStore.fromStandard(
            OutputChannel.stdOut(),
            OutputChannel.stdErr(),
            OutputChannel.warn()
        )
    }

    private fun Solver.asserHasDefaultProperties(mutable: Boolean) {
        assertHas(
            libraries = Default.libraries,
            staticKb = Default.staticKb,
            dynamicKb = Default.dynamicKb,
            flags = Default.flags,
            inputs = Default.inputs,
            outputs = Default.outputs
        )
        assertEquals(true, solverType.isInstance(this))
        assertEquals(mutable, mutableSolverType.isInstance(this))
    }

    override fun testCreatingEmptySolver() {
        factory.solverOf().asserHasDefaultProperties(mutable = false)
    }

    private fun Solver.asserHasCustomProperties(mutable: Boolean) {
        assertHas(
            libraries = Dummy.runtime,
            staticKb = Dummy.theory1,
            dynamicKb = Dummy.theory2,
            flags = Dummy.flags,
            inputs = Dummy.inputs,
            outputs = Dummy.outputs
        )
        assertEquals(mutable, this is MutableSolver)
    }

    override fun testCreatingCustomSolver() {
        factory.solverOf(
            libraries = Dummy.runtime,
            flags = Dummy.flags,
            staticKb = Dummy.theory1,
            dynamicKb = Dummy.theory2,
            stdIn = Dummy.input,
            stdOut = Dummy.output,
            stdErr = Dummy.error,
            warnings = Dummy.warning
        ).asserHasCustomProperties(mutable = false)
    }

    private fun Solver.asserHasDefaultBultinsAndProperties(mutable: Boolean) {
        assertHas(
            libraries = defaultBuiltIns.toRuntime(),
            staticKb = Default.staticKb,
            dynamicKb = Default.dynamicKb,
            flags = Default.flags,
            inputs = Default.inputs,
            outputs = Default.outputs
        )
        assertEquals(mutable, this is MutableSolver)
    }

    override fun testCreatingSolverWithDefaultBuiltins() {
        factory.solverWithDefaultBuiltins().asserHasDefaultBultinsAndProperties(mutable = false)
    }

    private fun Solver.asserHasDefaultBultinsAndCustomProperties(mutable: Boolean) {
        assertHas(
            libraries = Dummy.runtime + defaultBuiltIns,
            staticKb = Dummy.theory1,
            dynamicKb = Dummy.theory2,
            flags = Dummy.flags,
            inputs = Dummy.inputs,
            outputs = Dummy.outputs
        )
        assertEquals(mutable, this is MutableSolver)
    }

    override fun testCreatingCustomSolverWithDefaultBuiltins() {
        factory.solverWithDefaultBuiltins(
            otherLibraries = Dummy.runtime,
            flags = Dummy.flags,
            staticKb = Dummy.theory1,
            dynamicKb = Dummy.theory2,
            stdIn = Dummy.input,
            stdOut = Dummy.output,
            stdErr = Dummy.error,
            warnings = Dummy.warning
        ).asserHasDefaultBultinsAndCustomProperties(mutable = false)
    }

    override fun testCreatingEmptyMutableSolver() {
        factory.mutableSolverOf().asserHasDefaultProperties(mutable = true)
    }

    override fun testCreatingCustomMutableSolver() {
        factory.mutableSolverOf(
            libraries = Dummy.runtime,
            flags = Dummy.flags,
            staticKb = Dummy.theory1,
            dynamicKb = Dummy.theory2,
            stdIn = Dummy.input,
            stdOut = Dummy.output,
            stdErr = Dummy.error,
            warnings = Dummy.warning
        ).asserHasCustomProperties(mutable = true)
    }

    override fun testCreatingMutableSolverWithDefaultBuiltins() {
        factory.mutableSolverWithDefaultBuiltins().asserHasDefaultBultinsAndProperties(mutable = true)
    }

    override fun testCreatingCustomMutableSolverWithDefaultBuiltins() {
        factory.mutableSolverWithDefaultBuiltins(
            otherLibraries = Dummy.runtime,
            flags = Dummy.flags,
            staticKb = Dummy.theory1,
            dynamicKb = Dummy.theory2,
            stdIn = Dummy.input,
            stdOut = Dummy.output,
            stdErr = Dummy.error,
            warnings = Dummy.warning
        ).asserHasDefaultBultinsAndCustomProperties(mutable = true)
    }

    override fun testBuildingEmptySolver() {
        factory.newBuilder()
            .noBuiltins()
            .build()
            .asserHasDefaultProperties(mutable = false)
    }

    override fun testBuildingCustomSolver() {
        factory.newBuilder()
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

    override fun testBuildingSolverWithDefaultBuiltins() {
        factory.newBuilder()
            .build()
            .asserHasDefaultBultinsAndProperties(mutable = false)
    }

    override fun testBuildingCustomSolverWithDefaultBuiltins() {
        factory.newBuilder()
            .runtime(Dummy.runtime)
            .flags(Dummy.flags)
            .staticKb(Dummy.theory1)
            .dynamicKb(Dummy.theory2)
            .inputs(Dummy.inputs)
            .outputs(Dummy.outputs)
            .build()
            .asserHasDefaultBultinsAndCustomProperties(mutable = false)
    }

    override fun testBuildingEmptyMutableSolver() {
        factory.newBuilder()
            .noBuiltins()
            .buildMutable()
            .asserHasDefaultProperties(mutable = true)
    }

    override fun testBuildingCustomMutableSolver() {
        factory.newBuilder()
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

    override fun testBuildingMutableSolverWithDefaultBuiltins() {
        factory.newBuilder()
            .buildMutable()
            .asserHasDefaultBultinsAndProperties(mutable = true)
    }

    override fun testBuildingCustomMutableSolverWithDefaultBuiltins() {
        factory.newBuilder()
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
