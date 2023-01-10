package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.graphs.Graph
import it.unibo.tuprolog.utils.graphs.edge
import it.unibo.tuprolog.utils.graphs.node
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
        assertEquals(3, unweighted.size)
        assertEquals(4, unweighted.edgesCount)

        assertEquals(unweighted.nodes, setOf(node("a"), node("b"), node("c")))
        assertEquals(
            unweighted.edges,
            setOf(
                edge(node("a"), node("b")),
                edge(node("b"), node("a")),
                edge(node("a"), node("c")),
                edge(node("b"), node("c")),
            )
        )

        assertTrue(node("a") in unweighted)
        assertTrue(node("b") in unweighted)
        assertTrue(node("c") in unweighted)
        assertFalse(node("d") in unweighted)

        assertTrue(edge(node("a"), node("b")) in unweighted)
        assertTrue(edge(node("b"), node("a")) in unweighted)
        assertTrue(edge(node("a"), node("c")) in unweighted)
        assertFalse(edge(node("c"), node("a")) in unweighted)
        assertTrue(edge(node("b"), node("c")) in unweighted)
        assertFalse(edge(node("c"), node("b")) in unweighted)

        assertTrue(unweighted.containsEdgeAmong(node("a"), node("b")))
        assertTrue(unweighted.containsEdgeAmong(node("b"), node("a")))
        assertTrue(unweighted.containsEdgeAmong(node("a"), node("c")))
        assertFalse(unweighted.containsEdgeAmong(node("c"), node("a")))
        assertTrue(unweighted.containsEdgeAmong(node("b"), node("c")))
        assertFalse(unweighted.containsEdgeAmong(node("c"), node("b")))
    }
}
