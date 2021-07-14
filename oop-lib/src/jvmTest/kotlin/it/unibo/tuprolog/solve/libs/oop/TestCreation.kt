package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.SolverTest

interface TestCreation : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestCreation = TestCreationImpl(solverFactory)
    }
    fun mostProperConstructorIsSelectedWhenInstantiatingTypeRef()
    fun constructorCanBeSelectedViaExplicitCastWhenInstantiatingTypeRef()
    fun constructorSelectionMayFailWhenInstantiatingTypeRef()
}
