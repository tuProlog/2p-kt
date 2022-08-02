package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.stdlib.primitive.Sleep
import it.unibo.tuprolog.theory.Theory
import kotlin.reflect.KClass

class TestSolverConstructionImpl<T : Solver, MT : MutableSolver>(
    private val factory: SolverFactory,
    override val solverType: KClass<T>,
    override val mutableSolverType: KClass<MT>
) : TestSolverConstruction<T, MT> {

    private object Dummy {
        val library = libraryOf("dummy", Sleep)
        val runtime = library.toRuntime()
        val theory1 = prolog {
            theoryOf(fact { "a" }, fact { "b" }, fact { "c" })
        }
        val theory2 = prolog {
            theoryOf(fact { "nat"("z") }, rule { "nat"("s"(X)) impliedBy "nat"(X) })
        }
        val input = InputChannel.of("")
        val output = OutputChannel.of<String> { }
        val error = OutputChannel.of<String> { }
        val warning = OutputChannel.of<Warning> { }
    }

    private object Default {
        val staticKb = Theory.empty()
        val dynamicKb = Theory.empty()
        val flags = FlagStore.DEFAULT
        val inputs = InputStore.fromStandard(InputChannel.stdIn())
        val outputs = OutputStore.fromStandard(
            OutputChannel.stdOut(),
            OutputChannel.stdErr(),
            OutputChannel.warn()
        )
    }

    override fun testCreatingEmptySolver() {
        val solver = factory.solverOf()
        solver.assertHas(
            libraries = Runtime.empty(),
            staticKb = Theory.empty(),
            dynamicKb = Theory.empty(),
            flags = FlagStore.DEFAULT,
            inputs = InputStore.fromStandard(InputChannel.stdIn()),
            outputs = OutputStore.fromStandard(
                OutputChannel.stdOut()
            )
        )
    }

    override fun testCreatingCustomSolver() {
        TODO("Not yet implemented")
    }

    override fun testCreatingSolverWithDefaultBuiltins() {
        TODO("Not yet implemented")
    }

    override fun testCreatingCustomSolverWithDefaultBuiltins() {
        TODO("Not yet implemented")
    }

    override fun testCreatingEmptyMutableSolver() {
        TODO("Not yet implemented")
    }

    override fun testCreatingCustomMutableSolver() {
        TODO("Not yet implemented")
    }

    override fun testCreatingMutableSolverWithDefaultBuiltins() {
        TODO("Not yet implemented")
    }

    override fun testCreatingCustomMutableSolverWithDefaultBuiltins() {
        TODO("Not yet implemented")
    }

    override fun testBuildingEmptySolver() {
        TODO("Not yet implemented")
    }

    override fun testBuildingCustomSolver() {
        TODO("Not yet implemented")
    }

    override fun testBuildingSolverWithDefaultBuiltins() {
        TODO("Not yet implemented")
    }

    override fun testBuildingCustomSolverWithDefaultBuiltins() {
        TODO("Not yet implemented")
    }

    override fun testBuildingEmptyMutableSolver() {
        TODO("Not yet implemented")
    }

    override fun testBuildingCustomMutableSolver() {
        TODO("Not yet implemented")
    }

    override fun testBuildingMutableSolverWithDefaultBuiltins() {
        TODO("Not yet implemented")
    }

    override fun testBuildingCustomMutableSolverWithDefaultBuiltins() {
        TODO("Not yet implemented")
    }
}
