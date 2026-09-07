package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Cache
import it.unibo.tuprolog.utils.Optional

private typealias MguRequestForTerms = Triple<Term, Term, Boolean>
private typealias MguRequestForSubstitutions = Triple<Substitution, Substitution, Boolean>
private typealias MguRequest = Triple<*, *, Boolean>

/**
 * A [Unificator] decorator that memoizes the results of [mgu] and [merge] calls made on [decorated], in a shared
 * LRU cache of at most [cacheCapacity] entries, so that repeated calls with the same arguments (including the same
 * `occurCheckEnabled` flag) are served from the cache instead of being recomputed.
 *
 * Since [Unificator.match] and [Unificator.unify] are, by default, defined in terms of [Unificator.mgu], they too
 * benefit from the cache transitively — no separate caching is performed for them.
 *
 * Cache keys are built from [term1]/[term2] (or the two [Substitution]s, for [merge]) compared via [Term.equals]/
 * [Substitution.equals]; two structurally equal but distinct term/substitution instances therefore share a cache
 * entry. This class is safe for concurrent use from multiple threads: the underlying cache synchronizes its access.
 *
 * Instances are normally created through [Unificator.cached] rather than directly, since that factory also avoids
 * double-wrapping an already-cached [Unificator].
 *
 * @param decorated the [Unificator] whose results are being cached
 * @param cacheCapacity the maximum number of entries the LRU cache may hold, shared between [mgu] and [merge]
 */
class CachedUnificator(
    val decorated: Unificator,
    cacheCapacity: Int,
) : Unificator {
    private val mguCache: Cache<MguRequest, Substitution> = Cache.simpleLru(cacheCapacity)

    /** Delegates to [decorated]'s context. */
    override val context: Substitution
        get() = decorated.context

    /** Returns the cached result for ([term1], [term2], [occurCheckEnabled]), computing and caching it if absent. */
    override fun mgu(
        term1: Term,
        term2: Term,
        occurCheckEnabled: Boolean,
    ): Substitution {
        val mguRequest = MguRequestForTerms(term1, term2, occurCheckEnabled)
        return when (val cached = mguCache[mguRequest]) {
            is Optional.Some -> cached.value
            else -> {
                val mguResult = decorated.mgu(term1, term2, occurCheckEnabled)
                mguCache[mguRequest] = mguResult
                mguResult
            }
        }
    }

    /**
     * Returns the cached result for ([substitution1], [substitution2], [occurCheckEnabled]), computing and caching
     * it if absent. Shares the same underlying LRU cache (and thus the same [cacheCapacity] budget) as [mgu].
     */
    override fun merge(
        substitution1: Substitution,
        substitution2: Substitution,
        occurCheckEnabled: Boolean,
    ): Substitution {
        val mguRequest = MguRequestForSubstitutions(substitution1, substitution2, occurCheckEnabled)
        return when (val cached = mguCache[mguRequest]) {
            is Optional.Some -> cached.value
            else -> {
                val mguResult = decorated.merge(substitution1, substitution2, occurCheckEnabled)
                mguCache[mguRequest] = mguResult
                mguResult
            }
        }
    }
}
