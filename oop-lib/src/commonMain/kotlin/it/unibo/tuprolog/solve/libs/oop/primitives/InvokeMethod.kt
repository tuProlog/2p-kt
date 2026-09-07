package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.Result

/**
 * `invoke_method(+Ref, +Method(Args...), ?Result)`: invokes `Method` (with `Args`, converted via
 * [it.unibo.tuprolog.solve.libs.oop.termToObjectConverter]) on `Ref` -- an
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
    override fun Result.Value.getInvocationResult(): Term = toTerm()
}
