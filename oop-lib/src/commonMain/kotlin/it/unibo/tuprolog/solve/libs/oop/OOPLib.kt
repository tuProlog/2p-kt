package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier.XFX
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.libs.oop.primitives.*
import it.unibo.tuprolog.solve.libs.oop.primitives.NullRef
import it.unibo.tuprolog.solve.libs.oop.primitives.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.primitives.Ref
import it.unibo.tuprolog.solve.libs.oop.primitives.TypeRef
import it.unibo.tuprolog.solve.libs.oop.rules.Alias
import it.unibo.tuprolog.solve.libs.oop.rules.ColonEquals
import it.unibo.tuprolog.solve.libs.oop.rules.LeftArrow
import it.unibo.tuprolog.solve.libs.oop.rules.Returns
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.theory.Theory

internal expect val platformSpecificAliases: Array<Alias>

object OOPLib : AliasedLibrary by Library.aliased(
    operatorSet = OperatorSet(
        Operator("<-", XFX, 800),
        Operator("returns", XFX, 850),
        Operator(":=", XFX, 850),
        Operator("as", XFX, 200),
//        Operator(".", XFX, 600)
    ),
    theory = Theory.indexedOf(sequenceOf(
        LeftArrow,
        Returns,
        ColonEquals,
        Alias.forType("array", Array::class),
        Alias.forType("arraylist", ArrayList::class),
        *platformSpecificAliases
    ).map { it.wrappedImplementation }),
    primitives = sequenceOf<PrimitiveWrapper<*>>(
        NewObject,
        FindType,
        InvokeMethod,
        InvokeStrict,
        TypeRef,
        ObjectRef,
        NullRef,
        Ref
    ).map { it.descriptionPair }.toMap(),
    alias = "prolog.oop"
)