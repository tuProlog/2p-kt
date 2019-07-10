package it.unibo.tuprolog.unify

import it.unibo.tuprolog.unify.testutils.UnificationUtils
import it.unibo.tuprolog.unify.testutils.UnificationUtils.assertMatchCorrectForMultipleEq
import it.unibo.tuprolog.unify.testutils.UnificationUtils.assertMguCorrectForMultipleEq
import it.unibo.tuprolog.unify.testutils.UnificationUtils.assertUnifiedTermCorrectForMultipleEq
import kotlin.test.Test

/**
 * Test class for [Unification] companion object
 *
 * @author Enrico
 */
internal class UnificationTesting {

    @Test
    fun sequenceOfEquationsSuccessUnification() {
        assertMguCorrectForMultipleEq(UnificationUtils.successSequenceOfUnification, Unification.Companion::naive, true)
        assertMguCorrectForMultipleEq(UnificationUtils.successSequenceOfUnification, Unification.Companion::naive, false)

        assertMatchCorrectForMultipleEq(UnificationUtils.successSequenceOfUnification, Unification.Companion::naive, true)
        assertMatchCorrectForMultipleEq(UnificationUtils.successSequenceOfUnification, Unification.Companion::naive, false)

        assertUnifiedTermCorrectForMultipleEq(UnificationUtils.successSequenceOfUnification, Unification.Companion::naive, true)
        assertUnifiedTermCorrectForMultipleEq(UnificationUtils.successSequenceOfUnification, Unification.Companion::naive, false)
    }

//    @Test
//    fun sequenceOfEquationsFailedUnification() {
//        assertMguCorrectForMultipleEq(UnificationUtils.failSequenceOfUnification, Unification.Companion::naive, true)
//        assertMguCorrectForMultipleEq(UnificationUtils.failSequenceOfUnification, Unification.Companion::naive, false)
//
//        assertMatchCorrectForMultipleEq(UnificationUtils.failSequenceOfUnification, Unification.Companion::naive, true)
//        assertMatchCorrectForMultipleEq(UnificationUtils.failSequenceOfUnification, Unification.Companion::naive, false)
//
//        assertUnifiedTermCorrectForMultipleEq(UnificationUtils.failSequenceOfUnification, Unification.Companion::naive, true)
//        assertUnifiedTermCorrectForMultipleEq(UnificationUtils.failSequenceOfUnification, Unification.Companion::naive, false)
//    }
}
