package it.unibo.tuprolog.solve.library.impl

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.library.Pluggable

/** Base [Pluggable] implementation providing [hasRule] via a lazily-computed, cached set of [rulesSignatures]. */
abstract class AbstractPluggable : Pluggable {
    private val rulesSignaturesCache: Set<Signature> by lazy {
        rulesSignatures.toSet()
    }

    override fun hasRule(signature: Signature): Boolean = signature in rulesSignaturesCache
}
