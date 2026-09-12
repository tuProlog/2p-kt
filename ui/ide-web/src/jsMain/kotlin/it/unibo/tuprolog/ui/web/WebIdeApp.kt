package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.ui.gui.application.GuiApplication
import it.unibo.tuprolog.ui.gui.application.buildGuiApplication
import it.unibo.tuprolog.ui.gui.controller.ApplicationAction
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.presentation.EditorZoom
import it.unibo.tuprolog.ui.gui.prolog.solverFactoryProfile
import it.unibo.tuprolog.ui.gui.template.ClassicTheoryTemplates
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.events.Event

fun main() {
    document.title = "tuProlog Web IDE ${Info.VERSION}"
    val scope = MainScope()
    // Solver.prolog resolves ClassicSolverFactory via a runtime `require("2p-solve-classic")` by string module
    // name (see solve/src/jsMain/.../SolverExtensionsJs.kt), which only works when Kotlin/JS modules are
    // resolved by Node at runtime. Webpack bundles ide-web ahead of time instead, so that lookup fails at
    // startup; importing the factory directly (ide-web already depends on :solve-classic) sidesteps it.
    val profile =
        solverFactoryProfile(
            ClassicSolverFactory,
            SolverProfileId("prolog"),
            "Prolog",
            // OOPLib (gui-prolog's default runtime library alongside IOLib) is reflection-based and throws
            // NotImplementedError on Kotlin/JS as soon as a runtime tries to use it, which previously failed
            // every single resolution in this app regardless of whether the query needed OOP features at all.
            runtimeLibraries = listOf(IOLib),
        )
    val application =
        buildGuiApplication(scope) {
            solverProfile(profile, makeDefault = true)
        }
    val restored = WebWorkspacePersistence.load()
    val view = WebIdeView(application.controller, scope, ClassicTheoryTemplates.ALL)
    view.editorFontSizePx = restored?.fontSize ?: EditorZoom.DEFAULT_FONT_SIZE
    val effects = WebIdeEffectHandler(application.controller, scope)

    scope.launch {
        application.controller.state.collectLatest(view::render)
    }
    scope.launch {
        application.controller.effects.collectLatest(effects::handle)
    }
    scope.launch {
        application.controller.dispatch(ApplicationAction.Start)
        if (restored != null && restored.documents.isNotEmpty()) {
            restoreWorkspace(restored, application.controller)
        } else {
            application.controller.dispatch(WorkspaceAction.NewDocumentPage())
        }
    }

    // Set once the user deletes the persisted workspace, so a subsequent "beforeunload" autosave doesn't
    // recreate it - the same guard SwingIdeApplication.close() uses.
    var suppressAutosave = false
    installSettingsButtons(view) { suppressAutosave = true }
    installAutosaveOnUnload(application, view) { suppressAutosave }
}

private fun installSettingsButtons(
    view: WebIdeView,
    onDeletePersistedState: () -> Unit,
) {
    (document.getElementById("btn-restore-defaults") as HTMLButtonElement).addEventListener(
        "click",
        { _: Event -> view.editorFontSizePx = EditorZoom.DEFAULT_FONT_SIZE },
    )
    (document.getElementById("btn-delete-persisted-state") as HTMLButtonElement).addEventListener(
        "click",
        { _: Event ->
            val confirmed =
                window.confirm(
                    "Delete the saved workspace? Nothing will be restored the next time this page loads; " +
                        "documents currently open are not affected.",
                )
            if (confirmed) {
                WebWorkspacePersistence.delete()
                onDeletePersistedState()
            }
        },
    )
}

/** `localStorage.setItem` is synchronous, so a plain "beforeunload" save (unlike an async one) reliably
 * finishes before the page actually goes away - the same "save on close" moment as SwingIdeApplication. */
private fun installAutosaveOnUnload(
    application: GuiApplication,
    view: WebIdeView,
    suppressAutosave: () -> Boolean,
) {
    window.addEventListener(
        "beforeunload",
        { _: Event ->
            if (!suppressAutosave()) {
                val snapshot = capturePersistedWorkspace(application.controller.state.value, view.editorFontSizePx)
                WebWorkspacePersistence.save(snapshot)
            }
        },
    )
}
