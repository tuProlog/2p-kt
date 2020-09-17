package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.successfulUnifications
import java.util.Collections
import java.util.LinkedList
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertTrue

class TestThreadSafety {
    @Test
    fun testCachedUnificatorThreadSafety() {
        val executors = Executors.newFixedThreadPool(8)
        val results: MutableList<Future<Substitution>> = Collections.synchronizedList(LinkedList())
        val unificator = Unificator.cached(Unificator.strict())
        for (i in 1..100) {
            for (equation in successfulUnifications.keys) {
                results.add(
                    executors.submit<Substitution> {
                        unificator.mgu(equation.lhs, equation.rhs)
                    }
                )
            }
        }
        executors.shutdown()
        executors.awaitTermination(1, TimeUnit.MINUTES)
        for (res in results) {
            assertTrue { res.get() is Substitution.Unifier }
        }
    }
}
