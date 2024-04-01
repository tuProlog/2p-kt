package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.DefaultSubstitutionFactory
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface SubstitutionFactory {
    @JsName("failedSubstitution")
    fun failedSubstitution(): Substitution.Fail

    @JsName("emptyUnifier")
    fun emptyUnifier(): Substitution.Unifier

    @JsName("emptySubstitution")
    fun emptySubstitution(): Substitution.Unifier

    @JsName("substitutionOf")
    fun substitutionOf(
        variable: Var,
        term: Term,
    ): Substitution.Unifier

    @JsName("unifierOf")
    fun unifierOf(
        variable: Var,
        term: Term,
    ): Substitution.Unifier

    @JsName("substitutionOfMap")
    fun substitutionOf(assignments: Map<Var, Term>): Substitution.Unifier

    @JsName("unifierOfMap")
    fun unifierOf(assignments: Map<Var, Term>): Substitution.Unifier

    @JsName("substitutionOfPairs")
    fun substitutionOf(
        assignment: Pair<Var, Term>,
        vararg otherAssignments: Pair<Var, Term>,
    ): Substitution

    @JsName("unifierOfPairs")
    fun unifierOf(
        assignment: Pair<Var, Term>,
        vararg otherAssignments: Pair<Var, Term>,
    ): Substitution.Unifier

    @JsName("substitutionOfIterable")
    fun substitutionOf(assignments: Iterable<Pair<Var, Term>>): Substitution

    @JsName("unifierOfIterable")
    fun unifierOf(assignment: Iterable<Pair<Var, Term>>): Substitution.Unifier

    @JsName("substitutionOfSequence")
    fun substitutionOf(assignments: Sequence<Pair<Var, Term>>): Substitution

    @JsName("unifierOfSequence")
    fun unifierOf(assignments: Sequence<Pair<Var, Term>>): Substitution.Unifier

    companion object {
        @JsName("default")
        @JvmStatic
        val default: SubstitutionFactory = DefaultSubstitutionFactory
    }
}
