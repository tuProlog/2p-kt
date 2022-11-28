package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.dequeOf

class ClausePartitioner(
    override val unificator: Unificator,
    private val source: Iterable<Clause>,
    private val staticByDefault: Boolean = true
) : ClausePartition, DirectiveSelector {

    private val _staticClauses: MutableTheory = MutableTheory.emptyIndexed(unificator)
    private val _dynamicClauses: MutableTheory = MutableTheory.emptyIndexed(unificator)
    private val _operators: MutableList<Operator> = dequeOf()
    private val _initialGoals: MutableList<Struct> = dequeOf()
    private val _includes: MutableList<Atom> = dequeOf()
    private var _flagStore: FlagStore = FlagStore.EMPTY

    private val dynamicSignatures: MutableSet<Signature> = mutableSetOf()
    private val staticSignatures: MutableSet<Signature> = mutableSetOf()

    override val staticClauses: Theory
        get() {
            performPartition()
            return _staticClauses
        }

    override val dynamicClauses: Theory
        get() {
            performPartition()
            return _dynamicClauses
        }

    override val operators: OperatorSet
        get() {
            performPartition()
            return OperatorSet(_operators)
        }

    override val initialGoals: List<Struct>
        get() {
            performPartition()
            return _initialGoals
        }

    override val includes: List<Atom>
        get() {
            performPartition()
            return _includes
        }

    override val flagStore: FlagStore
        get() {
            performPartition()
            return _flagStore
        }

    private var partitioned = false

    private fun performPartition() {
        if (!partitioned) {
            source.forEach(this::listen)
            partitioned = true
        }
    }

    private val defaultClauses: MutableTheory by lazy { if (staticByDefault) _staticClauses else _dynamicClauses }

    override fun onSetFlag(directive: Directive, name: Term, value: Term) {
        onDirective(directive)
        if (name is Atom) {
            _flagStore += name.value to value
        }
    }

    override fun onOperator(directive: Directive, priority: Term, specifier: Term, name: Term) {
        onDirective(directive)
        if (priority is Integer) {
            try {
                val spec = Specifier.fromTerm(specifier)
                if (name is Atom) {
                    _operators += Operator(name.value, spec, priority.value.toInt())
                }
            } catch (_: IllegalArgumentException) {
                // do nothing
            }
        }
    }

    override fun onLoad(directive: Directive, goal: Term) {
        onDirective(directive)
        if (goal is Atom) {
            _includes += goal
        }
    }

    override fun onSolve(directive: Directive, goal: Term) {
        onDirective(directive)
        if (goal is Struct) {
            _initialGoals += goal
        }
    }

    override fun onStatic(directive: Directive, indicator: Indicator) {
        onDirective(directive)
        if (indicator.isWellFormed) {
            staticSignatures += Signature.fromIndicator(indicator)!!
        }
    }

    override fun onDynamic(directive: Directive, indicator: Indicator) {
        onDirective(directive)
        if (indicator.isWellFormed) {
            dynamicSignatures += Signature.fromIndicator(indicator)!!
        }
    }

    override fun onDirectiveMatchingPattern(directive: Directive, pattern: Term, unifier: Substitution.Unifier) {
        onDirective(directive)
    }

    override fun onDirective(directive: Directive) {
        onClause(directive)
    }

    override fun onRule(rule: Rule) {
        when (rule.head.extractSignature()) {
            in dynamicSignatures -> _dynamicClauses.assertZ(rule)
            in staticSignatures -> _staticClauses.assertZ(rule)
            else -> defaultClauses.assertZ(rule)
        }
    }

    override fun onFact(fact: Fact) {
        onRule(fact)
    }

    override fun onClause(clause: Clause) {
        defaultClauses.assertZ(clause)
    }
}
