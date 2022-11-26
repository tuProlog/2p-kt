package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.solve.library.Library
import javafx.scene.control.TreeItem

class LibraryView(library: Library) : TreeItem<String>(library.alias) {
    init {
        isExpanded = false

        val functionsChild = TreeItem("Functions")
        val predicatesChild = TreeItem("Predicates")
        val operatorsChild = TreeItem("Operators")

        children.addAll(functionsChild, predicatesChild, operatorsChild)

        val rules = library.clauses.asSequence().filterIsInstance<Rule>().map { it.head.indicator }
        val primitives = library.primitives.keys.asSequence().map { it.toIndicator() }
        val predicates = (rules + primitives).distinct().map { it.toString() }.sorted()

        predicatesChild.children.addAll(predicates.map { TreeItem(it) })

        val functions = library.functions.keys.asSequence().map { it.toIndicator() }.map { it.toString() }

        functionsChild.children.addAll(functions.map { TreeItem(it) })

        val operators = library.operators.asSequence().map { "'${it.functor}', ${it.specifier} (${it.priority})" }

        operatorsChild.children.addAll(operators.map { TreeItem(it) })
    }
}
