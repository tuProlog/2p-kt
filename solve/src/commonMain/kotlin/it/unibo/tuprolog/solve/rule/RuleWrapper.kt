package it.unibo.tuprolog.solve.rule

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.AbstractWrapper
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import kotlin.reflect.KProperty
import kotlin.collections.List as KtList


// TODO: 16/01/2020 document and test this class
abstract class RuleWrapper<C : ExecutionContext>(signature: Signature) : AbstractWrapper<Rule>(signature) {

    constructor(functor: String, arity: Int, vararg: Boolean = false) : this(
        Signature(
            functor,
            arity,
            vararg
        )
    )

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

    @Suppress("ClassName")
    object variableNames {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return property.name
        }
    }

}