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
        this += edge(node("a"), node("b"))
        this += edge(node("a"), node("c"))
        this += edge(node("b"), node("d"))
        this += edge(node("b"), node("e"))
        this += edge(node("e"), node("f"))
        connect(node("a"), node("f"), bidirectional = true)
    }

    private val weighted = Graph.build<String, Int> {
        this += node("a")
        this += node("b")
        this -= node("a")
        this += edge(node("a"), node("b"), 1)
        this += edge(node("a"), node("c"), 2)
        this += edge(node("b"), node("d"), 4)
        this += edge(node("b"), node("e"), 5)
        this += edge(node("e"), node("f"), 6)
        connect(node("a"), node("f"), bidirectional = true, weight = 3)
    }

    @Test
    fun testCreationUnweighted() {
        assertEquals(6, unweighted.size)
        assertEquals(7, unweighted.edgesCount)

        assertEquals(unweighted.nodes, listOf("a", "b", "c", "d", "e", "f").map { node(it) }.toSet())
        assertEquals(
            unweighted.edges,
            setOf(
                edge(node("a"), node("b")),
                edge(node("a"), node("c")),
                edge(node("b"), node("d")),
                edge(node("b"), node("e")),
                edge(node("e"), node("f")),
                edge(node("a"), node("f")),
                edge(node("f"), node("a"))
            )
        )

        assertTrue(node("a") in unweighted)
        assertTrue(node("b") in unweighted)
        assertTrue(node("c") in unweighted)
        assertTrue(node("d") in unweighted)
        assertTrue(node("e") in unweighted)
        assertTrue(node("f") in unweighted)
        assertFalse(node("g") in unweighted)

        assertTrue(edge(node("a"), node("b")) in unweighted)
        assertTrue(edge(node("a"), node("c")) in unweighted)
        assertTrue(edge(node("b"), node("d")) in unweighted)
        assertTrue(edge(node("b"), node("e")) in unweighted)
        assertTrue(edge(node("e"), node("f")) in unweighted)
        assertTrue(edge(node("a"), node("f")) in unweighted)
        assertTrue(edge(node("f"), node("a")) in unweighted)
        assertFalse(edge(node("b"), node("c")) in unweighted)
        assertFalse(edge(node("d"), node("e")) in unweighted)

        assertTrue(unweighted.containsEdgeAmong(node("a"), node("b")))
        assertTrue(unweighted.containsEdgeAmong(node("a"), node("c")))
        assertTrue(unweighted.containsEdgeAmong(node("b"), node("d")))
        assertTrue(unweighted.containsEdgeAmong(node("b"), node("e")))
        assertTrue(unweighted.containsEdgeAmong(node("e"), node("f")))
        assertTrue(unweighted.containsEdgeAmong(node("a"), node("f")))
        assertTrue(unweighted.containsEdgeAmong(node("f"), node("a")))
        assertFalse(unweighted.containsEdgeAmong(node("b"), node("c")))
        assertFalse(unweighted.containsEdgeAmong(node("d"), node("e")))
    }

    @Test
    fun testCreationWeighted() {
        assertEquals(6, weighted.size)
        assertEquals(7, weighted.edgesCount)

        assertEquals(weighted.nodes, listOf("a", "b", "c", "d", "e", "f").map { node(it) }.toSet())
        assertEquals(
            weighted.edges,
            setOf(
                edge(node("a"), node("b"), 1),
                edge(node("a"), node("c"), 2),
                edge(node("b"), node("d"), 4),
                edge(node("b"), node("e"), 5),
                edge(node("e"), node("f"), 6),
                edge(node("a"), node("f"), 3),
                edge(node("f"), node("a"), 3)
            )
        )

        assertTrue(node("a") in weighted)
        assertTrue(node("b") in weighted)
        assertTrue(node("c") in weighted)
        assertTrue(node("d") in weighted)
        assertTrue(node("e") in weighted)
        assertTrue(node("f") in weighted)
        assertFalse(node("g") in weighted)

        assertTrue(edge(node("a"), node("b"), 1) in weighted)
        assertTrue(edge(node("a"), node("c"), 2) in weighted)
        assertTrue(edge(node("b"), node("d"), 4) in weighted)
        assertTrue(edge(node("b"), node("e"), 5) in weighted)
        assertTrue(edge(node("e"), node("f"), 6) in weighted)
        assertTrue(edge(node("a"), node("f"), 3) in weighted)
        assertTrue(edge(node("f"), node("a"), 3) in weighted)
        assertFalse(edge(node("b"), node("c")) in weighted)
        assertFalse(edge(node("d"), node("e")) in weighted)

        assertTrue(weighted.containsEdgeAmong(node("a"), node("b")))
        assertTrue(weighted.containsEdgeAmong(node("a"), node("c")))
        assertTrue(weighted.containsEdgeAmong(node("b"), node("d")))
        assertTrue(weighted.containsEdgeAmong(node("b"), node("e")))
        assertTrue(weighted.containsEdgeAmong(node("e"), node("f")))
        assertTrue(weighted.containsEdgeAmong(node("a"), node("f")))
        assertTrue(weighted.containsEdgeAmong(node("f"), node("a")))
        assertFalse(weighted.containsEdgeAmong(node("b"), node("c")))
        assertFalse(weighted.containsEdgeAmong(node("d"), node("e")))
    }

    @Test
    fun testLimitedPreOrderDepthFirstSearch() {
        val expected = listOf(
            visitOf(0, node("a")),
            visitOf(1, node("b")),
            visitOf(2, node("d")),
            visitOf(2, node("e")),
            visitOf(3, node("f")),
            visitOf(1, node("c")),
            visitOf(1, node("f")),
            visitOf(2, node("a")),
            visitOf(3, node("b")),
            visitOf(3, node("c")),
            visitOf(3, node("f")),
        )
        assertEquals(expected, unweighted.asSequence(DepthFirst(maxDepth = 3), node("a")).toList())
        assertEquals(expected, weighted.asSequence(DepthFirst(maxDepth = 3), node("a")).toList())
    }

    @Test
    fun testPreOrderDepthFirstSearchOnGraphWithCycles() {
        val expected = listOf(
            visitOf(0, node("a")),
            visitOf(1, node("b")),
            visitOf(2, node("d")),
            visitOf(2, node("e")),
            visitOf(3, node("f")),
            visitOf(4, node("a")),
            visitOf(5, node("b")),
            visitOf(6, node("d")),
            visitOf(6, node("e")),
            visitOf(7, node("f")),
            visitOf(8, node("a")),
            visitOf(9, node("b")),
            visitOf(9, node("c")),
            visitOf(9, node("f")),
            visitOf(5, node("c")),
            visitOf(5, node("f")),
            visitOf(6, node("a")),
            visitOf(7, node("b")),
            visitOf(8, node("d")),
            visitOf(8, node("e")),
            visitOf(9, node("f")),
            visitOf(7, node("c")),
            visitOf(7, node("f")),
            visitOf(8, node("a")),
            visitOf(9, node("b")),
            visitOf(9, node("c")),
            visitOf(9, node("f")),
            visitOf(1, node("c")),
            visitOf(1, node("f")),
            visitOf(2, node("a")),
            visitOf(3, node("b")),
            visitOf(4, node("d")),
            visitOf(4, node("e")),
            visitOf(5, node("f")),
            visitOf(6, node("a")),
            visitOf(7, node("b")),
            visitOf(8, node("d")),
            visitOf(8, node("e")),
            visitOf(9, node("f")),
            visitOf(7, node("c")),
            visitOf(7, node("f")),
            visitOf(8, node("a")),
            visitOf(9, node("b")),
            visitOf(9, node("c")),
            visitOf(9, node("f")),
            visitOf(3, node("c")),
            visitOf(3, node("f")),
            visitOf(4, node("a")),
            visitOf(5, node("b")),
            visitOf(6, node("d")),
            visitOf(6, node("e")),
            visitOf(7, node("f")),
            visitOf(8, node("a")),
            visitOf(9, node("b")),
            visitOf(9, node("c")),
            visitOf(9, node("f")),
            visitOf(5, node("c")),
            visitOf(5, node("f")),
            visitOf(6, node("a")),
            visitOf(7, node("b")),
            visitOf(8, node("d")),
            visitOf(8, node("e")),
            visitOf(9, node("f")),
            visitOf(7, node("c")),
            visitOf(7, node("f")),
            visitOf(8, node("a")),
            visitOf(9, node("b")),
            visitOf(9, node("c")),
            visitOf(9, node("f")),
        )
        assertEquals(expected, unweighted.asSequence(DepthFirst(maxDepth = 9), node("a")).toList())
        assertEquals(expected, weighted.asSequence(DepthFirst(maxDepth = 9), node("a")).toList())
    }

    @Test
    fun testLimitedPostOrderDepthFirstSearch() {
        val expected = listOf(
            visitOf(2, node("d")),
            visitOf(3, node("f")),
            visitOf(2, node("e")),
            visitOf(1, node("b")),
            visitOf(1, node("c")),
            visitOf(3, node("b")),
            visitOf(3, node("c")),
            visitOf(3, node("f")),
            visitOf(2, node("a")),
            visitOf(1, node("f")),
            visitOf(0, node("a")),
        )
        assertEquals(expected, unweighted.asSequence(DepthFirst(maxDepth = 3, postOrder = true), node("a")).toList())
        assertEquals(expected, weighted.asSequence(DepthFirst(maxDepth = 3, postOrder = true), node("a")).toList())
    }

    @Test
    fun testLimitedBreadthFirstSearch() {
        val expected = listOf(
            visitOf(0, node("a")),
            visitOf(1, node("b")),
            visitOf(1, node("c")),
            visitOf(1, node("f")),
            visitOf(2, node("d")),
            visitOf(2, node("e")),
            visitOf(2, node("a")),
            visitOf(3, node("f")),
            visitOf(3, node("b")),
            visitOf(3, node("c")),
            visitOf(3, node("f")),
        )
        assertEquals(expected, unweighted.asSequence(BreadthFirst(maxDepth = 3), node("a")).toList())
        assertEquals(expected, weighted.asSequence(BreadthFirst(maxDepth = 3), node("a")).toList())
    }

    @Test
    fun testBreadthFirstSearchOnGraphWithCycles() {
        val expected = listOf(
            visitOf(0, node("a")),
            visitOf(1, node("b")),
            visitOf(1, node("c")),
            visitOf(1, node("f")),
            visitOf(2, node("d")),
            visitOf(2, node("e")),
            visitOf(2, node("a")),
            visitOf(3, node("f")),
            visitOf(3, node("b")),
            visitOf(3, node("c")),
            visitOf(3, node("f")),
            visitOf(4, node("a")),
            visitOf(4, node("d")),
            visitOf(4, node("e")),
            visitOf(4, node("a")),
            visitOf(5, node("b")),
            visitOf(5, node("c")),
            visitOf(5, node("f")),
            visitOf(5, node("f")),
            visitOf(5, node("b")),
            visitOf(5, node("c")),
            visitOf(5, node("f")),
            visitOf(6, node("d")),
            visitOf(6, node("e")),
            visitOf(6, node("a")),
            visitOf(6, node("a")),
            visitOf(6, node("d")),
            visitOf(6, node("e")),
            visitOf(6, node("a")),
            visitOf(7, node("f")),
            visitOf(7, node("b")),
            visitOf(7, node("c")),
            visitOf(7, node("f")),
            visitOf(7, node("b")),
            visitOf(7, node("c")),
            visitOf(7, node("f")),
            visitOf(7, node("f")),
            visitOf(7, node("b")),
            visitOf(7, node("c")),
            visitOf(7, node("f")),
            visitOf(8, node("a")),
            visitOf(8, node("d")),
            visitOf(8, node("e")),
            visitOf(8, node("a")),
            visitOf(8, node("d")),
            visitOf(8, node("e")),
            visitOf(8, node("a")),
            visitOf(8, node("a")),
            visitOf(8, node("d")),
            visitOf(8, node("e")),
            visitOf(8, node("a")),
            visitOf(9, node("b")),
            visitOf(9, node("c")),
            visitOf(9, node("f")),
            visitOf(9, node("f")),
            visitOf(9, node("b")),
            visitOf(9, node("c")),
            visitOf(9, node("f")),
            visitOf(9, node("f")),
            visitOf(9, node("b")),
            visitOf(9, node("c")),
            visitOf(9, node("f")),
            visitOf(9, node("b")),
            visitOf(9, node("c")),
            visitOf(9, node("f")),
            visitOf(9, node("f")),
            visitOf(9, node("b")),
            visitOf(9, node("c")),
            visitOf(9, node("f")),
        )
        assertEquals(expected, unweighted.asSequence(BreadthFirst(maxDepth = 9), node("a")).toList())
        assertEquals(expected, weighted.asSequence(BreadthFirst(maxDepth = 9), node("a")).toList())
    }

    @Test
    fun removeNodeImmutable() {
        val withoutA = weighted - node("a")
        assertTrue(withoutA.edges.flatMap { listOf(it.source, it.destination) }.none { it == node("a") })
    }

    @Test
    fun removeNodeMutable() {
        val withoutA = unweighted.toMutable().also { it -= node("a") }
        assertTrue(withoutA.edges.flatMap { listOf(it.source, it.destination) }.none { it == node("a") })
    }
}
