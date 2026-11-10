package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Terms.VAR_NAME_PATTERN
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.setTags

@Suppress("EqualsOrHashCode")
internal class VarImpl(
    override val name: String,
    private val identifier: VarIdentifier = nextVarIdentifier(),
    tags: Map<String, Any> = emptyMap(),
) : TermImpl(tags),
    Var {
    // Caches are plain fields filled on first use, as in AbstractStruct: `by lazy` costs two objects per variable,
    // and, on JS, a property reference at every read.
    private var cachedCompleteName: String? = null

    override val completeName: String
        get() = cachedCompleteName ?: "${name}_$identifier".also { cachedCompleteName = it }

    override val isAnonymous: Boolean = super.isAnonymous

    override val isNameWellFormed: Boolean
        get() = VAR_NAME_PATTERN.matches(name)

    override fun structurallyEquals(other: Term): Boolean = other.isVar

    override fun copyWithTags(tags: Map<String, Any>): Var = VarImpl(name, identifier, tags)

    override fun freshCopy(): Var = VarImpl(name, tags = tags)

    override fun freshCopy(scope: Scope): Var =
        when {
            isAnonymous -> scope.anonymous()
            else -> scope.varOf(name)
        }.setTags(tags)

    override fun toString(): String = if (isNameWellFormed) completeName else Var.escapeName(completeName)

    override val id: String get() = identifier.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherVar = asTerm(other)?.asVar()
        if (otherVar === null) return false
        return equalsByCompleteName(otherVar)
    }

    // the same as comparing complete names (`${name}_$identifier`, whose last `_` precedes the identifier), without
    // building them: VarImpl is the only Var
    @Suppress("NOTHING_TO_INLINE")
    private inline fun equalsByCompleteName(other: Var) =
        if (other is VarImpl) {
            identifier == other.identifier && name == other.name
        } else {
            completeName == other.completeName
        }

    private fun equalsToVar(
        other: Var,
        useVarCompleteName: Boolean,
    ) = if (useVarCompleteName) {
        equalsByCompleteName(other)
    } else {
        name == other.name
    }

    override fun equals(
        other: Term,
        useVarCompleteName: Boolean,
    ): Boolean = other.isVar && equalsToVar(other.castToVar(), useVarCompleteName)

    override val hashCodeCache: Int
        get() = identifier.hashCode()

    override fun isUnifierSkippable(unifier: Substitution.Unifier): Boolean = this !in unifier

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term = unifier[this] ?: this

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitVar(this)
}
