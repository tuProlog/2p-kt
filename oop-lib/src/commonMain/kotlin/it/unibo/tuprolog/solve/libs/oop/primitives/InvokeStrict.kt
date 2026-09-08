package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.Result

/**
 * `invoke_strict(+Ref, +Method(Args...), ?Result)`: like [InvokeMethod], but always unifying
 * `Result` with an [it.unibo.tuprolog.solve.libs.oop.ObjectRef] wrapping the invocation's return
 * value, instead of converting it to a plain [Term] when possible. Useful when the caller needs
 * to keep invoking further members on the result reflectively, rather than getting e.g. a bare
 * Prolog integer back.
 *
 * Fails (rather than throwing) if the invoked member returns no value
 * ([it.unibo.tuprolog.solve.libs.oop.Result.None], e.g. a `void`-returning method).
 */
object InvokeStrict : AbstractInvoke("strict") {
    override fun Result.Value.getInvocationResult(): Term = asObjectRef()
}
