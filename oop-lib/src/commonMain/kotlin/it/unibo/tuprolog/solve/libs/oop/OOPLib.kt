package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier.XFX
import it.unibo.tuprolog.core.operators.Specifier.XFY
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.libs.oop.primitives.Assign
import it.unibo.tuprolog.solve.libs.oop.primitives.FindType
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeStrict
import it.unibo.tuprolog.solve.libs.oop.primitives.NewObject3
import it.unibo.tuprolog.solve.libs.oop.primitives.NullRef
import it.unibo.tuprolog.solve.libs.oop.primitives.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.primitives.Ref
import it.unibo.tuprolog.solve.libs.oop.primitives.TypeRef
import it.unibo.tuprolog.solve.libs.oop.rules.Alias
import it.unibo.tuprolog.solve.libs.oop.rules.ColonEquals
import it.unibo.tuprolog.solve.libs.oop.rules.Dot
import it.unibo.tuprolog.solve.libs.oop.rules.FluentReduce
import it.unibo.tuprolog.solve.libs.oop.rules.NewObject2
import it.unibo.tuprolog.solve.libs.oop.rules.PropertyReduce
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.theory.Theory

internal expect val platformSpecificAliases: Array<Alias>

object OOPLib : AliasedLibrary by
    Library.aliased(
        operatorSet = OperatorSet(
            Operator(".", XFY, 800),
            Operator(":=", XFX, 850),
            Operator("as", XFX, 200),
        ),
        theory = Theory.indexedOf(
            sequenceOf(
                ColonEquals.Invocation,
                ColonEquals.Assignment,
                Dot,
                FluentReduce.Recursive,
                FluentReduce.Couple,
                // FluentReduce.Base,
                FluentReduce.Trivial,
                NewObject2,
                PropertyReduce.Recursive,
                PropertyReduce.Base,
                Alias.forType("string", String::class),
                Alias.forType("array", Array::class),
                Alias.forType("arraylist", ArrayList::class),
                *platformSpecificAliases
            ).map { it.wrappedImplementation }
        ),
        primitives = sequenceOf<PrimitiveWrapper<*>>(
            Assign,
            NewObject3,
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
