package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpecTestFactoryConfiguration
import io.kotest.core.spec.style.funSpec

/** Registers the cases of a shared solver contract; each case gets a fresh [T] from [prototype]. */
class ContractScope<T> internal constructor(
    private val spec: FunSpecTestFactoryConfiguration,
    private val disabled: Set<String>,
    private val prototype: () -> T,
) {
    fun case(
        id: String,
        body: T.() -> Unit,
    ) {
        spec.test(id).config(enabled = id !in disabled) { prototype().body() }
    }
}

/** A Kotest factory whose cases run [prototype]'s test methods; ids in [disabled] are registered but skipped. */
fun <T> contract(
    disabled: Set<String>,
    prototype: () -> T,
    cases: ContractScope<T>.() -> Unit,
) = funSpec { ContractScope(this, disabled, prototype).cases() }
