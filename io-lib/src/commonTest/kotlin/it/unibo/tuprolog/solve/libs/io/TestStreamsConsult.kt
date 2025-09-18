package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsConsult :
    TestConsult,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestConsult.prototype(this)

    @Test
    override fun testApi() {
        prototype.testApi()
    }

    @Test
    @Ignore
    override fun testConsultWorksLocally() {
        prototype.testConsultWorksLocally()
    }

    @Test
    @Ignore
    override fun testConsultWorksRemotely() {
        prototype.testConsultWorksRemotely()
    }

    @Test
    @Ignore
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
