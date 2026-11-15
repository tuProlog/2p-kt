package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.GoalImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A headless [Clause], i.e. `:- body`, such as `:- initialization(main).`.
 * Within a theory, goals play the role of *directives*, i.e. instructions executed by the Prolog engine at load time
 * (see `Theory.directives`), rather than facts/rules meant to be queried.
 * A [Goal] is distinct from the individual goals of its body, i.e. its [bodyItems].
 */
interface Goal : Clause {
    override val head: Nothing?
        get() = null

    override val isRule: Boolean
        get() = false

    override val isFact: Boolean
        get() = false

    override val isGoal: Boolean
        get() = true

    override fun freshCopy(): Goal

    override fun freshCopy(scope: Scope): Goal

    override fun asGoal(): Goal = this

    companion object {
        /**
         * Creates a [Goal] with the given [bodies] goals, folded into a single [Term] via [Tuple] when
         * there is more than one.
         * @throws IllegalArgumentException if [bodies] is empty
         */
        @JvmStatic
        @JsName("ofSequence")
        fun of(bodies: Sequence<Term>): Goal = of(bodies.asIterable())

        /** @see of */
        @JvmStatic
        @JsName("ofIterable")
        fun of(bodies: Iterable<Term>): Goal {
            require(bodies.any()) { "Goal requires at least one body element" }
            return GoalImpl(Tuple.wrapIfNeeded(bodies))
        }

        /** @see of */
        @JvmStatic
        @JsName("of")
        fun of(
            body1: Term,
            vararg body: Term,
        ): Goal = of(listOf(body1, *body))

        /**
         * Creates a [Goal] template with [length] anonymous-variable goals.
         * @throws IllegalArgumentException if [length] is not positive
         */
        @JvmStatic
        @JsName("template")
        fun template(length: Int = 1): Goal {
            require(length > 0)
            return of((0 until length).map { Var.anonymous() })
        }
    }
}
