package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Cache
import it.unibo.tuprolog.utils.Optional

private typealias MguRequest = Triple<Term, Term, Boolean>

class CachedUnificator(
    val decorated: Unificator,
    cacheCapacity: Int
) : Unificator {

    private val mguCache: Cache<MguRequest, Substitution> = Cache.simpleLru(cacheCapacity)

    override val context: Substitution
        get() = decorated.context

    override fun mgu(term1: Term, term2: Term, occurCheckEnabled: Boolean): Substitution {
        val mguRequest = MguRequest(term1, term2, occurCheckEnabled)
        return when (val cached = mguCache[mguRequest]) {
            is Optional.Some -> cached.value
            else -> {
                val mguResult = decorated.mgu(term1, term2, occurCheckEnabled)
                mguCache[mguRequest] = mguResult
                mguResult
            }
        }
    }
}