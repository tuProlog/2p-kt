package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.collections.impl.Factories
import it.unibo.tuprolog.theory.PrototypeTheoryTest
import kotlin.test.Test

internal class MutableIndexedTheoryTest {

    private val prototype = PrototypeTheoryTest(
        Factories::emptyMutableIndexedTheory,
        Factories::mutableIndexedTheoryOf
    )

    // @BeforeTest
    // fun init() {
    //     prototype.init()
    // }

    @Test
    fun theoryComplainsIFProvidingNotWellFormedClausesUponConstruction() {
        prototype.theoryComplainsIFProvidingNotWellFormedClausesUponConstruction()
    }

    @Test
    fun clausesCorrect() {
        prototype.clausesCorrect()
    }

    @Test
    fun rulesCorrect() {
        prototype.rulesCorrect()
    }

    @Test
    fun directivesCorrect() {
        prototype.directivesCorrect()
    }

    @Test
    fun plusTheoryPreservesOrder() {
        prototype.plusTheoryPreservesOrder()
    }

    @Test
    fun plusTheoryFailsOnBadTheory() {
        prototype.plusTheoryFailsOnBadTheory()
    }

    @Test
    fun plusClause() {
        prototype.plusClause()
    }

    @Test
    fun plusClauseRespectsPartialOrdering() {
        prototype.plusClauseRespectsPartialOrdering()
    }

    @Test
    fun plusClauseReturnsNewUnlinkedTheoryInstance() {
        prototype.plusClauseReturnsNewUnlinkedTheoryInstance()
    }

    @Test
    fun plusClauseComplainsOnBadClause() {
        prototype.plusClauseComplainsOnBadClause()
    }

    @Test
    fun containsClauseReturnsTrueWithPresentClauses() {
        prototype.containsClauseReturnsTrueWithPresentClauses()
    }

    @Test
    fun containsClauseReturnsFalseWithNonPresentClauses() {
        prototype.containsClauseReturnsFalseWithNonPresentClauses()
    }

    @Test
    fun containsStructReturnsTrueIfMatchingHeadIsFound() {
        prototype.containsStructReturnsTrueIfMatchingHeadIsFound()
    }

    @Test
    fun containsStructReturnsFalseIfNoMatchingHeadIsFound() {
        prototype.containsStructReturnsFalseIfNoMatchingHeadIsFound()
    }

    @Test
    fun containsIndicatorReturnsTrueIfMatchingClauseIsFound() {
        prototype.containsIndicatorReturnsTrueIfMatchingClauseIsFound()
    }

    @Test
    fun containsIndicatorReturnsFalseIfNoMatchingClauseIsFound() {
        prototype.containsIndicatorReturnsFalseIfNoMatchingClauseIsFound()
    }

    @Test
    fun containsIndicatorComplainsIfIndicatorNonWellFormed() {
        prototype.containsIndicatorComplainsIfIndicatorNonWellFormed()
    }

    @Test
    fun getClause() {
        prototype.getClause()
    }

    @Test
    fun getStruct() {
        prototype.getStruct()
    }

    @Test
    fun getIndicator() {
        prototype.getIndicator()
    }

    @Test
    fun getIndicatorComplainsIfIndicatorNonWellFormed() {
        prototype.getIndicatorComplainsIfIndicatorNonWellFormed()
    }

    @Test
    fun assertAClause() {
        prototype.assertAClause()
    }

    @Test
    fun assertAClauseComplainsOnBadClause() {
        prototype.assertAClauseComplainsOnBadClause()
    }

    @Test
    fun assertAStruct() {
        prototype.assertAStruct()
    }

    @Test
    fun assertACreatesNewUnlinkedInstance() {
        prototype.assertACreatesNewUnlinkedInstance()
    }

    @Test
    fun assertZClause() {
        prototype.assertZClause()
    }

    @Test
    fun assertZClauseComplainsOnBadClause() {
        prototype.assertZClauseComplainsOnBadClause()
    }

    @Test
    fun assertZStruct() {
        prototype.assertZStruct()
    }

    @Test
    fun assertZCreatesNewUnlinkedInstance() {
        prototype.assertZCreatesNewUnlinkedInstance()
    }

    @Test
    fun retractClauseReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        prototype.retractClauseReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise()
    }

    @Test
    fun retractClauseRemovesOnlyFirstMatchingClause() {
        prototype.retractClauseRemovesOnlyFirstMatchingClause()
    }

    @Test
    fun retractStructReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        prototype.retractStructReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise()
    }

    @Test
    fun retractStructRemovesOnlyFirstMatchingClause() {
        prototype.retractStructRemovesOnlyFirstMatchingClause()
    }

    @Test
    fun retractCreatesNewUnlinkedInstanceIfSuccessful() {
        prototype.retractCreatesNewUnlinkedInstanceIfSuccessful()
    }

    @Test
    fun retractReturnsSameTheoryOnFailure() {
        prototype.retractReturnsSameTheoryOnFailure()
    }

    @Test
    fun retractAllClauseReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        prototype.retractAllClauseReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise()
    }

    @Test
    fun retractAllClauseRemovesOnlyFirstMatchingClause() {
        prototype.retractAllClauseRemovesOnlyFirstMatchingClause()
    }

    @Test
    fun retractAllStructReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        prototype.retractAllStructReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise()
    }

    @Test
    fun retractAllStructRemovesOnlyFirstMatchingClause() {
        prototype.retractAllStructRemovesOnlyFirstMatchingClause()
    }

    @Test
    fun retractAllCreatesNewUnlinkedInstanceIfSuccessful() {
        prototype.retractAllCreatesNewUnlinkedInstanceIfSuccessful()
    }

    @Test
    fun retractAllReturnsSameTheoryOnFailure() {
        prototype.retractAllReturnsSameTheoryOnFailure()
    }

    @Test
    fun iteratorReturnsCorrectInstance() {
        prototype.iteratorReturnsCorrectInstance()
    }

    @Test
    fun getTakesUnificationIntoAccount() {
        prototype.getTakesUnificationIntoAccount()
    }

    @Test
    fun retractTakesUnificationIntoAccount() {
        prototype.retractTakesUnificationIntoAccount()
    }

    @Test
    fun nestedGetWorksAtSeveralDepthLevels() {
        prototype.nestedGetWorksAtSeveralDepthLevels()
    }

    @Test
    fun nestedRetractWorksAtSeveralDepthLevels() {
        prototype.nestedRetractWorksAtSeveralDepthLevels()
    }
}
