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
    override val help: String =
        """
        `invoke_strict(+Ref, +Method, ?Result)`
        
        Like `invoke_method/3`, but `Result` is always unified with an object reference wrapping the returned value, even when it could be converted to a plain term (e.g. a string or number stays a reference). Useful to keep calling methods on the result with its exact runtime type preserved, e.g. a `String` you want to keep manipulating as a JVM object. Accepts the same `Ref` and `Method` shapes, and raises the same errors, as `invoke_method/3`. Example: `new_object('java.lang.StringBuilder', [abc], B), invoke_strict(B, toString, S)` binds `S` to a reference to a `String`, whereas `invoke_method/3` would bind it to the atom `abc`.
        """.trimIndent()

    override fun Result.Value.getInvocationResult(): Term = asObjectRef()
}
