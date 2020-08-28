package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier.XFX
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.libs.oop.primitives.NewObject
import it.unibo.tuprolog.solve.libs.oop.primitives.FindType
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeStrict
import it.unibo.tuprolog.solve.libs.oop.rules.LeftArrow
import it.unibo.tuprolog.solve.libs.oop.rules.Returns
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.theory.Theory

object OOPLib : AliasedLibrary by Library.aliased(
    operatorSet = OperatorSet(
        Operator("<-", XFX, 800),
        Operator("returns", XFX, 850)//,
//        Operator("as", XFX, 200),
//        Operator(".", XFX, 600)
    ),
    theory = Theory.indexedOf(sequenceOf(
        LeftArrow,
        Returns
    ).map { it.wrappedImplementation }),
    primitives = sequenceOf<PrimitiveWrapper<*>>(
        NewObject,
        FindType,
        InvokeMethod,
        InvokeStrict
    ).map { it.descriptionPair }.toMap(),
    alias = "prolog.oop"
)