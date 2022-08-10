package it.unibo.tuprolog.solve.streams

import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestSolverConstruction
import it.unibo.tuprolog.solve.streams.stdlib.DefaultBuiltins
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsSolverConstruction :
    TestSolverConstruction<StreamsSolver, MutableSolver>,
    SolverFactory by StreamsSolverFactory {

    private val prototype = TestSolverConstruction.prototype<StreamsSolver, MutableSolver>(this, DefaultBuiltins)

    @Test
    override fun testCreatingEmptySolver() {
        prototype.testCreatingEmptySolver()
    }

    @Test
    override fun testCreatingCustomSolver() {
        prototype.testCreatingCustomSolver()
    }

    @Test
    override fun testCreatingSolverWithDefaultBuiltins() {
        prototype.testCreatingSolverWithDefaultBuiltins()
    }

    @Test
    override fun testCreatingCustomSolverWithDefaultBuiltins() {
        prototype.testCreatingCustomSolverWithDefaultBuiltins()
    }

    @Test
    @Ignore
    override fun testCreatingEmptyMutableSolver() {
        prototype.testCreatingEmptyMutableSolver()
    }

    @Test
    @Ignore
    override fun testCreatingCustomMutableSolver() {
        prototype.testCreatingCustomMutableSolver()
    }

    @Test
    @Ignore
    override fun testCreatingMutableSolverWithDefaultBuiltins() {
        prototype.testCreatingMutableSolverWithDefaultBuiltins()
    }

    @Test
    @Ignore
    override fun testCreatingCustomMutableSolverWithDefaultBuiltins() {
        prototype.testCreatingCustomMutableSolverWithDefaultBuiltins()
    }

    @Test
    override fun testBuildingEmptySolver() {
        prototype.testBuildingEmptySolver()
    }

    @Test
    override fun testBuildingCustomSolver() {
        prototype.testBuildingCustomSolver()
    }

    @Test
    override fun testBuildingSolverWithDefaultBuiltins() {
        prototype.testBuildingSolverWithDefaultBuiltins()
    }

    @Test
    override fun testBuildingCustomSolverWithDefaultBuiltins() {
        prototype.testBuildingCustomSolverWithDefaultBuiltins()
    }

    @Test
    @Ignore
    override fun testBuildingEmptyMutableSolver() {
        prototype.testBuildingEmptyMutableSolver()
    }

    @Test
    @Ignore
    override fun testBuildingCustomMutableSolver() {
        prototype.testBuildingCustomMutableSolver()
    }

    @Test
    @Ignore
    override fun testBuildingMutableSolverWithDefaultBuiltins() {
        prototype.testBuildingMutableSolverWithDefaultBuiltins()
    }

    @Test
    @Ignore
    override fun testBuildingCustomMutableSolverWithDefaultBuiltins() {
        prototype.testBuildingCustomMutableSolverWithDefaultBuiltins()
    }
}
