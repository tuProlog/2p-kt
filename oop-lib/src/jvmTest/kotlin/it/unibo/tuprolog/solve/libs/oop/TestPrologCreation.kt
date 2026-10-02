package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import org.junit.Test

class TestPrologCreation :
    TestCreation,
    SolverFactory by PrologSolverFactory {
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
