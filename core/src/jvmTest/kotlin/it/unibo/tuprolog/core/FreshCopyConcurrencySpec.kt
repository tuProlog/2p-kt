package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.util.concurrent.Callable
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import it.unibo.tuprolog.core.List as LogicList

class FreshCopyConcurrencySpec :
    FunSpec({
        test("fresh-copy/concurrent-lazy-children-share-renamed-variables") {
            val workers = 8
            val barrier = CyclicBarrier(workers)
            val variables =
                List(512) { index ->
                    val original = Var.of("X$index")
                    object : Var by original {
                        override fun freshCopy(scope: Scope): Var {
                            barrier.await(10, TimeUnit.SECONDS)
                            return original.freshCopy(scope)
                        }
                    }
                }
            val source =
                Struct.of(
                    "branches",
                    listOf(Var.of("Prefix")) +
                        List(workers) {
                            LogicList.of(sequenceOf(Atom.of("start")) + variables.asSequence())
                        },
                )
            val copy = source.freshCopy()
            val executor = Executors.newFixedThreadPool(workers)
            try {
                val futures =
                    copy.args.drop(1).map { child ->
                        executor.submit(
                            Callable {
                                child
                                    .castToList()
                                    .toSequence()
                                    .drop(1)
                                    .toList()
                            },
                        )
                    }
                val results = futures.map { it.get(30, TimeUnit.SECONDS) }
                for (index in variables.indices) {
                    val renamed = results.first()[index]
                    renamed shouldNotBe variables[index]
                    for (result in results) {
                        result[index] shouldBeSameInstanceAs renamed
                    }
                }
            } finally {
                executor.shutdownNow()
            }
        }
    })
