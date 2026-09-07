package it.unibo.tuprolog.ui.gui

import javafx.scene.control.Tab

/**
 * A user-defined [tab] to be added to the tuProlog IDE's side tab pane (see [TuPrologIDEBuilder.customTab]),
 * paired with a [modelConfigurator] that is invoked once, when the IDE starts, to let the tab hook itself
 * up to the IDE's [TuPrologIDEModel] (e.g. subscribing to solver events, or loading extra libraries).
 *
 * If [tab]'s [Tab.getId] matches the id of a tab already present in the IDE, [tab] replaces it instead of
 * being appended (see [TuPrologIDEController.addTab]).
 */
data class CustomTab(
    val tab: Tab,
    val modelConfigurator: ModelConfigurator,
)
