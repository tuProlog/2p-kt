package it.unibo.tuprolog.ui.swing.plp

import javax.swing.JComponent

/**
 * Backend used by the PLP Swing BDD feature. A Graphviz-backed implementation can be injected by the executable
 * module; the default renderer remains dependency-free and presents the canonical DOT representation.
 */
fun interface SwingBddGraphRenderer {
    fun createGraphComponent(
        dot: String,
        title: String?,
    ): JComponent
}
