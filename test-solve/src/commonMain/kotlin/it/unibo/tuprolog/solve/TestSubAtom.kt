package it.unibo.tuprolog.solve

interface TestSubAtom : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestSubAtomImpl =
            TestSubAtomImpl(solverFactory)
    }

    /**
     *  * ?- sub_atom(abracadabra, 0, 1, 10, S).
     */

    fun testSubAtomSubIsVar()

    /**
     *?- sub_atom(abracadabra, 6, 5, 0, S).
     */

    fun testSubAtomSubIsVar2()

    /**
     *?-sub_atom(abracadabra, 3, L, 3, S).
     */

    fun testSubAtomSubIsVar3()

    /**
     *?-sub_atom('Banana', 3, 2, T, S).
     */

    fun testSubAtomDoubleVar4()

    /**
     * ?- sub_atom(Banana, 3, 2, _, S2).
     */

    fun testSubAtomInstantiationError()

    /**
     * ?- sub_atom(5, 2, 2, _, S2).
     */

    fun testSubAtomTypeErrorAtomIsInteger()

    /**
     * ?-sub_atom('Banana', 4, 2, _, 2).
     */

    fun testSubAtomTypeErrorSubIsInteger()

    /**
     * ?_ sub_atom('Banana', a, 2, _, S2).
     */

    fun testSubAtomTypeErrorBeforeIsNotInteger()

    /**
     * ?_ sub_atom('Banana', 4, n, _, S2).
     */

    fun testSubAtomTypeErrorLengthIsNotInteger()

    /**
     * ?_ sub_atom('Banana', 4, _, m, S2).
     */

    fun testSubAtomTypeErrorAfterIsNotInteger()
}
