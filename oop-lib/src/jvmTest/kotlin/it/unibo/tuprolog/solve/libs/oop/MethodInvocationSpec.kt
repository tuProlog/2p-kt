package it.unibo.tuprolog.solve.libs.oop

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod

class MethodInvocationSpec :
    FunSpec({
        include(invocationCases(InvokeMethod.functor) { Atom.of(it.type.name) })
    })
