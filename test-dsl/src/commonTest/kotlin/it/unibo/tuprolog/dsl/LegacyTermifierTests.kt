package it.unibo.tuprolog.dsl

import io.kotest.core.spec.style.funSpec
import io.kotest.core.test.TestScope
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Var
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

fun legacyTermifierTests(
    createTermificator: (Scope) -> Termificator,
    replaced: Set<String> = emptySet(),
) = funSpec {
    // a spec including these tests may register its own version of the ones it [replaced]
    fun test(
        name: String,
        body: suspend TestScope.() -> Unit,
    ) {
        if (name !in replaced) this@funSpec.test(name, body)
    }

    with(TermificatorTester(createTermificator)) {
        beforeTest { setup() }

        test("term") {
            val someTerm = Var.of("X")
            assertTermificationWorks(someTerm, someTerm)
        }

        test("booleans") {
            assertTermificationWorks(true, Scope::truthOf)
            assertTermificationWorks(false, Scope::truthOf)
        }

        test("strings") {
            assertTermificationWorks("Xs", Scope::varOf)
            assertTermificationWorks("_", Scope::varOf)
            assertTermificationWorks("_X", Scope::varOf)
            assertTermificationWorks("_x", Scope::varOf)
            assertTermificationWorks("xs", Scope::atomOf)
        }

        test("chars") {
            assertTermificationWorks('X', Scope::varOf)
            assertTermificationWorks('_', Scope::varOf)
            assertTermificationWorks('x', Scope::atomOf)
        }

        test("integers") {
            assertTermificationWorks(1, Scope::intOf)
            assertTermificationWorks(BigInteger.ONE, Scope::intOf)
            assertTermificationWorks(1L, Scope::intOf)
            assertTermificationWorks(1.toShort(), Scope::intOf)
            assertTermificationWorks(1.toByte(), Scope::intOf)
        }

        test("floats") {
            assertTermificationWorks(1.5, Scope::realOf)
            assertTermificationWorks(BigDecimal.ONE + BigDecimal.ONE_HALF, Scope::realOf)
            assertTermificationWorks(1.5f, Scope::realOf)
            assertTermificationWorks(3.1, { realOf(it.toString()) })
            assertTermificationWorks(3.1f, { realOf(it.toString()) })
            assertTermificationWorks(1.0, PlatformSpecificValues.ONE_POINT_ZERO)
            assertTermificationWorks(-3, PlatformSpecificValues.MINUS_THREE)
        }

        test("arrays") {
            assertTermificationWorks(array, { array.map { intOf(it) }.let(this::logicListOf) })
        }

        test("lists") {
            assertTermificationWorks(list, { list.map { intOf(it) }.let(this::logicListOf) })
        }

        test("sets") {
            assertTermificationWorks(set, { set.map { intOf(it) }.let(this::logicListOf) })
        }

        test("sequences") {
            assertTermificationWorks(sequence, { sequence.map { intOf(it) }.let(this::logicListOf) })
        }

        test("maps") {
            assertTermificationFails(map)
        }

        test("pairs") {
            assertTermificationFails(pair)
        }

        test("triples") {
            assertTermificationFails(triple)
        }

        test("key-values") {
            assertTermificationFails(keyValue)
        }

        test("non-convertible-object") {
            assertTermificationFails(unconvertible)
        }
    }
}
