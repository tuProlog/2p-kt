package it.unibo.tuprolog.solve.solver.statemachine.state

/**
 * Test class for [StateEnd] and subclasses
 *
 * @author Enrico
 */
internal class StateEndTest {

//    private val myScope = Scope.empty()
//    private val solveRequest = Solve.Request(
//            Signature("p", 2),
//            listOf(myScope.varOf("A"), myScope.varOf("B")),
//            Truth.`true`(),
//            DummyInstances.executionContext
//    )
//    private val anException = HaltException(context = DummyInstances.executionContext)
//
//    /** A list with all state types instances */
//    private val allStateList = listOf(
//            StateEnd.True(solveRequest, executionStrategy),
//            StateEnd.False(solveRequest, executionStrategy),
//            StateEnd.Halt(solveRequest, executionStrategy, anException)
//    )
//
//    @Test
//    fun trueStateHoldInsertedData() {
//        val toBeTested = StateEnd.True(solveRequest, executionStrategy)
//        assertEquals(solveRequest, toBeTested.solve)
//    }
//
//    @Test
//    fun falseStateHoldInsertedData() {
//        val toBeTested = StateEnd.False(solveRequest, executionStrategy)
//        assertEquals(solveRequest, toBeTested.solve)
//    }
//
//    @Test
//    fun haltStateHoldInsertedData() {
//        val toBeTested = StateEnd.Halt(solveRequest, executionStrategy, anException)
//        assertEquals(solveRequest, toBeTested.solve)
//        assertEquals(anException, toBeTested.exception)
//    }
//
//    @Test
//    fun allStateEndInstancesReturnEmptyNextStatesSequence() {
//        allStateList.forEach { assertTrue { it.behave().none() } }
//    }
//
//    @Test
//    fun makeCopyRedirectsCallToCorrectCopyMethod() {
//        // precondition
//        allStateList.forEach { it.solve != DummyInstances.solveRequest }
//
//        val toBeTested = allStateList.map { it.makeCopy(DummyInstances.solveRequest) }
//
//        allStateList.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected::class, actual::class) }
//        toBeTested.forEach { assertSame(DummyInstances.solveRequest, it.solve) }
//    }
}
