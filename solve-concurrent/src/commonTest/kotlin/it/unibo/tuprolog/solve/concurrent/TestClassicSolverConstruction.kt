package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestSolverConstruction
import it.unibo.tuprolog.solve.concurrent.stdlib.DefaultBuiltins
import kotlin.test.Test

class TestClassicSolverConstruction :
    TestSolverConstruction<ConcurrentSolver, MutableConcurrentSolver>,
    SolverFactory by ConcurrentSolverFactory {
    private val prototype =
        TestSolverConstruction.prototype<ConcurrentSolver, MutableConcurrentSolver>(
            this,
            DefaultBuiltins,
        )

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
    override fun testCreatingEmptyMutableSolver() {
        prototype.testCreatingEmptyMutableSolver()
    }

    @Test
    override fun testCreatingCustomMutableSolver() {
        prototype.testCreatingCustomMutableSolver()
    }

    @Test
    override fun testCreatingMutableSolverWithDefaultBuiltins() {
        prototype.testCreatingMutableSolverWithDefaultBuiltins()
    }

    @Test
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
    override fun testBuildingEmptyMutableSolver() {
        prototype.testBuildingEmptyMutableSolver()
    }

    @Test
    override fun testBuildingCustomMutableSolver() {
        prototype.testBuildingCustomMutableSolver()
    }

    @Test
    override fun testBuildingMutableSolverWithDefaultBuiltins() {
        prototype.testBuildingMutableSolverWithDefaultBuiltins()
    }

    @Test
    override fun testBuildingCustomMutableSolverWithDefaultBuiltins() {
        prototype.testBuildingCustomMutableSolverWithDefaultBuiltins()
    }
}
