package it.unibo.tuprolog.solve

interface TestNumberCodes : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestNumberCodesImpl = TestNumberCodesImpl(solverFactory)
    }

    /**
     * [number_codes(33,L), [[L <-- [0'3,0'3]]]].
     *
     **/

    fun testNumberCodesListIsVar()

    /**
     * [number_codes(33.0,L), [[L <-- [51,51,46,48]]]].
     *
     **/

    fun testNumberCodesNumIsDecimal()

    /**
     *  [number_codes(33,L), [[L <-- [0'3,0'3]]]].
     *
     **/

    fun testNumberCodesListIsVar2()

    /**
     *  [number_codes(33,[0'3,0'3]), success].
     *
     **/

    fun testNumberCodesOk()

    /**
     *  [number_codes(33.0,[0'3,0'.,0'3,0'E,0'+,0'0,0'1]), success].
     *
     **/

    fun testNumberCodesCompleteTest()

    /**
     *  [number_codes(A,[0'-,0'2,0'5]), [[A <-- (-25)]]].
     *
     **/

    fun testNumberCodesNegativeNumber()

    /**
     * [number_codes(A,[0'0,39,0'a]), [[A <-- 97]]].
     *
     **/

    fun testNumberCodesChar()
}
