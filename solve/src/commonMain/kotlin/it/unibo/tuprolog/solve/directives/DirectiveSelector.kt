package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution.Unifier
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.stdlib.primitive.Op
import it.unibo.tuprolog.solve.stdlib.primitive.SetFlag
import it.unibo.tuprolog.solve.stdlib.rule.SetPrologFlag

/**
 * The concrete [DirectiveListener] recognizing every directive pattern 2P-Kt gives special treatment to when
 * loading a knowledge base: `dynamic/1`, `static/1` (declaring a predicate's storage), `initialization/1`/`solve/1`
 * (a goal to run at load time), `include/1`/`load/1` (another theory to load), `op/3` (operator declaration), and
 * `set_flag/2`/`set_prolog_flag/2` (flag assignment). Each recognized pattern is routed to the corresponding
 * `on*` callback ([onDynamic], [onStatic], [onSolve], [onLoad], [onOperator], [onSetFlag]); anything else falls
 * back to [ClauseListener.onDirective].
 *
 * Implemented by [ClausePartitioner], which is what actually collects the callbacks' arguments into a
 * [ClausePartition].
 */
interface DirectiveSelector : DirectiveListener {
    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        private val scope = Scope.empty()

        val Name = scope.varOf("Name")

        val Arity = scope.varOf("Arity")

        val Goal = scope.varOf("Goal")

        val Priority = scope.varOf("Goal")

        val Specifier = scope.varOf("Specifier")

        val Value = scope.varOf("Value")

        val DYNAMIC = scope.structOf("dynamic", scope.indicatorOf(Name, Arity))

        val STATIC = scope.structOf("static", scope.indicatorOf(Name, Arity))

        val INITIALIZATION = scope.structOf("initialization", Goal)

        val SOLVE = scope.structOf("solve", Goal)

        val INCLUDE = scope.structOf("include", Name)

        val LOAD = scope.structOf("load", Name)

        val OP = scope.structOf(Op.functor, Priority, Specifier, Name)

        val SET_FLAG = scope.structOf(SetFlag.functor, Name, Value)

        val SET_PROLOG_FLAG = scope.structOf(SetPrologFlag.functor, Name, Value)

        private val PATTERNS =
            listOf(DYNAMIC, STATIC, INITIALIZATION, SOLVE, INCLUDE, LOAD, OP, SET_FLAG, SET_PROLOG_FLAG)
    }

    override val patterns: List<Term> get() = PATTERNS

    override fun listenDirectiveMatchingPattern(
        directive: Directive,
        pattern: Term,
        unifier: Unifier,
    ) {
        when (pattern) {
            DYNAMIC -> onDynamic(directive, Indicator.of(unifier[Name]!!, unifier[Arity]!!))
            STATIC -> onStatic(directive, Indicator.of(unifier[Name]!!, unifier[Arity]!!))
            INITIALIZATION, SOLVE -> onSolve(directive, unifier[Goal]!!)
            INCLUDE, LOAD -> onLoad(directive, unifier[Name]!!)
            OP -> onOperator(directive, unifier[Priority]!!, unifier[Specifier]!!, unifier[Name]!!)
            SET_FLAG, SET_PROLOG_FLAG -> onSetFlag(directive, unifier[Name]!!, unifier[Value]!!)
            else -> super.listenDirectiveMatchingPattern(directive, pattern, unifier)
        }
    }

    /** Invoked on a `set_flag(name, value)`/`set_prolog_flag(name, value)` directive. */
    fun onSetFlag(
        directive: Directive,
        name: Term,
        value: Term,
    )

    /** Invoked on an `op(priority, specifier, name)` directive. */
    fun onOperator(
        directive: Directive,
        priority: Term,
        specifier: Term,
        name: Term,
    )

    /** Invoked on an `include(goal)`/`load(goal)` directive. */
    fun onLoad(
        directive: Directive,
        goal: Term,
    )

    /** Invoked on an `initialization(goal)`/`solve(goal)` directive. */
    fun onSolve(
        directive: Directive,
        goal: Term,
    )

    /** Invoked on a `static(indicator)` directive. */
    fun onStatic(
        directive: Directive,
        indicator: Indicator,
    )

    /** Invoked on a `dynamic(indicator)` directive. */
    fun onDynamic(
        directive: Directive,
        indicator: Indicator,
    )
}
