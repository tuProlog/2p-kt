package it.unibo.tuprolog.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.utils.graphs.BreadthFirst
import it.unibo.tuprolog.utils.graphs.DepthFirst
import it.unibo.tuprolog.utils.graphs.Graph
import it.unibo.tuprolog.utils.graphs.copy
import it.unibo.tuprolog.utils.graphs.edgeOf
import it.unibo.tuprolog.utils.graphs.isAcyclic
import it.unibo.tuprolog.utils.graphs.isTree
import it.unibo.tuprolog.utils.graphs.nodeOf
import it.unibo.tuprolog.utils.graphs.visitOf

class GraphSpec :
    FunSpec({
        val unweighted =
            Graph.build<String, Nothing> {
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

        val weighted =
            Graph.build<String, Int> {
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

        test("creation-unweighted") {
            unweighted.size shouldBe 6
            unweighted.edgesCount shouldBe 7

            unweighted.nodes shouldBe listOf("a", "b", "c", "d", "e", "f").map { nodeOf(it) }.toSet()
            unweighted.edges shouldBe
                setOf(
                    edgeOf(nodeOf("a"), nodeOf("b")),
                    edgeOf(nodeOf("a"), nodeOf("c")),
                    edgeOf(nodeOf("b"), nodeOf("d")),
                    edgeOf(nodeOf("b"), nodeOf("e")),
                    edgeOf(nodeOf("e"), nodeOf("f")),
                    edgeOf(nodeOf("a"), nodeOf("f")),
                    edgeOf(nodeOf("f"), nodeOf("a")),
                )

            (nodeOf("a") in unweighted).shouldBeTrue()
            (nodeOf("b") in unweighted).shouldBeTrue()
            (nodeOf("c") in unweighted).shouldBeTrue()
            (nodeOf("d") in unweighted).shouldBeTrue()
            (nodeOf("e") in unweighted).shouldBeTrue()
            (nodeOf("f") in unweighted).shouldBeTrue()
            (nodeOf("g") in unweighted).shouldBeFalse()

            (edgeOf(nodeOf("a"), nodeOf("b")) in unweighted).shouldBeTrue()
            (edgeOf(nodeOf("a"), nodeOf("c")) in unweighted).shouldBeTrue()
            (edgeOf(nodeOf("b"), nodeOf("d")) in unweighted).shouldBeTrue()
            (edgeOf(nodeOf("b"), nodeOf("e")) in unweighted).shouldBeTrue()
            (edgeOf(nodeOf("e"), nodeOf("f")) in unweighted).shouldBeTrue()
            (edgeOf(nodeOf("a"), nodeOf("f")) in unweighted).shouldBeTrue()
            (edgeOf(nodeOf("f"), nodeOf("a")) in unweighted).shouldBeTrue()
            (edgeOf(nodeOf("b"), nodeOf("c")) in unweighted).shouldBeFalse()
            (edgeOf(nodeOf("d"), nodeOf("e")) in unweighted).shouldBeFalse()

            unweighted.containsEdgeAmong(nodeOf("a"), nodeOf("b")).shouldBeTrue()
            unweighted.containsEdgeAmong(nodeOf("a"), nodeOf("c")).shouldBeTrue()
            unweighted.containsEdgeAmong(nodeOf("b"), nodeOf("d")).shouldBeTrue()
            unweighted.containsEdgeAmong(nodeOf("b"), nodeOf("e")).shouldBeTrue()
            unweighted.containsEdgeAmong(nodeOf("e"), nodeOf("f")).shouldBeTrue()
            unweighted.containsEdgeAmong(nodeOf("a"), nodeOf("f")).shouldBeTrue()
            unweighted.containsEdgeAmong(nodeOf("f"), nodeOf("a")).shouldBeTrue()
            unweighted.containsEdgeAmong(nodeOf("b"), nodeOf("c")).shouldBeFalse()
            unweighted.containsEdgeAmong(nodeOf("d"), nodeOf("e")).shouldBeFalse()
        }

        test("creation-weighted") {
            weighted.size shouldBe 6
            weighted.edgesCount shouldBe 7

            weighted.nodes shouldBe listOf("a", "b", "c", "d", "e", "f").map { nodeOf(it) }.toSet()
            weighted.edges shouldBe
                setOf(
                    edgeOf(nodeOf("a"), nodeOf("b"), 1),
                    edgeOf(nodeOf("a"), nodeOf("c"), 2),
                    edgeOf(nodeOf("b"), nodeOf("d"), 4),
                    edgeOf(nodeOf("b"), nodeOf("e"), 5),
                    edgeOf(nodeOf("e"), nodeOf("f"), 6),
                    edgeOf(nodeOf("a"), nodeOf("f"), 3),
                    edgeOf(nodeOf("f"), nodeOf("a"), 3),
                )

            (nodeOf("a") in weighted).shouldBeTrue()
            (nodeOf("b") in weighted).shouldBeTrue()
            (nodeOf("c") in weighted).shouldBeTrue()
            (nodeOf("d") in weighted).shouldBeTrue()
            (nodeOf("e") in weighted).shouldBeTrue()
            (nodeOf("f") in weighted).shouldBeTrue()
            (nodeOf("g") in weighted).shouldBeFalse()

            (edgeOf(nodeOf("a"), nodeOf("b"), 1) in weighted).shouldBeTrue()
            (edgeOf(nodeOf("a"), nodeOf("c"), 2) in weighted).shouldBeTrue()
            (edgeOf(nodeOf("b"), nodeOf("d"), 4) in weighted).shouldBeTrue()
            (edgeOf(nodeOf("b"), nodeOf("e"), 5) in weighted).shouldBeTrue()
            (edgeOf(nodeOf("e"), nodeOf("f"), 6) in weighted).shouldBeTrue()
            (edgeOf(nodeOf("a"), nodeOf("f"), 3) in weighted).shouldBeTrue()
            (edgeOf(nodeOf("f"), nodeOf("a"), 3) in weighted).shouldBeTrue()
            (edgeOf(nodeOf("b"), nodeOf("c")) in weighted).shouldBeFalse()
            (edgeOf(nodeOf("d"), nodeOf("e")) in weighted).shouldBeFalse()

            weighted.containsEdgeAmong(nodeOf("a"), nodeOf("b")).shouldBeTrue()
            weighted.containsEdgeAmong(nodeOf("a"), nodeOf("c")).shouldBeTrue()
            weighted.containsEdgeAmong(nodeOf("b"), nodeOf("d")).shouldBeTrue()
            weighted.containsEdgeAmong(nodeOf("b"), nodeOf("e")).shouldBeTrue()
            weighted.containsEdgeAmong(nodeOf("e"), nodeOf("f")).shouldBeTrue()
            weighted.containsEdgeAmong(nodeOf("a"), nodeOf("f")).shouldBeTrue()
            weighted.containsEdgeAmong(nodeOf("f"), nodeOf("a")).shouldBeTrue()
            weighted.containsEdgeAmong(nodeOf("b"), nodeOf("c")).shouldBeFalse()
            weighted.containsEdgeAmong(nodeOf("d"), nodeOf("e")).shouldBeFalse()
        }

        test("limited-pre-order-depth-first-search") {
            val expected =
                listOf(
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
            unweighted.asSequence(DepthFirst(maxDepth = 3), nodeOf("a")).toList() shouldBe expected
            weighted.asSequence(DepthFirst(maxDepth = 3), nodeOf("a")).toList() shouldBe expected
        }

        test("pre-order-depth-first-search-on-graph-with-cycles") {
            val expected =
                listOf(
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
            unweighted.asSequence(DepthFirst(maxDepth = 9), nodeOf("a")).toList() shouldBe expected
            weighted.asSequence(DepthFirst(maxDepth = 9), nodeOf("a")).toList() shouldBe expected
        }

        test("limited-post-order-depth-first-search") {
            val expected =
                listOf(
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
            unweighted.asSequence(DepthFirst(maxDepth = 3, postOrder = true), nodeOf("a")).toList() shouldBe expected
            weighted.asSequence(DepthFirst(maxDepth = 3, postOrder = true), nodeOf("a")).toList() shouldBe expected
        }

        test("limited-breadth-first-search") {
            val expected =
                listOf(
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
            unweighted.asSequence(BreadthFirst(maxDepth = 3), nodeOf("a")).toList() shouldBe expected
            weighted.asSequence(BreadthFirst(maxDepth = 3), nodeOf("a")).toList() shouldBe expected
        }

        test("breadth-first-search-on-graph-with-cycles") {
            val expected =
                listOf(
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
            unweighted.asSequence(BreadthFirst(maxDepth = 9), nodeOf("a")).toList() shouldBe expected
            weighted.asSequence(BreadthFirst(maxDepth = 9), nodeOf("a")).toList() shouldBe expected
        }

        test("remove-node-immutable") {
            val withoutA = weighted - nodeOf("a")
            (withoutA.edges.flatMap { listOf(it.source, it.destination) }.none { it == nodeOf("a") }).shouldBeTrue()
        }

        test("remove-node-mutable") {
            val withoutA = unweighted.toMutable().also { it -= nodeOf("a") }
            (withoutA.edges.flatMap { listOf(it.source, it.destination) }.none { it == nodeOf("a") }).shouldBeTrue()
        }

        test("tree-check") {
            val tree =
                unweighted.copy {
                    this -= edgeOf(nodeOf("a"), nodeOf("f"))
                    this -= edgeOf(nodeOf("f"), nodeOf("a"))
                }

            tree.isTree.shouldBeTrue()
        }

        test("acyclic-check") {
            val tree =
                weighted.copy {
                    this -= edgeOf(nodeOf("f"), nodeOf("a"))
                }

            tree.isAcyclic.shouldBeTrue()
        }

        /** a -1-> b, a -2-> c, b -3-> c */
        fun triangle(): Graph<String, Int> =
            Graph.build {
                this += edgeOf(nodeOf("a"), nodeOf("b"), 1)
                this += edgeOf(nodeOf("a"), nodeOf("c"), 2)
                this += edgeOf(nodeOf("b"), nodeOf("c"), 3)
            }

        test("immutable-edits/leave-the-original-untouched") {
            val graph = triangle()
            val before = graph.edges
            graph - nodeOf("c")
            graph - edgeOf(nodeOf("a"), nodeOf("b"), 1)
            graph + edgeOf(nodeOf("a"), nodeOf("d"), 4)
            graph + nodeOf("a")
            graph.edges shouldBe before
        }

        test("immutable-edits/adding-an-edge-keeps-the-other-edges-of-its-source") {
            val graph = triangle() + edgeOf(nodeOf("a"), nodeOf("d"), 4)
            (graph.outgoingEdges(nodeOf("a")).map { it.destination.value }.toSet()) shouldBe setOf("b", "c", "d")
            (nodeOf("d") in graph).shouldBeTrue()
        }

        test("immutable-edits/adding-an-existing-node-keeps-its-edges") {
            (triangle() + nodeOf("a")).outdegree(nodeOf("a")) shouldBe 2
        }

        test("immutable-edits/removing-an-edge") {
            val graph = triangle() - edgeOf(nodeOf("a"), nodeOf("b"), 1)
            graph.containsEdgeAmong(nodeOf("a"), nodeOf("b")).shouldBeFalse()
            graph.edgesCount shouldBe 2
        }

        test("copies/editing-a-mutable-copy-leaves-the-original-untouched") {
            val graph = triangle()
            val before = graph.edges
            graph.toMutable().also { it += edgeOf(nodeOf("a"), nodeOf("d"), 4) }
            graph.toMutable().also { it -= nodeOf("c") }
            graph.edges shouldBe before
        }

        test("copies/editing-a-mutable-graph-leaves-its-copies-untouched") {
            val mutable = triangle().toMutable()
            val clone = mutable.clone()
            val immutable = mutable.toImmutable()
            val before = immutable.edges
            mutable += edgeOf(nodeOf("a"), nodeOf("d"), 4)
            mutable -= edgeOf(nodeOf("b"), nodeOf("c"), 3)
            clone.edges shouldBe before
            immutable.edges shouldBe before
        }

        test("queries/degrees-and-edges") {
            val graph = triangle()
            graph.indegree(nodeOf("c")) shouldBe 2
            graph.outdegree(nodeOf("a")) shouldBe 2
            graph.outdegree(nodeOf("z")) shouldBe 0
            (graph.ingoingEdges(nodeOf("c")).map { it.source.value }.toSet()) shouldBe setOf("a", "b")
            graph[nodeOf("b") to nodeOf("c")] shouldBe 3
            (edgeOf(nodeOf("a"), nodeOf("b"), 1) in graph).shouldBeTrue()
            (edgeOf(nodeOf("a"), nodeOf("b"), 9) in graph).shouldBeFalse()
            graph.size shouldBe 3
            graph.toSet() shouldBe graph.edges
        }

        test("queries/equality-and-rendering") {
            triangle() shouldBe triangle()
            triangle().hashCode() shouldBe triangle().hashCode()
            ("a -1-> b" in triangle().toString()).shouldBeTrue()
            (triangle().asIterable(DepthFirst(), nodeOf("a")).map { it.node.value }.toList()) shouldBe
                listOf("a", "b", "c", "c")
        }
    })
