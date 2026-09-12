package it.unibo.tuprolog.ui.swing.plp

private val NODE_LINE = Regex("""^(\S+)\s*\[shape=(?:circle|record),\s*label="(.*)"]$""")
private val EDGE_LINE = Regex("""^(\S+)\s*->\s*(\S+)(\s*\[style=dashed])?$""")

/**
 * Converts a BDD's Graphviz DOT source (see `BinaryDecisionDiagram.toDotString`) into an equivalent PlantUML
 * state diagram, so it can be rendered by [PlantUmlSwingBddGraphRenderer] without a Graphviz/GraalVM dependency.
 * Only understands the fixed node/edge shapes that producer ever emits (a `label="..."` node per state, and a
 * `[style=dashed]` edge for the BDD's low/false branch vs. a plain one for its high/true branch), not arbitrary
 * DOT; unrecognized lines (e.g. the surrounding `digraph { }`) are silently skipped.
 */
internal fun dotToPlantUml(dot: String): String {
    val plantUml = StringBuilder("@startuml\n!pragma layout smetana\nhide empty description\n")
    dot.lineSequence().map(String::trim).forEach { line ->
        val node = NODE_LINE.matchEntire(line)
        val edge = EDGE_LINE.matchEntire(line)
        when {
            node != null -> {
                val (id, label) = node.destructured
                plantUml.append("state \"${label.replace("\"", "'")}\" as s${id.toStateId()}\n")
            }
            edge != null -> {
                val (from, to, dashed) = edge.destructured
                val style = if (dashed.isNotBlank()) "[dashed]" else ""
                plantUml.append("s${from.toStateId()} -$style-> s${to.toStateId()}\n")
            }
        }
    }
    return plantUml.append("@enduml").toString()
}

private fun String.toStateId(): String = replace("-", "N")
