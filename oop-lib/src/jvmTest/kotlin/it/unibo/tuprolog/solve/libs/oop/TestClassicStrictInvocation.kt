package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import org.junit.Test

class TestClassicStrictInvocation :
    TestStrictInvocation,
    SolverFactory by ClassicSolverFactory {
    private val prototype = TestStrictInvocation.prototype(this)

    @Test
    override fun mostProperOverloadIsSelectedWhenInvokingObjectRefMethod() {
        prototype.mostProperOverloadIsSelectedWhenInvokingObjectRefMethod()
    }

    @Test
    override fun overloadCanBeSelectedViaExplicitCastWhenInvokingObjectRefMethod() {
        prototype.overloadCanBeSelectedViaExplicitCastWhenInvokingObjectRefMethod()
    }

    @Test
    override fun overloadSelectionMayFailWhenInvokingObjectRefMethod() {
        prototype.overloadSelectionMayFailWhenInvokingObjectRefMethod()
    }

    @Test
    override fun mostProperOverloadIsSelectedWhenInvokingTypeRefMethod() {
        prototype.mostProperOverloadIsSelectedWhenInvokingTypeRefMethod()
    }

    @Test
    override fun overloadCanBeSelectedViaExplicitCastWhenInvokingTypeRefMethod() {
        prototype.overloadCanBeSelectedViaExplicitCastWhenInvokingTypeRefMethod()
    }

    @Test
    override fun overloadSelectionMayFailWhenInvokingTypeRefMethod() {
        prototype.overloadSelectionMayFailWhenInvokingTypeRefMethod()
    }
}
