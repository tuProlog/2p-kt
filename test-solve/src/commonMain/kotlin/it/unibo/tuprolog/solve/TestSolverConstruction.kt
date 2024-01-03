package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.library.Library

interface TestSolverConstruction<T : Solver, MT : MutableSolver> {
    companion object {
        inline fun <reified T : Solver, reified MT : MutableSolver> prototype(
            factory: SolverFactory,
            defaultBuiltins: Library,
        ): TestSolverConstruction<T, MT> = TestSolverConstructionImpl(factory, defaultBuiltins, T::class, MT::class)
    }

    fun testCreatingEmptySolver()

    fun testCreatingCustomSolver()

    fun testCreatingSolverWithDefaultBuiltins()

    fun testCreatingCustomSolverWithDefaultBuiltins()

    fun testCreatingEmptyMutableSolver()

    fun testCreatingCustomMutableSolver()

    fun testCreatingMutableSolverWithDefaultBuiltins()

    fun testCreatingCustomMutableSolverWithDefaultBuiltins()

    fun testBuildingEmptySolver()

    fun testBuildingCustomSolver()

    fun testBuildingSolverWithDefaultBuiltins()

    fun testBuildingCustomSolverWithDefaultBuiltins()

    fun testBuildingEmptyMutableSolver()

    fun testBuildingCustomMutableSolver()

    fun testBuildingMutableSolverWithDefaultBuiltins()

    fun testBuildingCustomMutableSolverWithDefaultBuiltins()
}
