package it.unibo.tuprolog.solve

import kotlin.reflect.KClass

interface TestSolverConstruction<T : Solver, MT : MutableSolver> {

    companion object {
        inline fun <reified T : Solver, reified MT : MutableSolver> prototype(
            factory: SolverFactory
        ): TestSolverConstruction<T, MT> = TestSolverConstructionImpl(factory, T::class, MT::class)
    }

    val solverType: KClass<T>

    val mutableSolverType: KClass<MT>

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
