package it.unibo.tuprolog.solve

interface TestNumberChars : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestNumberCharsImpl = TestNumberCharsImpl(solverFactory)
    }

    /**
     * [number_chars(33,L), [[L <-- ['3','3']]]].
     *
     **/

    fun testNumberCharsListIsVar()

    /**
     * [number_chars(33,['3','3']), success].
     *
     **/

    fun testNumberCharsOK()

    /**
     * [number_chars(X,['3','3']), [[X <-- 33]].
     *
     **/

    fun testNumberCharsNumIsVar()

    /**
     * [number_chars(A,['-','2','5']), [[A <-- (-25)]]].
     *
     **/

    fun testNumberCharsNumNegativeIsVar()

    /**
     * [number_chars(A,['\n',' ','3']), [[A <-- 3]]].
     *
     **/

    fun testNumberCharsSpace()

    /**
     * number_chars(A,['4','.','2']), [[A <-- 4.2]]].
     *
     **/

    fun testNumberCharsDecimalNumber()

    /**
     * [number_chars(X,['3','.','3','E','+','0']), [[X <-- 3.3]]].
     *
     **/

    fun testNumberCharsCompleteCase()

    /**
     * [number_chars(A,L), instantiation_error].
     *
     **/

    fun testNumberCharsInstationErrror()
}
