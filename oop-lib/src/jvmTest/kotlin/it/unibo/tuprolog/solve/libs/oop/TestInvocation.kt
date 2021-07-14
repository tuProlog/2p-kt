package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.SolverTest

interface TestInvocation : SolverTest {
    fun mostProperOverloadIsSelectedWhenInvokingObjectRefMethod()
    fun overloadCanBeSelectedViaExplicitCastWhenInvokingObjectRefMethod()
    fun overloadSelectionMayFailWhenInvokingObjectRefMethod()
    fun mostProperOverloadIsSelectedWhenInvokingTypeRefMethod()
    fun overloadCanBeSelectedViaExplicitCastWhenInvokingTypeRefMethod()
    fun overloadSelectionMayFailWhenInvokingTypeRefMethod()
}
