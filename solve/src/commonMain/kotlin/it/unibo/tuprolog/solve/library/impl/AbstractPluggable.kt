package it.unibo.tuprolog.solve.library.impl

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.library.Pluggable
import it.unibo.tuprolog.solve.library.defaultDocumentation

/** Base [Pluggable] implementation providing [hasRule] via a lazily-computed, cached set of [rulesSignatures]. */
abstract class AbstractPluggable : Pluggable {
    override val documentation: Map<Signature, String>
        get() = defaultDocumentation(this)

    // read through `.value`, not `by`: on JS, delegation builds a property reference at every read
    private val lazyRulesSignaturesCache =
        lazy {
            rulesSignatures.toSet()
        }

    private val rulesSignaturesCache: Set<Signature>
        get() = lazyRulesSignaturesCache.value

    override fun hasRule(signature: Signature): Boolean = signature in rulesSignaturesCache
}
