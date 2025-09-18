package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.library.Runtime
import kotlin.test.Test

class TestClassicInclude :
    TestInclude,
    SolverFactory by ClassicSolverFactory {
    private val prototype = TestInclude.prototype(this)

    override val defaultRuntime: Runtime
        get() = super.defaultRuntime + Runtime.of(IOLib)

    @Test
    override fun testLocalInclude() {
        prototype.testLocalInclude()
    }

    @Test
    override fun testLocalLoad() {
        prototype.testLocalLoad()
    }

    @Test
    override fun testRemoteInclude() {
        prototype.testRemoteInclude()
    }

    @Test
    override fun testRemoteLoad() {
        prototype.testRemoteLoad()
    }

    @Test
    override fun testMissingInclude() {
        prototype.testMissingInclude()
    }

    @Test
    override fun testMissingLoad() {
        prototype.testMissingLoad()
    }
}
