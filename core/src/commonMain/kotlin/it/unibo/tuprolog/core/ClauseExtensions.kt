@file:JvmName("ClauseExtensions")

package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmName

/**
 * Prepares the receiver Clause for execution, using the provided visitor
 *
 * For example, the [Clause] `product(A) :- A, A` is transformed, after preparation for execution,
 * as the Term: `product(A) :- call(A), call(A)`
 */
@JsName("prepareForExecution")
fun Clause.prepareForExecution(): Clause = accept(Clause.defaultPreparationForExecutionVisitor).castToClause()

/**
 * Prepares the receiver [Clause] for execution like [prepareForExecution], additionally applying [unifier] to
 * each variable encountered along the way.
 */
@JsName("prepareForExecutionWithUnifier")
fun Clause.prepareForExecution(unifier: Substitution.Unifier): Clause =
    accept(Clause.preparationForExecutionVisitor(unifier)).castToClause()

/**
 * This [Clause], with each variable occurring in a goal position of its body replaced by `call/1` of that variable,
 * as ISO Prolog does when a clause is added to the database: e.g. `p(X) :- X, q(X)` becomes
 * `p(X) :- call(X), q(X)`. Goal positions are the body itself and, recursively, the arguments of the control
 * constructs `,`/2, `;`/2 and `->`/2 (see [Clause.notableFunctors]); data arguments, and the arguments of other
 * goals (e.g. `call/1`, `\+/1`, `findall/3`), are left untouched. Non-callable goals (e.g. numbers) are left
 * untouched too, and keep making the clause not [well-formed][Clause.isWellFormed].
 *
 * Returns this very clause if nothing needs to change. Variables and tags are preserved.
 */
@JsName("normalized")
fun Clause.normalized(): Clause {
    val normalizedBody = body.normalizedGoal()
    return if (normalizedBody === body) this else setBody(normalizedBody).replaceTags(tags).castToClause()
}

private val Term.isControlConstruct: Boolean
    get() = this is Struct && arity == 2 && functor in Clause.notableFunctors

/** This goal, normalized as described by [normalized]: returns this very term if nothing needs to change. */
internal fun Term.normalizedGoal(): Term {
    if (isVar) return Struct.of("call", this)
    if (!isControlConstruct) return this
    // control constructs are walked along their right spine with a loop (conjunctions can be very long), and
    // recursively on their left arguments, which are rarely deep
    val spine = mutableListOf<Struct>()
    var current: Term = this
    while (current.isControlConstruct) {
        val construct = current.castToStruct()
        spine.add(construct)
        current = construct[1]
    }
    var result = if (current.isVar) Struct.of("call", current) else current
    for (construct in spine.asReversed()) {
        val left = construct[0].normalizedGoal()
        result =
            if (left === construct[0] && result === construct[1]) {
                construct
            } else {
                Struct.of(construct.functor, left, result).replaceTags(construct.tags)
            }
    }
    return result
}
