package it.unibo.tuprolog.solve.rule

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.VariablesProvider
import it.unibo.tuprolog.solve.AbstractWrapper
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import kotlin.collections.List as KtList

/**
 * A wrapper class for a Prolog-defined [Rule], i.e. the clause-based counterpart of
 * [it.unibo.tuprolog.solve.primitive.PrimitiveWrapper]/[it.unibo.tuprolog.solve.function.FunctionWrapper]. Used by
 * [it.unibo.tuprolog.solve.libraryOf] to bundle a library-provided rule (implemented in Prolog itself, rather than
 * in Kotlin) alongside primitives and functions.
 *
 * Subclasses build [implementation] declaratively, within [scope], by overriding [head] (the rule head's arguments,
 * whose count must match [signature]'s arity) and [body] (defaulting to `true`, i.e. a fact).
 *
 * @param C unused type parameter, kept for symmetry with [it.unibo.tuprolog.solve.primitive.PrimitiveWrapper]/
 * [it.unibo.tuprolog.solve.function.FunctionWrapper].
 * @throws IllegalArgumentException if [head] returns a number of arguments different from [signature]'s arity.
 */
abstract class RuleWrapper<C : ExecutionContext>(
    signature: Signature,
) : AbstractWrapper<Rule>(signature) {
    constructor(functor: String, arity: Int, vararg: Boolean = false) :
        this(Signature(functor, arity, vararg))

    private val scope: Scope = Scope.empty()

    /** A [VariablesProvider] scoped to this wrapper's rule, for use within [head]/[body] overrides. */
    protected val variables = VariablesProvider.of(scope)

    final override val implementation: Rule by lazy {
        val headArgs = scope.head
        require(headArgs.size == signature.arity)
        val body = scope.body
        Rule.of(Struct.of(functor, headArgs), body)
    }

    /** The rule head's arguments, in [scope]; must have as many elements as [signature]'s arity. */
    open val Scope.head: KtList<Term> get() = emptyList()

    /** The rule body, in [scope]; defaults to `true`, i.e. this wrapper declares a fact. */
    open val Scope.body: Term get() = truthOf(true)

    final override fun toString(): String = "RuleWrapper(signature=${signature.toIndicator()}, rule=$implementation)"
}
