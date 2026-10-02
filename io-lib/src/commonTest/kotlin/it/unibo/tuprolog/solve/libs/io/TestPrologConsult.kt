package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import kotlin.test.Test

class TestPrologConsult :
    TestConsult,
    SolverFactory by PrologSolverFactory {
    private val prototype = TestConsult.prototype(this)

    @Test
    override fun testApi() {
        prototype.testApi()
    }

    @Test
    override fun testConsultWorksLocally() {
        prototype.testConsultWorksLocally()
    }

    @Test
    override fun testConsultWorksRemotely() {
        prototype.testConsultWorksRemotely()
    }

    @Test
    override fun testConsultingWrongTheoryWorksLocally() {
        prototype.testConsultWorksRemotely()
    }

    @Test
    override fun testConsultingWrongTheoryWorksRemotely() {
        prototype.testConsultingWrongTheoryWorksRemotely()
    }

    @Test
    override fun testConsultingMissingTheoryWorksLocally() {
        prototype.testConsultingMissingTheoryWorksLocally()
    }

    @Test
    override fun testConsultingMissingTheoryWorksRemotely() {
        prototype.testConsultingMissingTheoryWorksRemotely()
    }
}
