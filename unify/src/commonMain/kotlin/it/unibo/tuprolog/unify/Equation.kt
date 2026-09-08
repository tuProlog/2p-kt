package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermConvertible
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.Castable
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import it.unibo.tuprolog.core.List as LogicList

/**
 * An equation between two logic [Term]s, `lhs = rhs`, as built and progressively simplified while computing a
 * [Substitution] (see [AbstractUnificator]).
 *
 * LHS stands for Left-Hand side and RHS stands for Right-Hand side, of the Equation. Every [Equation] falls into
 * exactly one of five shapes, reflected by this sealed class' subtypes and the corresponding `is`/`as`/`castTo`
 * member triples ([isIdentity]/[asIdentity]/[castToIdentity] and so on):
 * - [Identity] — both sides are already equal (a no-op for unification purposes);
 * - [Assignment] (further split into [LeftAssignment] and [RightAssignment]) — one side is a [Var] that could be
 *   bound to the other side, turning the equation into a [Substitution] entry;
 * - [Comparison] — both sides are non-variable, non-equal terms still to be decomposed further (e.g. two structs
 *   with the same functor/arity, to be compared argument-wise);
 * - [Contradiction] — both sides are irreconcilably different, signaling unification failure.
 *
 * Instances are normally created through the factory functions in the [companion object][Equation.Companion],
 * which classify a pair of [Term]s into the appropriate subtype rather than requiring callers to pick one manually.
 */
sealed class Equation(
    /** The left-hand side of the equation */
    @JsName("lhs") open val lhs: Term,
    /** The right-hand side of the equation */
    @JsName("rhs") open val rhs: Term,
) : TermConvertible,
    Castable<Equation> {
    /** Whether this [Equation] is an [Identity], i.e. an equation between already-equal terms. */
    @JsName("isIdentity")
    open val isIdentity: Boolean
        get() = false

    /** This [Equation] as an [Identity], or `null` if [isIdentity] is `false`. */
    @JsName("asIdentity")
    open fun asIdentity(): Identity? = null

    /** This [Equation] as an [Identity]. @throws ClassCastException if [isIdentity] is `false`. */
    @JsName("castToIdentity")
    fun castToIdentity(): Identity =
        asIdentity() ?: throw ClassCastException("Cannot cast $this to ${Identity::class.simpleName}")

    /** Whether this [Equation] is an [Assignment] (either [LeftAssignment] or [RightAssignment]). */
    @JsName("isAssignment")
    open val isAssignment: Boolean
        get() = false

    /** This [Equation] as an [Assignment], or `null` if [isAssignment] is `false`. */
    @JsName("asAssignment")
    open fun asAssignment(): Assignment? = null

    /** This [Equation] as an [Assignment]. @throws ClassCastException if [isAssignment] is `false`. */
    @JsName("castToAssignment")
    fun castToAssignment(): Assignment =
        asAssignment() ?: throw ClassCastException("Cannot cast $this to ${Assignment::class.simpleName}")

    /** Whether this [Equation] is a [LeftAssignment], i.e. shaped as `Var = Term`. */
    @JsName("isLeftAssignment")
    open val isLeftAssignment: Boolean
        get() = false

    /** This [Equation] as a [LeftAssignment], or `null` if [isLeftAssignment] is `false`. */
    @JsName("asLeftAssignment")
    open fun asLeftAssignment(): LeftAssignment? = null

    /** This [Equation] as a [LeftAssignment]. @throws ClassCastException if [isLeftAssignment] is `false`. */
    @JsName("castToLeftAssignment")
    fun castToLeftAssignment(): LeftAssignment =
        asLeftAssignment() ?: throw ClassCastException("Cannot cast $this to ${LeftAssignment::class.simpleName}")

    /** Whether this [Equation] is a [RightAssignment], i.e. shaped as `Term = Var`. */
    @JsName("isRightAssignment")
    open val isRightAssignment: Boolean
        get() = false

    /** This [Equation] as a [RightAssignment], or `null` if [isRightAssignment] is `false`. */
    @JsName("asRightAssignment")
    open fun asRightAssignment(): RightAssignment? = null

    /** This [Equation] as a [RightAssignment]. @throws ClassCastException if [isRightAssignment] is `false`. */
    @JsName("castToRightAssignment")
    fun castToRightAssignment(): RightAssignment =
        asRightAssignment() ?: throw ClassCastException("Cannot cast $this to ${RightAssignment::class.simpleName}")

    /** Whether this [Equation] is a [Comparison], i.e. still needs decomposing into sub-equations. */
    @JsName("isComparison")
    open val isComparison: Boolean
        get() = false

    /** This [Equation] as a [Comparison], or `null` if [isComparison] is `false`. */
    @JsName("asComparison")
    open fun asComparison(): Comparison? = null

    /** This [Equation] as a [Comparison]. @throws ClassCastException if [isComparison] is `false`. */
    @JsName("castToComparison")
    fun castToComparison(): Comparison =
        asComparison() ?: throw ClassCastException("Cannot cast $this to ${Comparison::class.simpleName}")

    /** Whether this [Equation] is a [Contradiction], i.e. represents unification failure. */
    @JsName("isContradiction")
    open val isContradiction: Boolean
        get() = false

    /** This [Equation] as a [Contradiction], or `null` if [isContradiction] is `false`. */
    @JsName("asContradiction")
    open fun asContradiction(): Contradiction? = null

    /** This [Equation] as a [Contradiction]. @throws ClassCastException if [isContradiction] is `false`. */
    @JsName("castToContradiction")
    fun castToContradiction(): Contradiction =
        asContradiction() ?: throw ClassCastException("Cannot cast $this to ${Contradiction::class.simpleName}")

    /** Creates a copy of this [Equation], of the same concrete subtype, with [lhs] and/or [rhs] replaced. */
    @JsName("clone")
    abstract fun clone(
        lhs: Term = this.lhs,
        rhs: Term = this.rhs,
    ): Equation

    /** Turns this [Equation] into a [Contradiction] with the same [lhs] and [rhs], regardless of its actual shape. */
    @JsName("toContradiction")
    fun toContradiction(): Contradiction = Contradiction(lhs, rhs)

    /**
     * The `(variable, term)` pair this [Equation] assigns, i.e. its [Var] side paired with the other side.
     * @throws IllegalArgumentException if neither [lhs] nor [rhs] is a [Var] (this [Equation] is not an [Assignment]).
     */
    @JsName("toAssignmentPair")
    open fun toAssignmentPair(): Pair<Var, Term> =
        when {
            lhs.isVar -> lhs.castToVar() to rhs
            rhs.isVar -> rhs.castToVar() to lhs
            else -> throw IllegalArgumentException("Equation contains no variables: $this")
        }

    /**
     * The single-binding [Substitution] this [Equation] amounts to, i.e. its [Var] side unified with the other side.
     * @throws IllegalArgumentException if neither [lhs] nor [rhs] is a [Var] (this [Equation] is not an [Assignment]).
     */
    @JsName("toSubstitution")
    open fun toSubstitution(): Substitution =
        when {
            lhs.isVar -> Substitution.unifier(lhs.castToVar(), rhs)
            rhs.isVar -> Substitution.unifier(rhs.castToVar(), lhs)
            else -> throw IllegalArgumentException("Equation contains no variables: $this")
        }

    /** Converts this [Equation] to its logical representation, the binary [Struct] `lhs = rhs`. */
    override fun toTerm(): Struct = Struct.of("=", lhs, rhs)

    /** This [Equation]'s [lhs] and [rhs], as a [Pair]. */
    @JsName("toPair")
    open fun toPair(): Pair<Term, Term> = Pair(lhs, rhs)

    /** Creates a new [Equation] with [lhs] and [rhs] swapped, reclassifying it accordingly (see [of]). */
    @JsName("swap")
    fun swap(): Equation = of(rhs, lhs)

    /**
     * Applies given [substitution] to the Equation left-hand and right-hand sides, returning the new Equation
     *
     * To modify default equality between [Term]s, a custom [equalityChecker] can be provided
     */
    @JvmOverloads
    @JsName("apply")
    fun apply(
        substitution: Substitution,
        equalityChecker: (Term, Term) -> Boolean = Term::equals,
    ): Equation = of(lhs[substitution], rhs[substitution], equalityChecker)

    /** An equation of identical [Term]s */
    data class Identity(
        override val lhs: Term,
        override val rhs: Term,
    ) : Equation(lhs, rhs) {
        override val isIdentity: Boolean
            get() = true

        override fun asIdentity(): Identity = this

        override fun clone(
            lhs: Term,
            rhs: Term,
        ): Identity = copy(lhs = lhs, rhs = rhs)
    }

    /** An equation assigning a [Var] to a [Term], regardless of which side ([lhs] or [rhs]) the [Var] is on. */
    abstract class Assignment(
        override val lhs: Term,
        override val rhs: Term,
    ) : Equation(lhs, rhs) {
        /** The [Var] being assigned, i.e. whichever of [lhs]/[rhs] is a variable. */
        @JsName("variable")
        abstract val variable: Var

        /** The [Term] being assigned to [variable], i.e. the other side of the equation. */
        @JsName("term")
        abstract val term: Term

        override val isAssignment: Boolean
            get() = true

        override fun asAssignment(): Assignment = this

        override fun toAssignmentPair(): Pair<Var, Term> = Pair(variable, term)

        override fun toSubstitution(): Substitution = Substitution.unifier(variable, term)

        abstract override fun clone(
            lhs: Term,
            rhs: Term,
        ): Assignment
    }

    /** An equation stating [Var] = [Term] */
    data class LeftAssignment(
        override val lhs: Var,
        override val rhs: Term,
    ) : Assignment(lhs, rhs) {
        override val variable: Var = lhs
        override val term: Term = rhs

        override val isLeftAssignment: Boolean
            get() = true

        override fun asLeftAssignment(): LeftAssignment = this

        override fun clone(
            lhs: Term,
            rhs: Term,
        ): LeftAssignment = copy(lhs = lhs.castToVar(), rhs = rhs)

        override fun toPair(): Pair<Var, Term> = Pair(lhs, rhs)
    }

    /** An equation stating [Term] = [Var] */
    data class RightAssignment(
        override val lhs: Term,
        override val rhs: Var,
    ) : Assignment(lhs, rhs) {
        override val variable: Var = rhs
        override val term: Term = lhs

        override val isRightAssignment: Boolean
            get() = true

        override fun asRightAssignment(): RightAssignment = this

        override fun clone(
            lhs: Term,
            rhs: Term,
        ): RightAssignment = copy(lhs = lhs, rhs = rhs.castToVar())

        override fun toPair(): Pair<Term, Var> = Pair(lhs, rhs)
    }

    /** An equation comparing [Term]s, possibly different */
    data class Comparison(
        override val lhs: Term,
        override val rhs: Term,
    ) : Equation(lhs, rhs) {
        override val isComparison: Boolean
            get() = true

        override fun asComparison(): Comparison = this

        override fun clone(
            lhs: Term,
            rhs: Term,
        ): Comparison = copy(lhs = lhs, rhs = rhs)
    }

    /** A contradicting equation, trying to equate non equal [Term]s */
    data class Contradiction(
        override val lhs: Term,
        override val rhs: Term,
    ) : Equation(lhs, rhs) {
        override val isContradiction: Boolean
            get() = true

        override fun asContradiction(): Contradiction = this

        override fun clone(
            lhs: Term,
            rhs: Term,
        ): Contradiction = copy(lhs = lhs, rhs = rhs)

        override fun toSubstitution(): Substitution.Fail = Substitution.failed()
    }

    /** Equation companion object */
    companion object {
        /**
         * Classifies [lhs] and [rhs] into the appropriate [Equation] subtype: [Identity] if they are trivially
         * equal (per [equalityChecker], or structurally for variables), [LeftAssignment]/[RightAssignment] if
         * exactly one side is a [Var], [Contradiction] if they can be told apart at this level (different constants,
         * or structs with different functor/arity), or [Comparison] if they need further decomposition (e.g. two
         * structs with the same functor/arity, whose arguments are not inspected by this shallow classification —
         * use [allOf] to recursively decompose down to non-decomposable equations).
         *
         * @param equalityChecker decides whether two non-variable [Term]s are equal; defaults to [Term.equals] but
         * can be swapped (e.g. by [AbstractUnificator.checkTermsEquality]) to alter what counts as identical
         */
        @JvmStatic
        @JvmOverloads
        @JsName("of")
        fun of(
            lhs: Term,
            rhs: Term,
            equalityChecker: (Term, Term) -> Boolean = Term::equals,
        ): Equation =
            when {
                lhs.isVar && rhs.isVar -> {
                    if (equalityChecker(lhs, rhs)) {
                        Identity(lhs, rhs)
                    } else {
                        LeftAssignment(lhs.castToVar(), rhs)
                    }
                }
                lhs.isVar -> LeftAssignment(lhs.castToVar(), rhs)
                rhs.isVar -> RightAssignment(lhs, rhs.castToVar())
                lhs.isConstant && rhs.isConstant -> {
                    if (equalityChecker(lhs, rhs)) {
                        Identity(lhs, rhs)
                    } else {
                        Contradiction(lhs, rhs)
                    }
                }
                lhs.isConstant || rhs.isConstant -> Contradiction(lhs, rhs)
                lhs.isStruct && rhs.isStruct -> {
                    val lhsStruct = lhs.castToStruct()
                    val rhsStruct = rhs.castToStruct()
                    if (lhsStruct.arity != rhsStruct.arity || lhsStruct.functor != rhsStruct.functor) {
                        Contradiction(lhsStruct, rhsStruct)
                    } else {
                        Comparison(lhsStruct, rhsStruct)
                    }
                }
                else -> Comparison(lhs, rhs)
            }

        /** Same as [of], but taking the two [Term]s as a [Pair] (`pair.first` = lhs, `pair.second` = rhs). */
        @JvmStatic
        @JvmOverloads
        @JsName("ofPair")
        fun of(
            pair: Pair<Term, Term>,
            equalityChecker: (Term, Term) -> Boolean = Term::equals,
        ): Equation = of(pair.first, pair.second, equalityChecker)

        /** Applies [allOf] to every [Pair] in [pairs], concatenating the resulting fully-decomposed [Equation]s. */
        @JvmStatic
        @JvmOverloads
        @JsName("fromSequence")
        fun from(
            pairs: Sequence<Pair<Term, Term>>,
            equalityChecker: (Term, Term) -> Boolean = Term::equals,
        ): Sequence<Equation> = pairs.flatMap { allOf(it, equalityChecker) }

        /** Same as [from], for an [Iterable] of [Pair]s. */
        @JvmStatic
        @JvmOverloads
        @JsName("fromIterable")
        fun from(
            pairs: Iterable<Pair<Term, Term>>,
            equalityChecker: (Term, Term) -> Boolean = Term::equals,
        ): Sequence<Equation> = from(pairs.asSequence(), equalityChecker)

        /** Same as [from], for a `vararg` of [Pair]s. */
        @JvmStatic
        @JvmOverloads
        @JsName("from")
        fun from(
            vararg pairs: Pair<Term, Term>,
            equalityChecker: (Term, Term) -> Boolean = Term::equals,
        ): Sequence<Equation> = from(sequenceOf(*pairs), equalityChecker)

        /** Same as [allOf], but taking the two [Term]s as a [Pair] (`pair.first` = lhs, `pair.second` = rhs). */
        @JvmStatic
        @JvmOverloads
        @JsName("allOfPair")
        fun allOf(
            pair: Pair<Term, Term>,
            equalityChecker: (Term, Term) -> Boolean = Term::equals,
        ): Sequence<Equation> = allOf(pair.first, pair.second, equalityChecker)

        private fun allOfLists(
            lhs: LogicList,
            rhs: LogicList,
            equalityChecker: (Term, Term) -> Boolean = Term::equals,
        ): Sequence<Equation> =
            lhs.unfold().zip(rhs.unfold()).flatMap { (l, r) ->
                when {
                    l.isCons && r.isCons -> sequenceOf(of(l.castToCons().head, r.castToCons().head, equalityChecker))
                    l.isList && r.isList -> sequenceOf(of(l.castToList(), r.castToList(), equalityChecker))
                    else -> allOf(l, r, equalityChecker)
                }
            }

        private fun allOfTuples(
            lhs: Tuple,
            rhs: Tuple,
            equalityChecker: (Term, Term) -> Boolean = Term::equals,
        ): Sequence<Equation> =
            lhs.unfold().zip(rhs.unfold()).flatMap { (l, r) ->
                when {
                    l.isTuple && r.isTuple ->
                        sequenceOf(
                            of(l.castToTuple().left, r.castToTuple().left, equalityChecker),
                        )
                    else -> allOf(l, r, equalityChecker)
                }
            }

        /**
         * Recursively decomposes [lhs] and [rhs] into a (possibly empty) sequence of [Equation]s, none of which is
         * a [Comparison] between structurally-matching compound terms: lists, tuples and structs sharing the same
         * functor/arity are unfolded and paired element-wise (recursing into each pair), rather than being
         * classified as a single [Comparison] the way [of] would. This is what [AbstractUnificator] uses to expand
         * a pair of [Term]s into the equations it then simplifies to compute an MGU.
         */
        @JvmStatic
        @JvmOverloads
        @JsName("allOf")
        fun allOf(
            lhs: Term,
            rhs: Term,
            equalityChecker: (Term, Term) -> Boolean = Term::equals,
        ): Sequence<Equation> =
            when {
                lhs.isAtom && rhs.isAtom -> {
                    sequenceOf(of(lhs, rhs, equalityChecker))
                }
                lhs.isList && rhs.isList -> {
                    allOfLists(lhs.castToList(), rhs.castToList(), equalityChecker)
                }
                lhs.isTuple && rhs.isTuple -> {
                    allOfTuples(lhs.castToTuple(), rhs.castToTuple(), equalityChecker)
                }
                lhs.isStruct && rhs.isStruct -> {
                    val lhsStruct = lhs.castToStruct()
                    val rhsStruct = rhs.castToStruct()
                    if (lhsStruct.arity == rhsStruct.arity && lhsStruct.functor == rhsStruct.functor) {
                        lhsStruct.argsSequence.zip(rhsStruct.argsSequence).flatMap { allOf(it, equalityChecker) }
                    } else {
                        sequenceOf(of(lhs, rhs, equalityChecker))
                    }
                }
                else -> {
                    sequenceOf(of(lhs, rhs, equalityChecker))
                }
            }
    }
}
