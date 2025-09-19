package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import org.junit.Test

class TestClassicCreation :
    TestCreation,
    SolverFactory by ClassicSolverFactory {
    private val prototype = TestCreation.prototype(this)

    @Test
    override fun mostProperConstructorIsSelectedWhenInstantiatingTypeRef() {
        prototype.mostProperConstructorIsSelectedWhenInstantiatingTypeRef()
    }

    @Test
    override fun constructorCanBeSelectedViaExplicitCastWhenInstantiatingTypeRef() {
        prototype.constructorCanBeSelectedViaExplicitCastWhenInstantiatingTypeRef()
    }

    @Test
    override fun constructorSelectionMayFailWhenInstantiatingTypeRef() {
        prototype.constructorSelectionMayFailWhenInstantiatingTypeRef()
    }
}
