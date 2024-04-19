package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.SubstitutionFactory
import it.unibo.tuprolog.core.SubstitutionImpl
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.exception.SubstitutionException

object DefaultSubstitutionFactory : SubstitutionFactory {
    private val failedSubstitution: Substitution.Fail = SubstitutionImpl.FailImpl()

    private val emptySubstitution: Substitution.Unifier = substitutionOf(emptyMap())

    override fun failedSubstitution(): Substitution.Fail = failedSubstitution

    override fun emptyUnifier(): Substitution.Unifier = emptySubstitution

    override fun emptySubstitution(): Substitution.Unifier = emptySubstitution

    private inline fun <T> castToUnifierOrThrowException(
        arg: T,
        ctor: (T) -> Substitution,
    ): Substitution.Unifier = ctor(arg).let { it.asUnifier() ?: throw SubstitutionException(it) }

    override fun substitutionOf(
        variable: Var,
        term: Term,
    ): Substitution.Unifier = unifierOf(variable, term)

    override fun substitutionOf(assignments: Map<Var, Term>): Substitution.Unifier = unifierOf(assignments)

    override fun substitutionOf(
        assignment: Pair<Var, Term>,
        vararg otherAssignments: Pair<Var, Term>,
    ): Substitution = substitutionOf(sequenceOf(assignment, *otherAssignments))

    override fun substitutionOf(assignments: Iterable<Pair<Var, Term>>): Substitution =
        substitutionOf(assignments.asSequence())

    override fun substitutionOf(assignments: Sequence<Pair<Var, Term>>): Substitution = SubstitutionImpl.of(assignments)

    override fun unifierOf(
        variable: Var,
        term: Term,
    ): Substitution.Unifier = unifierOf(mapOf(variable to term))

    override fun unifierOf(assignments: Map<Var, Term>): Substitution.Unifier =
        SubstitutionImpl.UnifierImpl.of(assignments)

    override fun unifierOf(
        assignment: Pair<Var, Term>,
        vararg otherAssignments: Pair<Var, Term>,
    ): Substitution.Unifier =
        castToUnifierOrThrowException(sequenceOf(assignment, *otherAssignments), this::substitutionOf)

    override fun unifierOf(assignment: Iterable<Pair<Var, Term>>): Substitution.Unifier =
        castToUnifierOrThrowException(assignment, this::substitutionOf)

    override fun unifierOf(assignments: Sequence<Pair<Var, Term>>): Substitution.Unifier =
        castToUnifierOrThrowException(assignments, this::substitutionOf)
}
