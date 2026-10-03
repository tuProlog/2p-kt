package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.Result

/**
 * `invoke_method(+Ref, +Method(Args...), ?Result)`: invokes `Method` (with `Args`, converted via
 * `termToObjectConverter`) on `Ref` -- an
 * [it.unibo.tuprolog.solve.libs.oop.ObjectRef], [it.unibo.tuprolog.solve.libs.oop.TypeRef], or
 * `$Alias` expression -- unifying `Result` with the invocation's return value converted to a plain
 * [Term] (e.g. a JVM `String` becomes a Prolog atom). Use [InvokeStrict] instead to keep the
 * result as an [it.unibo.tuprolog.solve.libs.oop.ObjectRef] regardless of its type.
 *
 * Fails (rather than throwing) if the invoked member returns no value
 * ([it.unibo.tuprolog.solve.libs.oop.Result.None], e.g. a `void`-returning method).
 *
 * Example: `new_object('java.lang.String', [hello], S), invoke_method(S, length, N)` binds `N`
 * to the plain Prolog integer `5`; [InvokeStrict] would instead bind it to an [it.unibo.tuprolog.solve.libs.oop.ObjectRef]
 * wrapping the boxed `Integer` value `5`.
 */
object InvokeMethod : AbstractInvoke("method") {
    override val help: String =
        """
        `invoke_method(+Ref, +Method, ?Result)`
        
        Invokes a public member of the object referenced by `Ref` and unifies `Result` with the returned value, converted to a plain term when possible: strings and characters become atoms, numbers become numbers, booleans become `true`/`false`, `null` becomes the null reference, and any other object stays an object reference. `Ref` may be an object reference, a type reference (to call static or companion-object members) or a `${'$'}Alias` reference; `Method` is an atom (no arguments; also reads a property) or a compound `name(Arg1, ..., ArgN)`. Arguments are converted to objects and the best-matching overload is selected; use `as` casts to steer the choice. Succeeds once. Raises a type error if `Ref` is neither a reference nor a `${'$'}Alias` term, an existence error if the alias is not registered or no suitable method exists, a representation error if an argument cannot be converted, and a system error if the method itself throws. This is what `Obj.method(...)` (see `./2`) lowers to. Example: `invoke_method(${'$'}math, max(1, 2), M)`.
        """.trimIndent()

    override fun Result.Value.getInvocationResult(): Term = toTerm()
}
