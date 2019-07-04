package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.StructImpl
import it.unibo.tuprolog.core.testutils.StructUtils
import kotlin.test.Test
import kotlin.test.assertNotEquals

/**
 * Test class for [Struct] companion object
 *
 * @author Enrico
 */
internal class StructTest {

    private val correctNonSpecialStructInstances = StructUtils.nonSpecialStructs.map { (functor, args) -> StructImpl(functor, args) }

    @Test
    fun structOfShouldCreateSubClassInstanceWithSpecialStructs() {
        val specialStructInstances = StructUtils.specialStructs.map { (functor, args) -> Struct.of(functor, *args) }
        specialStructInstances.forEach { assertNotEquals(StructImpl::class, it::class) }
    }

}
