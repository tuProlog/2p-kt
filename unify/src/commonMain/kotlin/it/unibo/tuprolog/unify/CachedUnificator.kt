package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Cache
import it.unibo.tuprolog.utils.Optional

private typealias MguRequestForTerms = Triple<Term, Term, Boolean>
private typealias MguRequestForSubstitutions = Triple<Substitution, Substitution, Boolean>
private typealias MguRequest = Triple<*, *, Boolean>

class CachedUnificator(
    val decorated: Unificator,
    cacheCapacity: Int
) : Unificator {

    private val mguCache: Cache<MguRequest, Substitution> = Cache.simpleLru(cacheCapacity)

    override val context: Substitution
        get() = decorated.context

    override fun mgu(term1: Term, term2: Term, occurCheckEnabled: Boolean): Substitution {
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

    override fun merge(
        substitution1: Substitution,
        substitution2: Substitution,
        occurCheckEnabled: Boolean
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