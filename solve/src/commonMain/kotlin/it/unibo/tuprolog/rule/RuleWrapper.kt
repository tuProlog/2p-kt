package it.unibo.tuprolog.rule

import it.unibo.tuprolog.AbstractWrapper
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.toIndicator
import it.unibo.tuprolog.solve.ExecutionContext
import kotlin.collections.List as KtList

abstract class RuleWrapper<C : ExecutionContext>(signature: Signature) : AbstractWrapper<Rule>(signature) {

    constructor(functor: String, arity: Int, vararg: Boolean = false) : this(Signature(functor, arity, vararg))

    override val wrappedImplementation: Rule by lazy {
        val scope = Scope.empty()
        val headArgs = scope.head
        require(headArgs.size == signature.arity)
        val body = scope.body
        Rule.of(Struct.of(functor, headArgs), body)
    }

    open val Scope.head: KtList<Term> get() = kotlin.collections.emptyList()

    open val Scope.body: Term get() = truthOf(true)

    override fun toString(): String {
        return "RuleWrapper(signature=${signature.toIndicator()}, rule=$wrappedImplementation)"
    }


}