package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.graphs.BreadthFirst
import it.unibo.tuprolog.utils.graphs.DepthFirst
import it.unibo.tuprolog.utils.graphs.Graph
import it.unibo.tuprolog.utils.graphs.edge
import it.unibo.tuprolog.utils.graphs.node
import it.unibo.tuprolog.utils.graphs.visitOf
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

    @Test
    fun testCreationWeighted() {
        assertEquals(3, weighted.size)
        assertEquals(4, weighted.edgesCount)

        assertEquals(weighted.nodes, setOf(node("a"), node("b"), node("c")))
        assertEquals(
            weighted.edges,
            setOf(
                edge(node("a"), node("b"), weight = 3),
                edge(node("b"), node("a"), weight = 3),
                edge(node("a"), node("c"), weight = 1),
                edge(node("b"), node("c"), weight = 2),
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

    @Test
    fun testLimitedPreOrderDepthFirstSearch() {
        assertEquals(
            listOf(
                visitOf(0, node("a")),
                visitOf(1, node("c")),
                visitOf(1, node("b")),
                visitOf(2, node("c")),
                visitOf(2, node("a")),
                visitOf(3, node("c")),
                visitOf(3, node("b")),
            ),
            unweighted.asSequence(DepthFirst(maxDepth = 3), node("a")).toList()
        )
    }

    @Test
    fun testPreOrderDepthFirstSearchOnGraphWithCycles() {
        assertEquals(
            listOf(
                visitOf(0, node("a")),
                visitOf(1, node("c")),
                visitOf(1, node("b")),
                visitOf(2, node("c")),
                visitOf(2, node("a")),
                visitOf(3, node("c")),
                visitOf(3, node("b")),
                visitOf(4, node("c")),
                visitOf(4, node("a")),
                visitOf(5, node("c")),
                visitOf(5, node("b")),
                visitOf(6, node("c")),
                visitOf(6, node("a")),
                visitOf(7, node("c")),
                visitOf(7, node("b")),
                visitOf(8, node("c")),
                visitOf(8, node("a")),
                visitOf(9, node("c")),
                visitOf(9, node("b")),
            ),
            unweighted.asSequence(DepthFirst(maxDepth = 9), node("a")).toList()
        )
    }

    @Test
    fun testLimitedPostOrderDepthFirstSearch() {
        assertEquals(
            listOf(
                visitOf(1, node("c")),
                visitOf(2, node("c")),
                visitOf(3, node("c")),
                visitOf(3, node("b")),
                visitOf(2, node("a")),
                visitOf(1, node("b")),
                visitOf(0, node("a")),
            ),
            unweighted.asSequence(DepthFirst(maxDepth = 3, postOrder = true), node("a")).toList()
        )
    }

    @Test
    fun testLimitedBreadthFirstSearch() {
        assertEquals(
            listOf(
                visitOf(0, node("a")),
                visitOf(1, node("c")),
                visitOf(1, node("b")),
                visitOf(2, node("c")),
                visitOf(2, node("a")),
                visitOf(3, node("c")),
                visitOf(3, node("b")),
            ),
            unweighted.asSequence(BreadthFirst(maxDepth = 3), node("a")).toList().also { it.forEach(::println) }
        )
    }

    @Test
    fun testBreadthFirstSearchOnGraphWithCycles() {
        assertEquals(
            listOf(
                visitOf(0, node("a")),
                visitOf(1, node("c")),
                visitOf(1, node("b")),
                visitOf(2, node("c")),
                visitOf(2, node("a")),
                visitOf(3, node("c")),
                visitOf(3, node("b")),
                visitOf(4, node("c")),
                visitOf(4, node("a")),
                visitOf(5, node("c")),
                visitOf(5, node("b")),
                visitOf(6, node("c")),
                visitOf(6, node("a")),
                visitOf(7, node("c")),
                visitOf(7, node("b")),
                visitOf(8, node("c")),
                visitOf(8, node("a")),
                visitOf(9, node("c")),
                visitOf(9, node("b")),
            ),
            unweighted.asSequence(BreadthFirst(maxDepth = 9), node("a")).toList()
        )
    }
}
