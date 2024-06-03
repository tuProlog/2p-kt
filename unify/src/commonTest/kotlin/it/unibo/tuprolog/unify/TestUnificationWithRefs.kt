package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.NullRef
import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.VariablesProvider
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestUnificationWithRefs {
    private lateinit var unificator: Unificator
    private lateinit var obj: StringBuilder
    private lateinit var ref: ObjectRef
    private lateinit var nullRef: NullRef
    private lateinit var variables: VariablesProvider

    private fun f(vararg args: Term): Struct = Struct.of("f", *args)

    @BeforeTest
    fun setUp() {
        unificator = Unificator.default
        obj = StringBuilder("hello")
        ref = ObjectRef.of(obj)
        nullRef = ObjectRef.of(null).castToNullRef()
        variables = VariablesProvider.of()
    }

    @Test
    fun testUnificationWithRefs() =
        with(variables) {
            val expected =
                Substitution.of(
                    X to ref,
                    Y to nullRef,
                )
            val actual =
                unificator.mgu(
                    f(X, nullRef),
                    f(ref, Y),
                )
            assertEquals(expected, actual)
        }

    @Test
    fun testMatchAmongNullRefs() {
        assertTrue(unificator.match(nullRef, NullRef()))
    }

    @Test
    fun testMatchAmongRefs() {
        assertTrue(unificator.match(ref, ObjectRef.of(obj)))
    }

    @Test
    fun testUnmatchAmongDifferentRefs() {
        assertFalse(unificator.match(ObjectRef.of("hello"), ObjectRef.of("HELLO".lowercase())))
    }

    @Test
    fun testUnmatchAmongDifferentRefs2() {
        assertFalse(unificator.match(ObjectRef.of(1), ObjectRef.of(2)))
    }

    @Test
    fun testDifferenceWithLogicTypes() {
        assertFalse(unificator.match(ObjectRef.of(1), Integer.of(1)))
        assertFalse(unificator.match(ObjectRef.of("hello"), Atom.of("hello")))
    }
}
