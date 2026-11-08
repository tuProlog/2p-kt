package it.unibo.tuprolog.solve.tags

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.unify.AbstractWorklistUnificator
import it.unibo.tuprolog.unify.UnificationRequest
import it.unibo.tuprolog.utils.addTags

/**
 * A worklist [it.unibo.tuprolog.unify.Unificator] customising only `handleResult`: it fails every unifier binding a
 * variable to a term tagged [FORBIDDEN], and tags the values of the others with [SEEN].
 */
internal class TagResultUnificator : AbstractWorklistUnificator() {
    override fun handleResult(
        request: UnificationRequest,
        result: Substitution,
    ): Substitution =
        when {
            result.isFailed -> result
            result.values.any { FORBIDDEN in it.tags } -> Substitution.failed()
            else -> Substitution.of(result.mapValues { (_, value) -> value.addTags(SEEN to true) })
        }

    companion object {
        const val FORBIDDEN = "forbidden"
        const val SEEN = "seen"
    }
}
