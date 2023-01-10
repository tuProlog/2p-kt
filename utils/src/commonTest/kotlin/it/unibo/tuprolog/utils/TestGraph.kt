package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.graphs.Graph
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestGraph {
    private val unweighted = Graph.build<String, Nothing> {
        this += node("a")
        this += node("b")
        this -= node("a")
        this += edge(node("a"), node("c"))
        this += edge(node("b"), node("c"))
        connect(node("a"), node("b"), bidirectional = true)
    }

    private val weighted = Graph.build<String, Int> {
        this += node("a")
        this += node("b")
        this -= node("a")
        this += edge(node("a"), node("c"), weight = 1)
        this += edge(node("b"), node("c"), weight = 2)
        connect(node("a"), node("b"), bidirectional = true, weight = 3)
    }

    @Test
    fun testCreationUnweighted() {
        with(unweighted) {
            assertEquals(3, size)
            assertEquals(4, edgesCount)

            assertEquals(nodes, setOf(node("a"), node("b"), node("c")))
            assertEquals(
                edges,
                setOf(
                    edge(node("a"), node("b")),
                    edge(node("b"), node("a")),
                    edge(node("a"), node("c")),
                    edge(node("b"), node("c")),
                )
            )

            assertTrue(contains(node("a")))
            assertTrue(contains(node("b")))
            assertTrue(contains(node("c")))
            assertFalse(contains(node("d")))

            assertTrue(contains(edge(node("a"), node("b"))))
            assertTrue(contains(edge(node("b"), node("a"))))
            assertTrue(contains(edge(node("a"), node("c"))))
            assertFalse(contains(edge(node("c"), node("a"))))
            assertTrue(contains(edge(node("b"), node("c"))))
            assertFalse(contains(edge(node("c"), node("b"))))

            assertTrue(containsEdgeAmong(node("a"), node("b")))
            assertTrue(containsEdgeAmong(node("b"), node("a")))
            assertTrue(containsEdgeAmong(node("a"), node("c")))
            assertFalse(containsEdgeAmong(node("c"), node("a")))
            assertTrue(containsEdgeAmong(node("b"), node("c")))
            assertFalse(containsEdgeAmong(node("c"), node("b")))
        }
    }
}
