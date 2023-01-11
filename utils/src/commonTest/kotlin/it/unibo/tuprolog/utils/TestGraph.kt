package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.graphs.BreadthFirst
import it.unibo.tuprolog.utils.graphs.DepthFirst
import it.unibo.tuprolog.utils.graphs.Graph
import it.unibo.tuprolog.utils.graphs.copy
import it.unibo.tuprolog.utils.graphs.edgeOf
import it.unibo.tuprolog.utils.graphs.isAcyclic
import it.unibo.tuprolog.utils.graphs.isTree
import it.unibo.tuprolog.utils.graphs.nodeOf
import it.unibo.tuprolog.utils.graphs.visitOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestGraph {
    private val unweighted = Graph.build<String, Nothing> {
        this += nodeOf("a")
        this += nodeOf("b")
        this -= nodeOf("a")
        this += edgeOf(nodeOf("a"), nodeOf("b"))
        this += edgeOf(nodeOf("a"), nodeOf("c"))
        this += edgeOf(nodeOf("b"), nodeOf("d"))
        this += edgeOf(nodeOf("b"), nodeOf("e"))
        this += edgeOf(nodeOf("e"), nodeOf("f"))
        connect(nodeOf("a"), nodeOf("f"), bidirectional = true)
    }

    private val weighted = Graph.build<String, Int> {
        this += nodeOf("a")
        this += nodeOf("b")
        this -= nodeOf("a")
        this += edgeOf(nodeOf("a"), nodeOf("b"), 1)
        this += edgeOf(nodeOf("a"), nodeOf("c"), 2)
        this += edgeOf(nodeOf("b"), nodeOf("d"), 4)
        this += edgeOf(nodeOf("b"), nodeOf("e"), 5)
        this += edgeOf(nodeOf("e"), nodeOf("f"), 6)
        connect(nodeOf("a"), nodeOf("f"), bidirectional = true, weight = 3)
    }

    @Test
    fun testCreationUnweighted() {
        assertEquals(6, unweighted.size)
        assertEquals(7, unweighted.edgesCount)

        assertEquals(unweighted.nodes, listOf("a", "b", "c", "d", "e", "f").map { nodeOf(it) }.toSet())
        assertEquals(
            unweighted.edges,
            setOf(
                edgeOf(nodeOf("a"), nodeOf("b")),
                edgeOf(nodeOf("a"), nodeOf("c")),
                edgeOf(nodeOf("b"), nodeOf("d")),
                edgeOf(nodeOf("b"), nodeOf("e")),
                edgeOf(nodeOf("e"), nodeOf("f")),
                edgeOf(nodeOf("a"), nodeOf("f")),
                edgeOf(nodeOf("f"), nodeOf("a"))
            )
        )

        assertTrue(nodeOf("a") in unweighted)
        assertTrue(nodeOf("b") in unweighted)
        assertTrue(nodeOf("c") in unweighted)
        assertTrue(nodeOf("d") in unweighted)
        assertTrue(nodeOf("e") in unweighted)
        assertTrue(nodeOf("f") in unweighted)
        assertFalse(nodeOf("g") in unweighted)

        assertTrue(edgeOf(nodeOf("a"), nodeOf("b")) in unweighted)
        assertTrue(edgeOf(nodeOf("a"), nodeOf("c")) in unweighted)
        assertTrue(edgeOf(nodeOf("b"), nodeOf("d")) in unweighted)
        assertTrue(edgeOf(nodeOf("b"), nodeOf("e")) in unweighted)
        assertTrue(edgeOf(nodeOf("e"), nodeOf("f")) in unweighted)
        assertTrue(edgeOf(nodeOf("a"), nodeOf("f")) in unweighted)
        assertTrue(edgeOf(nodeOf("f"), nodeOf("a")) in unweighted)
        assertFalse(edgeOf(nodeOf("b"), nodeOf("c")) in unweighted)
        assertFalse(edgeOf(nodeOf("d"), nodeOf("e")) in unweighted)

        assertTrue(unweighted.containsEdgeAmong(nodeOf("a"), nodeOf("b")))
        assertTrue(unweighted.containsEdgeAmong(nodeOf("a"), nodeOf("c")))
        assertTrue(unweighted.containsEdgeAmong(nodeOf("b"), nodeOf("d")))
        assertTrue(unweighted.containsEdgeAmong(nodeOf("b"), nodeOf("e")))
        assertTrue(unweighted.containsEdgeAmong(nodeOf("e"), nodeOf("f")))
        assertTrue(unweighted.containsEdgeAmong(nodeOf("a"), nodeOf("f")))
        assertTrue(unweighted.containsEdgeAmong(nodeOf("f"), nodeOf("a")))
        assertFalse(unweighted.containsEdgeAmong(nodeOf("b"), nodeOf("c")))
        assertFalse(unweighted.containsEdgeAmong(nodeOf("d"), nodeOf("e")))
    }

    @Test
    fun testCreationWeighted() {
        assertEquals(6, weighted.size)
        assertEquals(7, weighted.edgesCount)

        assertEquals(weighted.nodes, listOf("a", "b", "c", "d", "e", "f").map { nodeOf(it) }.toSet())
        assertEquals(
            weighted.edges,
            setOf(
                edgeOf(nodeOf("a"), nodeOf("b"), 1),
                edgeOf(nodeOf("a"), nodeOf("c"), 2),
                edgeOf(nodeOf("b"), nodeOf("d"), 4),
                edgeOf(nodeOf("b"), nodeOf("e"), 5),
                edgeOf(nodeOf("e"), nodeOf("f"), 6),
                edgeOf(nodeOf("a"), nodeOf("f"), 3),
                edgeOf(nodeOf("f"), nodeOf("a"), 3)
            )
        )

        assertTrue(nodeOf("a") in weighted)
        assertTrue(nodeOf("b") in weighted)
        assertTrue(nodeOf("c") in weighted)
        assertTrue(nodeOf("d") in weighted)
        assertTrue(nodeOf("e") in weighted)
        assertTrue(nodeOf("f") in weighted)
        assertFalse(nodeOf("g") in weighted)

        assertTrue(edgeOf(nodeOf("a"), nodeOf("b"), 1) in weighted)
        assertTrue(edgeOf(nodeOf("a"), nodeOf("c"), 2) in weighted)
        assertTrue(edgeOf(nodeOf("b"), nodeOf("d"), 4) in weighted)
        assertTrue(edgeOf(nodeOf("b"), nodeOf("e"), 5) in weighted)
        assertTrue(edgeOf(nodeOf("e"), nodeOf("f"), 6) in weighted)
        assertTrue(edgeOf(nodeOf("a"), nodeOf("f"), 3) in weighted)
        assertTrue(edgeOf(nodeOf("f"), nodeOf("a"), 3) in weighted)
        assertFalse(edgeOf(nodeOf("b"), nodeOf("c")) in weighted)
        assertFalse(edgeOf(nodeOf("d"), nodeOf("e")) in weighted)

        assertTrue(weighted.containsEdgeAmong(nodeOf("a"), nodeOf("b")))
        assertTrue(weighted.containsEdgeAmong(nodeOf("a"), nodeOf("c")))
        assertTrue(weighted.containsEdgeAmong(nodeOf("b"), nodeOf("d")))
        assertTrue(weighted.containsEdgeAmong(nodeOf("b"), nodeOf("e")))
        assertTrue(weighted.containsEdgeAmong(nodeOf("e"), nodeOf("f")))
        assertTrue(weighted.containsEdgeAmong(nodeOf("a"), nodeOf("f")))
        assertTrue(weighted.containsEdgeAmong(nodeOf("f"), nodeOf("a")))
        assertFalse(weighted.containsEdgeAmong(nodeOf("b"), nodeOf("c")))
        assertFalse(weighted.containsEdgeAmong(nodeOf("d"), nodeOf("e")))
    }

    @Test
    fun testLimitedPreOrderDepthFirstSearch() {
        val expected = listOf(
            visitOf(0, nodeOf("a")),
            visitOf(1, nodeOf("b")),
            visitOf(2, nodeOf("d")),
            visitOf(2, nodeOf("e")),
            visitOf(3, nodeOf("f")),
            visitOf(1, nodeOf("c")),
            visitOf(1, nodeOf("f")),
            visitOf(2, nodeOf("a")),
            visitOf(3, nodeOf("b")),
            visitOf(3, nodeOf("c")),
            visitOf(3, nodeOf("f")),
        )
        assertEquals(expected, unweighted.asSequence(DepthFirst(maxDepth = 3), nodeOf("a")).toList())
        assertEquals(expected, weighted.asSequence(DepthFirst(maxDepth = 3), nodeOf("a")).toList())
    }

    @Test
    fun testPreOrderDepthFirstSearchOnGraphWithCycles() {
        val expected = listOf(
            visitOf(0, nodeOf("a")),
            visitOf(1, nodeOf("b")),
            visitOf(2, nodeOf("d")),
            visitOf(2, nodeOf("e")),
            visitOf(3, nodeOf("f")),
            visitOf(4, nodeOf("a")),
            visitOf(5, nodeOf("b")),
            visitOf(6, nodeOf("d")),
            visitOf(6, nodeOf("e")),
            visitOf(7, nodeOf("f")),
            visitOf(8, nodeOf("a")),
            visitOf(9, nodeOf("b")),
            visitOf(9, nodeOf("c")),
            visitOf(9, nodeOf("f")),
            visitOf(5, nodeOf("c")),
            visitOf(5, nodeOf("f")),
            visitOf(6, nodeOf("a")),
            visitOf(7, nodeOf("b")),
            visitOf(8, nodeOf("d")),
            visitOf(8, nodeOf("e")),
            visitOf(9, nodeOf("f")),
            visitOf(7, nodeOf("c")),
            visitOf(7, nodeOf("f")),
            visitOf(8, nodeOf("a")),
            visitOf(9, nodeOf("b")),
            visitOf(9, nodeOf("c")),
            visitOf(9, nodeOf("f")),
            visitOf(1, nodeOf("c")),
            visitOf(1, nodeOf("f")),
            visitOf(2, nodeOf("a")),
            visitOf(3, nodeOf("b")),
            visitOf(4, nodeOf("d")),
            visitOf(4, nodeOf("e")),
            visitOf(5, nodeOf("f")),
            visitOf(6, nodeOf("a")),
            visitOf(7, nodeOf("b")),
            visitOf(8, nodeOf("d")),
            visitOf(8, nodeOf("e")),
            visitOf(9, nodeOf("f")),
            visitOf(7, nodeOf("c")),
            visitOf(7, nodeOf("f")),
            visitOf(8, nodeOf("a")),
            visitOf(9, nodeOf("b")),
            visitOf(9, nodeOf("c")),
            visitOf(9, nodeOf("f")),
            visitOf(3, nodeOf("c")),
            visitOf(3, nodeOf("f")),
            visitOf(4, nodeOf("a")),
            visitOf(5, nodeOf("b")),
            visitOf(6, nodeOf("d")),
            visitOf(6, nodeOf("e")),
            visitOf(7, nodeOf("f")),
            visitOf(8, nodeOf("a")),
            visitOf(9, nodeOf("b")),
            visitOf(9, nodeOf("c")),
            visitOf(9, nodeOf("f")),
            visitOf(5, nodeOf("c")),
            visitOf(5, nodeOf("f")),
            visitOf(6, nodeOf("a")),
            visitOf(7, nodeOf("b")),
            visitOf(8, nodeOf("d")),
            visitOf(8, nodeOf("e")),
            visitOf(9, nodeOf("f")),
            visitOf(7, nodeOf("c")),
            visitOf(7, nodeOf("f")),
            visitOf(8, nodeOf("a")),
            visitOf(9, nodeOf("b")),
            visitOf(9, nodeOf("c")),
            visitOf(9, nodeOf("f")),
        )
        assertEquals(expected, unweighted.asSequence(DepthFirst(maxDepth = 9), nodeOf("a")).toList())
        assertEquals(expected, weighted.asSequence(DepthFirst(maxDepth = 9), nodeOf("a")).toList())
    }

    @Test
    fun testLimitedPostOrderDepthFirstSearch() {
        val expected = listOf(
            visitOf(2, nodeOf("d")),
            visitOf(3, nodeOf("f")),
            visitOf(2, nodeOf("e")),
            visitOf(1, nodeOf("b")),
            visitOf(1, nodeOf("c")),
            visitOf(3, nodeOf("b")),
            visitOf(3, nodeOf("c")),
            visitOf(3, nodeOf("f")),
            visitOf(2, nodeOf("a")),
            visitOf(1, nodeOf("f")),
            visitOf(0, nodeOf("a")),
        )
        assertEquals(expected, unweighted.asSequence(DepthFirst(maxDepth = 3, postOrder = true), nodeOf("a")).toList())
        assertEquals(expected, weighted.asSequence(DepthFirst(maxDepth = 3, postOrder = true), nodeOf("a")).toList())
    }

    @Test
    fun testLimitedBreadthFirstSearch() {
        val expected = listOf(
            visitOf(0, nodeOf("a")),
            visitOf(1, nodeOf("b")),
            visitOf(1, nodeOf("c")),
            visitOf(1, nodeOf("f")),
            visitOf(2, nodeOf("d")),
            visitOf(2, nodeOf("e")),
            visitOf(2, nodeOf("a")),
            visitOf(3, nodeOf("f")),
            visitOf(3, nodeOf("b")),
            visitOf(3, nodeOf("c")),
            visitOf(3, nodeOf("f")),
        )
        assertEquals(expected, unweighted.asSequence(BreadthFirst(maxDepth = 3), nodeOf("a")).toList())
        assertEquals(expected, weighted.asSequence(BreadthFirst(maxDepth = 3), nodeOf("a")).toList())
    }

    @Test
    fun testBreadthFirstSearchOnGraphWithCycles() {
        val expected = listOf(
            visitOf(0, nodeOf("a")),
            visitOf(1, nodeOf("b")),
            visitOf(1, nodeOf("c")),
            visitOf(1, nodeOf("f")),
            visitOf(2, nodeOf("d")),
            visitOf(2, nodeOf("e")),
            visitOf(2, nodeOf("a")),
            visitOf(3, nodeOf("f")),
            visitOf(3, nodeOf("b")),
            visitOf(3, nodeOf("c")),
            visitOf(3, nodeOf("f")),
            visitOf(4, nodeOf("a")),
            visitOf(4, nodeOf("d")),
            visitOf(4, nodeOf("e")),
            visitOf(4, nodeOf("a")),
            visitOf(5, nodeOf("b")),
            visitOf(5, nodeOf("c")),
            visitOf(5, nodeOf("f")),
            visitOf(5, nodeOf("f")),
            visitOf(5, nodeOf("b")),
            visitOf(5, nodeOf("c")),
            visitOf(5, nodeOf("f")),
            visitOf(6, nodeOf("d")),
            visitOf(6, nodeOf("e")),
            visitOf(6, nodeOf("a")),
            visitOf(6, nodeOf("a")),
            visitOf(6, nodeOf("d")),
            visitOf(6, nodeOf("e")),
            visitOf(6, nodeOf("a")),
            visitOf(7, nodeOf("f")),
            visitOf(7, nodeOf("b")),
            visitOf(7, nodeOf("c")),
            visitOf(7, nodeOf("f")),
            visitOf(7, nodeOf("b")),
            visitOf(7, nodeOf("c")),
            visitOf(7, nodeOf("f")),
            visitOf(7, nodeOf("f")),
            visitOf(7, nodeOf("b")),
            visitOf(7, nodeOf("c")),
            visitOf(7, nodeOf("f")),
            visitOf(8, nodeOf("a")),
            visitOf(8, nodeOf("d")),
            visitOf(8, nodeOf("e")),
            visitOf(8, nodeOf("a")),
            visitOf(8, nodeOf("d")),
            visitOf(8, nodeOf("e")),
            visitOf(8, nodeOf("a")),
            visitOf(8, nodeOf("a")),
            visitOf(8, nodeOf("d")),
            visitOf(8, nodeOf("e")),
            visitOf(8, nodeOf("a")),
            visitOf(9, nodeOf("b")),
            visitOf(9, nodeOf("c")),
            visitOf(9, nodeOf("f")),
            visitOf(9, nodeOf("f")),
            visitOf(9, nodeOf("b")),
            visitOf(9, nodeOf("c")),
            visitOf(9, nodeOf("f")),
            visitOf(9, nodeOf("f")),
            visitOf(9, nodeOf("b")),
            visitOf(9, nodeOf("c")),
            visitOf(9, nodeOf("f")),
            visitOf(9, nodeOf("b")),
            visitOf(9, nodeOf("c")),
            visitOf(9, nodeOf("f")),
            visitOf(9, nodeOf("f")),
            visitOf(9, nodeOf("b")),
            visitOf(9, nodeOf("c")),
            visitOf(9, nodeOf("f")),
        )
        assertEquals(expected, unweighted.asSequence(BreadthFirst(maxDepth = 9), nodeOf("a")).toList())
        assertEquals(expected, weighted.asSequence(BreadthFirst(maxDepth = 9), nodeOf("a")).toList())
    }

    @Test
    fun removeNodeImmutable() {
        val withoutA = weighted - nodeOf("a")
        assertTrue(withoutA.edges.flatMap { listOf(it.source, it.destination) }.none { it == nodeOf("a") })
    }

    @Test
    fun removeNodeMutable() {
        val withoutA = unweighted.toMutable().also { it -= nodeOf("a") }
        assertTrue(withoutA.edges.flatMap { listOf(it.source, it.destination) }.none { it == nodeOf("a") })
    }

    @Test
    fun treeCheck() {
        val tree = unweighted.copy {
            this -= edgeOf(nodeOf("a"), nodeOf("f"))
            this -= edgeOf(nodeOf("f"), nodeOf("a"))
        }

        assertTrue(tree.isTree)
    }

    @Test
    fun acyclicCheck() {
        val tree = weighted.copy {
            this -= edgeOf(nodeOf("f"), nodeOf("a"))
        }

        assertTrue(tree.isAcyclic)
    }
}
