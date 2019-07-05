package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.DirectiveImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.DirectiveUtils
import kotlin.test.Test

/**
 * Test class for [Directive] companion object
 *
 * @author Enrico
 */
internal class DirectiveTest {

    private val correctInstances = DirectiveUtils.mixedDirectives.map(::DirectiveImpl)

    @Test
    fun directiveOfReturnsCorrectInstance() {
        val toBeTested = DirectiveUtils.mixedDirectives.map { body -> Directive.of(body) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun directiveOfConstructsTupleForMultipleArguments() {
        val directiveFirstPart = Atom.of("hello")
        val directiveSecondPart = Atom.of("world")

        val correctInstance = DirectiveImpl(Tuple.wrapIfNeeded(directiveFirstPart, directiveSecondPart))
        val toBeTested = Directive.of(directiveFirstPart, directiveSecondPart)

        assertEqualities(correctInstance, toBeTested)
    }
}
