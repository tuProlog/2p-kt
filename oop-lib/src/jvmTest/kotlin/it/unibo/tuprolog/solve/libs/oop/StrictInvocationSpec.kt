package it.unibo.tuprolog.solve.libs.oop

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeStrict

class StrictInvocationSpec :
    FunSpec({
        include(invocationCases(InvokeStrict.functor) { ObjectRef.of(it.type.name) })
    })
