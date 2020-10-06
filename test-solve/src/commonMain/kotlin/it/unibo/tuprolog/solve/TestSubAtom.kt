package it.unibo.tuprolog.solve

interface TestSubAtom : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestSubAtomImpl =
            TestSubAtomImpl(solverFactory)
    }

    /**
     *  * ?- sub_atom(abracadabra, 0, 5, _, S2).
     */

    fun testSubAtomSubIsVar()

    /**
     *?- sub_atom(abracadabra, _, 5, 0, S2).
     */

    fun testSubAtomSubIsVar2()

    /**
     *?-sub_atom(abracadabra, 3, L, 3, S2).
     */

    fun testSubAtomSubIsVar3()

    /**
     *?-sub_atom('Banana', 3, 2, _, S2).
     */

    fun testSubAtomDoubleVar4()
}
