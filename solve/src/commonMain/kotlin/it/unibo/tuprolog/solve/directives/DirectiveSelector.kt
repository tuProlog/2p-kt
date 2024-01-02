package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution.Unifier
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.stdlib.primitive.Op
import it.unibo.tuprolog.solve.stdlib.primitive.SetFlag
import it.unibo.tuprolog.solve.stdlib.rule.SetPrologFlag

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

        private val PATTERNS = listOf(DYNAMIC, STATIC, INITIALIZATION, SOLVE, INCLUDE, LOAD, OP, SET_FLAG, SET_PROLOG_FLAG)
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

    fun onSetFlag(
        directive: Directive,
        name: Term,
        value: Term,
    )

    fun onOperator(
        directive: Directive,
        priority: Term,
        specifier: Term,
        name: Term,
    )

    fun onLoad(
        directive: Directive,
        goal: Term,
    )

    fun onSolve(
        directive: Directive,
        goal: Term,
    )

    fun onStatic(
        directive: Directive,
        indicator: Indicator,
    )

    fun onDynamic(
        directive: Directive,
        indicator: Indicator,
    )
}
