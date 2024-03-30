package it.unibo.tuprolog.core

import kotlin.js.JsName

interface SubstitutionFactory {
    @JsName("failedSubstitution")
    fun failedSubstitution(): Substitution.Fail

    @JsName("emptyUnifier")
    fun emptyUnifier(): Substitution.Unifier

    @JsName("emptySubstitution")
    fun emptySubstitution(): Substitution.Unifier

    @JsName("substitutionOf")
    fun substitutionOf(variable: Var, term: Term): Substitution.Unifier

    @JsName("unifierOf")
    fun unifierOf(variable: Var, term: Term): Substitution.Unifier

    @JsName("substitutionOfMap")
    fun substitutionOf(assignments: Map<Var, Term>): Substitution.Unifier

    @JsName("unifierOfMap")
    fun unifierOf(assignments: Map<Var, Term>): Substitution.Unifier

    @JsName("substitutionOfPairs")
    fun substitutionOf(assignment: Pair<Var, Term>, vararg otherAssignments: Pair<Var, Term>): Substitution

    @JsName("unifierOfPairs")
    fun unifierOf(assignment: Pair<Var, Term>, vararg otherAssignments: Pair<Var, Term>): Substitution.Unifier

    @JsName("substitutionOfIterable")
    fun substitutionOf(assignment: Iterable<Pair<Var, Term>>): Substitution

    @JsName("unifierOfIterable")
    fun unifierOf(assignment: Iterable<Pair<Var, Term>>): Substitution.Unifier

    @JsName("substitutionOfSequence")
    fun substitutionOf(assignment: Sequence<Pair<Var, Term>>): Substitution

    @JsName("unifierOfSequence")
    fun unifierOf(assignment: Sequence<Pair<Var, Term>>): Substitution.Unifier
}
