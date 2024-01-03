package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.DirectiveImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.DirectiveUtils
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * Test class for [Directive] companion object
 *
 * @author Enrico
 */
internal class DirectiveTest {
    private val correctInstances = DirectiveUtils.mixedDirectives.map(::DirectiveImpl)

    @Test
    fun directiveOfVarargReturnsCorrectInstance() {
        val toBeTested = DirectiveUtils.mixedDirectives.map { body -> Directive.of(body) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun directiveOfVarargConstructsTupleForMultipleArguments() {
        val directiveFirstPart = Atom.of("hello")
        val directiveSecondPart = Atom.of("world")

        val correctInstance = DirectiveImpl(Tuple.wrapIfNeeded(directiveFirstPart, directiveSecondPart))
        val toBeTested = Directive.of(directiveFirstPart, directiveSecondPart)

        assertEqualities(correctInstance, toBeTested)
    }

    @Test
    fun directiveOfIterableConstructsTupleForMultipleArguments() {
        val directiveFirstPart = Atom.of("hello")
        val directiveSecondPart = Atom.of("world")

        val correctInstance = DirectiveImpl(Tuple.wrapIfNeeded(directiveFirstPart, directiveSecondPart))
        val toBeTested = Directive.of(listOf(directiveFirstPart, directiveSecondPart))

        assertEqualities(correctInstance, toBeTested)
    }

    @Test
    fun directiveOfIterableComplainsIfEmptyIterable() {
        assertFailsWith<IllegalArgumentException> { Directive.of(emptyList()) }
    }
}
