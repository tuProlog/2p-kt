package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [Cut]
 *
 * @author Enrico
 */
internal class CutTest {

    private val cutPrimitiveSignature = Signature("!", 0)

    @Test
    fun cutPrimitiveReturnsAlwaysYesResponseWithNoModificationFromRequest() {
        val substitution = Substitution.of("A", Truth.`true`())
        val context = DummyInstances.executionContextImpl.copy(substitution = substitution)
        val toBeTested = Cut.primitive(DummyInstances.solveRequest.copy(signature = cutPrimitiveSignature, context = context))

        assertEquals(1, toBeTested.count())
        with(toBeTested.single().solution) {
            assertTrue { this is Solution.Yes }
            assertEquals(cutPrimitiveSignature.withArgs(emptyList()), this.query)
            assertEquals(substitution, this.substitution)
        }
    }

//    @Test
//    fun cutPrimitiveFillsExecutionContextFieldWithAllChoicePointContextParent() {
//        fun makeRequest(executionContext: ExecutionContextImpl) =
//                DummyInstances.solveRequest.copy(signature = cutPrimitiveSignature, context = executionContext)
//
//        fun Solve.Response.underTestField(): Iterable<DeclarativeImplExecutionContext> = this.context!!.toCutContextsParent.toList()
//
//        with(DummyInstances.executionContext) {
//            val contextWithNoParents = this
//            val contextWithNoParentsButChoicePointChild = this.copy(isChoicePointChild = true)
//            val contextWithOneParentNotChoicePointChild = this.copy(clauseScopedParents = sequenceOf(this.copy()))
//            val contextWithOneParentChoicePointChild = this.copy(clauseScopedParents = sequenceOf(this.copy(isChoicePointChild = true)))
//            val contextWithTwoParentsNotChoicePointChild = this.copy(clauseScopedParents = sequenceOf(this.copy(), this.copy()))
//
//            val contextWithTwoParentsFirstChoicePointChild = this.copy(
//                    clauseScopedParents = sequenceOf(this.copy(isChoicePointChild = true, clauseScopedParents = sequenceOf(this.copy())), this.copy()))
//            val contextWithTwoParentsSecondChoicePointChild = this.copy(
//                    clauseScopedParents = sequenceOf(this.copy(), this.copy(isChoicePointChild = true, clauseScopedParents = sequenceOf(this.copy()))))
//            val contextWithTwoParentsBothChoicePointChild = this.copy(
//                    clauseScopedParents = sequenceOf(
//                            this.copy(isChoicePointChild = true, clauseScopedParents = sequenceOf(this.copy())),
//                            this.copy(isChoicePointChild = true, clauseScopedParents = sequenceOf(this.copy()))
//                    ))
//
//            assertEquals(emptyList(), Cut.primitive(makeRequest(contextWithNoParents)).single().underTestField())
//            assertEquals(emptyList(), Cut.primitive(makeRequest(contextWithNoParentsButChoicePointChild)).single().underTestField())
//            assertEquals(emptyList(), Cut.primitive(makeRequest(contextWithOneParentNotChoicePointChild)).single().underTestField())
//            assertEquals(emptyList(), Cut.primitive(makeRequest(contextWithOneParentChoicePointChild)).single().underTestField())
//            assertEquals(emptyList(), Cut.primitive(makeRequest(contextWithTwoParentsNotChoicePointChild)).single().underTestField())
//            assertSame(
//                    contextWithTwoParentsFirstChoicePointChild.clauseScopedParents.first().clauseScopedParents.first(),
//                    Cut.primitive(makeRequest(contextWithTwoParentsFirstChoicePointChild)).single().underTestField().single()
//            )
//            assertSame(
//                    contextWithTwoParentsSecondChoicePointChild.clauseScopedParents.last().clauseScopedParents.first(),
//                    Cut.primitive(makeRequest(contextWithTwoParentsSecondChoicePointChild)).single().underTestField().single()
//            )
//            assertEquals(
//                    listOf(
//                            contextWithTwoParentsBothChoicePointChild.clauseScopedParents.first().clauseScopedParents.first(),
//                            contextWithTwoParentsSecondChoicePointChild.clauseScopedParents.last().clauseScopedParents.first()
//                    ),
//                    Cut.primitive(makeRequest(contextWithTwoParentsBothChoicePointChild)).single().underTestField()
//            )
//        }
//    }
//
//    @Test
//    fun cutAddsContextsToCutInProvidedField() {
//        val alreadyPresentToCut = DummyInstances.executionContext.copy()
//        val currentlyAddedToCut = DummyInstances.executionContext.copy()
//        val startRequest = with(DummyInstances.executionContext) {
//            createSolveRequest(Atom.of("!"))
//                    .copy(context = this.copy(
//                            toCutContextsParent = sequenceOf(alreadyPresentToCut),
//                            clauseScopedParents = sequenceOf(this.copy(isChoicePointChild = true, clauseScopedParents = sequenceOf(currentlyAddedToCut)))
//                    ))
//        }
//
//        val response = Cut.primitive(startRequest).toList()
//
//        assertEquals(1, response.count())
//        assertSame(2, response.single().context!!.toCutContextsParent.count())
//        assertSame(alreadyPresentToCut, response.single().context!!.toCutContextsParent.first())
//        assertSame(currentlyAddedToCut, response.single().context!!.toCutContextsParent.last())
//    }
}
