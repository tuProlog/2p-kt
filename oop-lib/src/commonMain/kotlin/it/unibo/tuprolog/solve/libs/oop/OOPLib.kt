package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier.FX
import it.unibo.tuprolog.core.operators.Specifier.XFX
import it.unibo.tuprolog.core.operators.Specifier.XFY
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.library.impl.AbstractLibrary
import it.unibo.tuprolog.solve.libs.oop.primitives.ArrayItems
import it.unibo.tuprolog.solve.libs.oop.primitives.Assign
import it.unibo.tuprolog.solve.libs.oop.primitives.Cast
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeStrict
import it.unibo.tuprolog.solve.libs.oop.primitives.ListItems
import it.unibo.tuprolog.solve.libs.oop.primitives.NewObject3
import it.unibo.tuprolog.solve.libs.oop.primitives.NullRef
import it.unibo.tuprolog.solve.libs.oop.primitives.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.primitives.Ref
import it.unibo.tuprolog.solve.libs.oop.primitives.Register
import it.unibo.tuprolog.solve.libs.oop.primitives.SetItems
import it.unibo.tuprolog.solve.libs.oop.primitives.Type
import it.unibo.tuprolog.solve.libs.oop.primitives.TypeRef
import it.unibo.tuprolog.solve.libs.oop.primitives.Unregister
import it.unibo.tuprolog.solve.libs.oop.rules.Alias
import it.unibo.tuprolog.solve.libs.oop.rules.ColonEquals
import it.unibo.tuprolog.solve.libs.oop.rules.Dot
import it.unibo.tuprolog.solve.libs.oop.rules.FluentReduce
import it.unibo.tuprolog.solve.libs.oop.rules.NewObject2
import it.unibo.tuprolog.solve.libs.oop.rules.PropertyReduce
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

object OOPLib : AbstractLibrary() {

    override val alias: String
        get() = "prolog.oop"

    override val primitives: Map<Signature, Primitive>
        get() = listOf<PrimitiveWrapper<*>>(
            ArrayItems,
            Assign,
            Cast,
            Type,
            InvokeMethod,
            InvokeStrict,
            ListItems,
            NewObject3,
            NullRef,
            ObjectRef,
            Ref,
            Register,
            SetItems,
            TypeRef,
            Unregister
        ).map { it.descriptionPair }.toMap()

    override val clauses: List<Clause>
        get() = listOf(
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
            Alias.forType("string", String::class),
            Alias.forType("array", Array::class),
            Alias.forType("list", List::class),
            Alias.forType("arraylist", ArrayList::class),
            Alias.forType("map", Map::class),
            Alias.forType("hashmap", LinkedHashMap::class),
            Alias.forType("int", Int::class),
            Alias.forType("integer", Int::class),
            Alias.forType("double", Double::class),
            Alias.forType("float", Float::class),
            Alias.forType("long", Long::class),
            Alias.forType("short", Short::class),
            Alias.forType("byte", Byte::class),
            Alias.forType("char", Char::class),
            Alias.forType("bool", Boolean::class),
            Alias.forType("boolean", Boolean::class),
            Alias.forType("any", Any::class),
            Alias.forType("nothing", Nothing::class),
            Alias.forType("big_integer", BigInteger::class),
            Alias.forType("big_decimal", BigDecimal::class),
            *platformSpecificAliases
        ).map { it.implementation }

    override val operators: OperatorSet
        get() = OperatorSet(
            Operator(".", XFY, 800),
            Operator(":=", XFX, 850),
            Operator("as", XFX, 200),
            Operator("$", FX, 100),
        )
}
