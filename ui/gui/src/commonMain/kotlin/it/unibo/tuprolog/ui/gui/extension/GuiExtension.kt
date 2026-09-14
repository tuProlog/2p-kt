package it.unibo.tuprolog.ui.gui.extension

import it.unibo.tuprolog.ui.gui.identity.ExtensionId

/** A pluggable bundle of GUI behavior (e.g. a specific solver profile, like PLP's), registered with a
 * [GuiExtensionRegistry] and identified by [id] so its contributions can be looked up and its commands routed. */
interface GuiExtension {
    /** Uniquely identifies this extension among every other one registered in the same application. */
    val id: ExtensionId

    /** What this extension actually adds to the application. */
    val contributions: GuiContributions
}
