package it.unibo.tuprolog.solve.library

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.LogicFunction
import it.unibo.tuprolog.solve.primitive.Primitive
import kotlin.js.JsName

interface Pluggable {
    /** Operators to be loaded by a solver when the library is used */
    @JsName("operators")
    val operators: OperatorSet

    /** Rules, facts, or directories to be loaded by a solver when the library is used */
    @JsName("clauses")
    val clauses: List<Clause>

    /** [Primitive]s to be loaded by a solver when the library is used,
     * indexed by their [Signature] in the eyes of the solver */
    @JsName("primitives")
    val primitives: Map<Signature, Primitive>

    /** [LogicFunction]s to be loaded by a solver when the library is used,
     * indexed by their [Signature] in the eyes of the solver */
    @JsName("functions")
    val functions: Map<Signature, LogicFunction>

    /**
     * Checks whether this library contains the provided signature.
     *
     * The default implementation, checks for signature presence among primitives and theory clauses by indicator-like search
     */
    @JsName("containsSignature")
    operator fun contains(signature: Signature): Boolean =
        hasPrimitive(signature) || hasRule(signature) || hasFunction(signature)

    /** Checks whether this library contains the definition of provided operator */
    @JsName("containsOperator")
    operator fun contains(operator: Operator): Boolean = operator in operators

    /** Checks whether this library has a [Primitive] with provided signature */
    @JsName("hasPrimitive")
    fun hasPrimitive(signature: Signature): Boolean = primitives.containsKey(signature)

    /** Checks whether this library has a [LogicFunction] with provided signature */
    @JsName("hasFunction")
    fun hasFunction(signature: Signature): Boolean = functions.containsKey(signature)

    /** Checks whether this library has a [Rule] with provided signature */
    @JsName("hasRule")
    fun hasRule(signature: Signature): Boolean

    @JsName("ruleSignatures")
    val rulesSignatures: Sequence<Signature>
        get() = clauses.asSequence()
            .filterIsInstance<Rule>()
            .map { it.head.indicator }
            .map { Signature.fromIndicator(it) }
            .filterNotNull()
            .distinct()

    /** Checks whether the provided signature, is protected in this library */
    @JsName("hasProtected")
    fun hasProtected(signature: Signature): Boolean = signature in this
}
