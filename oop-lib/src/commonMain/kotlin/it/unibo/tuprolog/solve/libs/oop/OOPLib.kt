package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier.FX
import it.unibo.tuprolog.core.operators.Specifier.XFX
import it.unibo.tuprolog.core.operators.Specifier.XFY
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.library.documentationOf
import it.unibo.tuprolog.solve.library.impl.AbstractLibrary
import it.unibo.tuprolog.solve.library.mergeDocumentation
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
import it.unibo.tuprolog.solve.rule.RuleWrapper
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

/**
 * The `prolog.oop` library, bridging the JVM/Kotlin object world with Prolog terms via reflection.
 *
 * Loading this [it.unibo.tuprolog.solve.library.Library] (e.g. via
 * `Runtime.of(OOPLib)`, see [it.unibo.tuprolog.solve.library.Runtime]) into a
 * [it.unibo.tuprolog.solve.Solver] lets Prolog clauses create JVM/Kotlin objects, invoke their
 * (possibly overloaded) methods and constructors, read and write their properties, and convert
 * values back and forth between the two worlds -- all without writing any custom [Primitive] by
 * hand. This is what makes it possible, for instance, to script the standard library or any
 * third-party JVM class directly from Prolog: `new_object('java.util.ArrayList', [], L), L.add(1)`.
 *
 * The library contributes:
 * - **primitives**: [it.unibo.tuprolog.solve.libs.oop.primitives.NewObject3] (`new_object/3`),
 *   [it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod] (`invoke_method/3`),
 *   [it.unibo.tuprolog.solve.libs.oop.primitives.InvokeStrict] (`invoke_strict/3`),
 *   [it.unibo.tuprolog.solve.libs.oop.primitives.Assign] (`assign/3`),
 *   [it.unibo.tuprolog.solve.libs.oop.primitives.Cast] (`cast/3`),
 *   [it.unibo.tuprolog.solve.libs.oop.primitives.Type] (`type/2`), the `*_items/2` family
 *   ([it.unibo.tuprolog.solve.libs.oop.primitives.ArrayItems],
 *   [it.unibo.tuprolog.solve.libs.oop.primitives.ListItems],
 *   [it.unibo.tuprolog.solve.libs.oop.primitives.SetItems]), the type testers
 *   ([it.unibo.tuprolog.solve.libs.oop.primitives.Ref],
 *   [it.unibo.tuprolog.solve.libs.oop.primitives.ObjectRef],
 *   [it.unibo.tuprolog.solve.libs.oop.primitives.TypeRef],
 *   [it.unibo.tuprolog.solve.libs.oop.primitives.NullRef]), and alias management
 *   ([it.unibo.tuprolog.solve.libs.oop.primitives.Register],
 *   [it.unibo.tuprolog.solve.libs.oop.primitives.Unregister]);
 * - **clauses**: the fluent-syntax sugar built on top of the primitives above (`:=/2`, `./2`,
 *   `new_object/2`, `fluent_reduce/2`, `property_reduce/3`) plus a battery of default
 *   [it.unibo.tuprolog.solve.libs.oop.rules.Alias] facts for common types (`string`, `array`,
 *   `list`, `int`, `big_integer`, ... and, on the JVM only, `system`, `math`, `stdout`, `stderr`,
 *   `stdin`; the JS target contributes no platform-specific alias);
 * - **operators**: `.`/2 (`xfy`, 800), `:=`/2 (`xfx`, 850), `as`/2 (`xfx`, 200) and `$`/1
 *   (`fx`, 100), which together enable the fluent Prolog syntax documented on
 *   [it.unibo.tuprolog.solve.libs.oop.rules.ColonEquals] and
 *   [it.unibo.tuprolog.solve.libs.oop.rules.Dot].
 *
 * Reflection-based method/constructor invocation (everything beyond alias/type-name resolution)
 * is only implemented on the JVM target: see the platform notes on
 * [it.unibo.tuprolog.solve.libs.oop.allSupertypes], [it.unibo.tuprolog.solve.libs.oop.fullName]
 * and the other `expect` declarations in `TypeUtils.kt` for what throws
 * [kotlin.NotImplementedError] on Kotlin/JS.
 *
 * @see it.unibo.tuprolog.solve.library.Runtime
 */
object OOPLib : AbstractLibrary() {
    override val alias: String
        get() = "prolog.oop"

    override val help: String =
        """
        `library('prolog.oop')`
        
        Bridges Prolog with the JVM/Kotlin object world via reflection: Prolog code can create objects, call their (possibly overloaded) methods and constructors, read and write their properties, and convert values back and forth. Objects and classes are represented by opaque reference terms (see `object_ref/1`, `type_ref/1`, `null_ref/1`); Prolog atoms, numbers and booleans are converted automatically to and from strings, numbers and booleans.
        
        Main entry points are `new_object/2,3` (instantiation), `invoke_method/3` and `invoke_strict/3` (method calls), `assign/3` (property writes), `cast/3` and `type/2` (types and conversions), `array_items/2`, `list_items/2` and `set_items/2` (collections), and `register/2`/`unregister/1` (aliases, see `alias/2`).
        
        The operators `./2` (method call or property read, chainable), `:=/2` (capture a result or assign a property), `as/2` (explicit conversion) and `${'$'}/1` (alias reference, e.g. `${'$'}stdout`) provide a fluent syntax on top of these predicates. Example: `new_object('java.util.ArrayList', [], L), L.add(1 as 'java.lang.Long'), N := L.size, ${'$'}stdout.println(N)`. Reflective invocation is only available on the JVM.
        """.trimIndent()

    override val primitives: Map<Signature, Primitive>
        get() =
            listOf<PrimitiveWrapper<*>>(
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
                Unregister,
            ).map { it.descriptionPair }.toMap()

    private val ruleWrappers: List<RuleWrapper<ExecutionContext>>
        get() =
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
                *platformSpecificAliases,
            )

    override val clauses: List<Clause>
        get() = ruleWrappers.map { it.implementation }

    override val operators: OperatorSet
        get() =
            OperatorSet(
                Operator(".", XFY, 800),
                Operator(":=", XFX, 850),
                Operator("as", XFX, 200),
                Operator("$", FX, 100),
            )

    override val documentation: Map<Signature, String>
        get() =
            mergeDocumentation(
                super.documentation,
                documentationOf(ruleWrappers.asSequence()),
                operatorDocumentation,
            )

    private val operatorDocumentation: Map<Signature, String>
        get() =
            mapOf(
                Signature("$", 1) to
                    """
                    `${'$'}+Alias`
                    
                    Alias reference, with no predicate behind it: `${'$'}Alias` stands for the object or type reference registered under the atom `Alias` by an `alias/2` fact, i.e. one of the library's default aliases (`string`, `int`, `arraylist`, `stdout`, ...) or one added via `register/2`. It is resolved by the library wherever a reference is expected: as the receiver of `invoke_method/3`, `invoke_strict/3`, `./2` and `:=/2`, as the type of `new_object/2,3` and `cast/3`, as the value of `assign/3`, as the type in `as/2` casts, and inside method or constructor arguments (where it must denote an object). An unregistered alias raises an existence error. Example: `${'$'}stdout.println(hello)` or `new_object(${'$'}arraylist, L)`.
                    """.trimIndent(),
                Signature("as", 2) to
                    """
                    `+Term as +Type`
                    
                    Explicit conversion, with no predicate behind it. Inside method or constructor arguments (of `invoke_method/3`, `invoke_strict/3`, `new_object/3`, `./2` chains, `*_items/2` lists and `assign/3` values), `Term as Type` converts `Term` into exactly `Type` instead of the automatically inferred type, which also steers overload selection. There `Type` is an atom holding a fully qualified type name (e.g. `'java.lang.Long'`), a type reference, or a `${'$'}Alias` reference to a type; if `Term` cannot be represented as `Type`, an error is raised. On the right of `:=/2`, `Result := Term as Type` instead calls `cast/3`, which requires a type reference or a `${'$'}Alias`. Since `as` binds tighter than `.`, parenthesise chains: `R := (Obj.size) as ${'$'}long`. Example: `L.add(1 as 'java.lang.Long')`.
                    """.trimIndent(),
            )
}
