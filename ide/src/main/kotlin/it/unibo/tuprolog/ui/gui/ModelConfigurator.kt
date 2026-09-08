package it.unibo.tuprolog.ui.gui

/**
 * A callback given a chance to configure a [TuPrologIDEModel] right after it is created, e.g. to load extra
 * libraries via [TuPrologIDEModel.customizeSolver] or to pre-populate [TuPrologIDEModel.query]. Used by
 * [TuPrologIDEBuilder.customTab] to let a [CustomTab] wire itself to the IDE's model, and by
 * [TuPrologIDEController.customizeModel].
 */
typealias ModelConfigurator = (TuPrologIDEModel) -> Unit
