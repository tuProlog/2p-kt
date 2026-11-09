package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.impl.Bindings
import it.unibo.tuprolog.unify.impl.SubstitutionMaterializer
import it.unibo.tuprolog.unify.impl.UnificationHooks
import it.unibo.tuprolog.unify.impl.UnificationRun
import it.unibo.tuprolog.unify.impl.hasCycles

/**
 * Skeletal [Unificator] implementation, computing most general unifiers with a worklist of pairs of terms still to be
 * unified, and a table of variable bindings. It is the engine behind [Unificator.default], [Unificator.strict] and
 * [Unificator.naive], and the class to extend for custom unification strategies.
 *
 * Bindings are recorded once and dereferenced when needed, rather than propagated through every pending pair;
 * compound terms are decomposed one level at a time, when their pair is processed, so a mismatch is found before the
 * rest of the terms is visited; the public [Substitution] is built once, at the end. Deep terms and long lists are
 * processed iteratively. All the state of a call is local to it, so an instance can be shared across threads.
 *
 * Subclasses can customise:
 * - [checkTermsEquality], how two constants (or two variables) are compared — what distinguishes [Unificator.strict]
 *   from [Unificator.naive];
 * - [handleComparison], invoked on every pair of terms about to be compared, e.g. to take terms' tags into account:
 *   the pair can be observed, replaced (e.g. by tagged copies), accepted as equal, or rejected, and its orientation is
 *   preserved, so that unification can be made asymmetric;
 * - [handleResult], invoked on the outcome of each [mgu] and [merge] call.
 *
 * A subclass can also implement [IncrementalUnificator] by delegating to [unifyIncrementally], so that solvers unify in
 * their own bindings, without building a [Substitution] per unification: do so only if it does not override
 * [handleResult], which that path cannot call.
 *
 * With occurs-check disabled, cyclic bindings such as `X -> f(X)` are allowed, and unifying cyclic terms terminates.
 *
 * ```kotlin
 * val caseInsensitive =
 *     object : AbstractWorklistUnificator() {
 *         override fun checkTermsEquality(first: Term, second: Term): Boolean = when {
 *             first.isAtom && second.isAtom ->
 *                 first.castToAtom().value.equals(second.castToAtom().value, ignoreCase = true)
 *             else -> first == second
 *         }
 *     }
 * caseInsensitive.match(Atom.of("Foo"), Atom.of("foo")) // true
 * ```
 *
 * @param context the starting bindings assumed by this [Unificator]; see [Unificator.context]
 */
abstract class AbstractWorklistUnificator(
    override val context: Substitution,
) : Unificator {
    /** Creates an [AbstractWorklistUnificator] with an empty starting [context]. */
    constructor() : this(Substitution.empty())

    private val hooks =
        object : UnificationHooks {
            override fun checkTermsEquality(
                first: Term,
                second: Term,
            ): Boolean = this@AbstractWorklistUnificator.checkTermsEquality(first, second)

            override fun handleComparison(step: UnificationStep) =
                this@AbstractWorklistUnificator.handleComparison(step)
        }

    private val contextHasCycles: Boolean by lazy { context.hasCycles() }

    /** The [context], made idempotent once and for all, as the bindings every run starts from must be. */
    private val baseContext: Substitution by lazy {
        if (context.isFailed || context.size < 2) {
            context
        } else {
            val bindings = Bindings(Substitution.empty())
            for ((variable, term) in context) bindings[variable] = term
            SubstitutionMaterializer(bindings).materialize()
        }
    }

    /**
     * Decides whether [first] and [second] are equal, when both are constants or both are (distinct instances of)
     * variables. Compound terms are never passed: they are compared argument by argument. Defaults to [Term.equals].
     */
    protected open fun checkTermsEquality(
        first: Term,
        second: Term,
    ): Boolean = first == second

    /**
     * Invoked on every pair of terms about to be compared, including identical ones and compound ones (before their
     * decomposition), but not on the bindings of [context]. The default implementation does nothing, so the pair is
     * unified as usual. See [UnificationStep] for what can be observed and done.
     */
    protected open fun handleComparison(step: UnificationStep) = Unit

    /**
     * Post-processes the outcome of [request] (possibly [Substitution.failed]) right before [mgu] or [merge] return it.
     * The default implementation returns [result] unchanged.
     */
    protected open fun handleResult(
        request: UnificationRequest,
        result: Substitution,
    ): Substitution = result

    private fun newRun(
        base: Substitution,
        request: UnificationRequest,
    ): UnificationRun? {
        val occursCheck = request.occurCheckEnabled
        if (base.isFailed || occursCheck && (if (base === context) contextHasCycles else base.hasCycles())) return null
        return UnificationRun(hooks, Bindings(if (base === context) baseContext else base), occursCheck, request)
    }

    /**
     * Unifies [term1] and [term2] in [store], as [IncrementalUnificator.unifyInto] does, honouring [checkTermsEquality]
     * and [handleComparison] but not [handleResult]. Meant for subclasses opting into [IncrementalUnificator]:
     *
     * ```kotlin
     * class MyUnificator : AbstractWorklistUnificator(), IncrementalUnificator {
     *     override fun handleComparison(step: UnificationStep) { /* ... */ }
     *
     *     override fun unifyInto(store: BindingStore, term1: Term, term2: Term, occurCheckEnabled: Boolean) =
     *         unifyIncrementally(store, term1, term2, occurCheckEnabled)
     * }
     * ```
     */
    protected fun unifyIncrementally(
        store: BindingStore,
        term1: Term,
        term2: Term,
        occurCheckEnabled: Boolean,
    ): Boolean {
        if (context.isFailed || occurCheckEnabled && contextHasCycles) return false
        val run =
            UnificationRun(
                hooks,
                Bindings(baseContext, store),
                occurCheckEnabled,
                MguRequest(term1, term2, occurCheckEnabled),
            )
        run.push(term1, term2)
        return run.unifyPending()
    }

    override fun mgu(
        term1: Term,
        term2: Term,
        occurCheckEnabled: Boolean,
    ): Substitution {
        val request = MguRequest(term1, term2, occurCheckEnabled)
        val run = newRun(context, request) ?: return handleResult(request, Substitution.failed())
        run.push(term1, term2)
        return handleResult(request, run.solve())
    }

    /**
     * Unifies the bindings of [substitution2] with the ones of [context] and [substitution1]: a variable bound by both
     * gets its two values unified, rather than one overriding the other. Only those pairs of values are passed to
     * [handleComparison]. The result is idempotent, provided that [substitution1] is, as any substitution returned by
     * a [Unificator]: its own bindings are not resolved against each other (e.g. `{X -> f(Y), Y -> a}` is kept as is).
     */
    override fun merge(
        substitution1: Substitution,
        substitution2: Substitution,
        occurCheckEnabled: Boolean,
    ): Substitution {
        val request = MergeRequest(substitution1, substitution2, occurCheckEnabled)
        val run =
            when {
                substitution1.isFailed || substitution2.isFailed || context.isFailed -> null
                // with nothing to reconcile it with, substitution1 (assumed idempotent) is the base of the run
                context.isEmpty() -> newRun(substitution1, request)
                else -> newRun(context, request)?.takeIf { it.merge(substitution1) }
            }?.takeIf { it.merge(substitution2) } ?: return handleResult(request, Substitution.failed())
        return handleResult(request, run.solve())
    }
}
