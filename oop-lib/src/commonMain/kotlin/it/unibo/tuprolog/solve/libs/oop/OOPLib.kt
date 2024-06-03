package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Termificator
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier.FX
import it.unibo.tuprolog.core.operators.Specifier.XFX
import it.unibo.tuprolog.core.operators.Specifier.XFY
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.library.impl.AbstractLibrary
import it.unibo.tuprolog.solve.libs.oop.impl.BaseOOPContext
import it.unibo.tuprolog.solve.libs.oop.primitives.ArrayItems
import it.unibo.tuprolog.solve.libs.oop.primitives.Assign
import it.unibo.tuprolog.solve.libs.oop.primitives.Cast
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeStrict
import it.unibo.tuprolog.solve.libs.oop.primitives.ListItems
import it.unibo.tuprolog.solve.libs.oop.primitives.NewObject3
import it.unibo.tuprolog.solve.libs.oop.primitives.NullRef
import it.unibo.tuprolog.solve.libs.oop.primitives.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.primitives.Register
import it.unibo.tuprolog.solve.libs.oop.primitives.SetItems
import it.unibo.tuprolog.solve.libs.oop.primitives.Type
import it.unibo.tuprolog.solve.libs.oop.primitives.TypeRef
import it.unibo.tuprolog.solve.libs.oop.primitives.Unregister
import it.unibo.tuprolog.solve.libs.oop.rules.ColonEquals
import it.unibo.tuprolog.solve.libs.oop.rules.Dot
import it.unibo.tuprolog.solve.libs.oop.rules.FluentReduce
import it.unibo.tuprolog.solve.libs.oop.rules.NewObject2
import it.unibo.tuprolog.solve.libs.oop.rules.PropertyReduce
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper

class OOPLib(private val oopContext: OOPContext) : AbstractLibrary(), OOPContext by oopContext {
    constructor(
        termificator: Termificator = Termificator.default(),
        typeFactory: TypeFactory = TypeFactory.default,
        objectifier: Objectifier = Objectifier.of(typeFactory),
    ) : this(BaseOOPContext(termificator, objectifier, typeFactory))

    override val alias: String
        get() = "prolog.oop"

    override val primitives: Map<Signature, Primitive> by lazy {
        listOf<PrimitiveWrapper<*>>(
            ArrayItems(this),
            Assign(this),
            Cast(this),
            Type(this),
            InvokeMethod(this),
            InvokeStrict(this),
            ListItems(this),
            NewObject3(this),
            NullRef,
            ObjectRef,
            Register,
            SetItems(this),
            TypeRef,
            Unregister,
        ).associate { it.descriptionPair }
    }

    override val clauses: List<Clause> by lazy {
        listOf(
            ColonEquals.Cast,
            ColonEquals.Invocation,
            ColonEquals.Assignment,
            Dot,
            FluentReduce.Recursive,
            FluentReduce.Couple,
            FluentReduce.Trivial,
            NewObject2,
            PropertyReduce.Recursive,
            PropertyReduce.Base,
            *defaultAliases,
        ).map { it.implementation }
    }

    override val operators: OperatorSet by lazy {
        OperatorSet(
            Operator(".", XFY, 800),
            Operator(":=", XFX, 850),
            Operator("as", XFX, 200),
            Operator("$", FX, 100),
        )
    }
}
