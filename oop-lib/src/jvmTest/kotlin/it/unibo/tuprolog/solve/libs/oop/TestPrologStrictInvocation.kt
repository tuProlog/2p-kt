package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import org.junit.Test

class TestPrologStrictInvocation :
    TestStrictInvocation,
    SolverFactory by PrologSolverFactory {
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
