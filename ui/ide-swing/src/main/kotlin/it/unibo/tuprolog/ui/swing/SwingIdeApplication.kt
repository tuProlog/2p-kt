package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.application.GuiApplication
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.persistence.PersistedWorkspace
import it.unibo.tuprolog.ui.gui.persistence.restoreWorkspace
import it.unibo.tuprolog.ui.gui.template.TheoryTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.awt.GraphicsEnvironment
import java.util.concurrent.atomic.AtomicBoolean

private const val DEFAULT_FONT_SIZE = 14

/** Owns the Swing frame, state/effect subscriptions, and frontend lifecycle. */
class SwingIdeApplication(
    private val application: GuiApplication,
    scope: CoroutineScope,
    private val featureRenderers: SwingFeatureRendererRegistry = SwingFeatureRendererRegistry(),
    private val templates: List<TheoryTemplate> = emptyList(),
    private val persistence: WorkspacePersistence? = null,
) {
    private val frontendJob = SupervisorJob(scope.coroutineContext[Job])
    private val frontendScope = CoroutineScope(scope.coroutineContext + frontendJob)
    private val closed = AtomicBoolean(false)
    private lateinit var frame: SwingIdeFrame
    private val collectors = mutableListOf<Job>()
    private var uncaughtExceptionHandler: Thread.UncaughtExceptionHandler? = null
    private var previousUncaughtExceptionHandler: Thread.UncaughtExceptionHandler? = null

    /** Set once the user deletes the persisted workspace, so a subsequent normal [close] doesn't recreate it. */
    private var suppressAutosave = false

    suspend fun show(createInitialPage: Boolean = true): SwingIdeApplication {
        check(!GraphicsEnvironment.isHeadless()) { "Cannot show the Swing IDE in a headless environment" }
        application.start()
        val restored = persistence?.load()
        onEdt {
            restored?.lookAndFeel?.let { applyLookAndFeel(it) }
            frame = createFrame(restored)
            installUncaughtExceptionHandler()
            installCollectors()
            restored?.let(::applyWindowBounds)
            frame.isVisible = true
        }
        if (restored != null && restored.documents.isNotEmpty()) {
            restoreWorkspace(restored, application.controller)
        } else if (createInitialPage &&
            application.controller.state.value.workspace.pages
                .isEmpty()
        ) {
            application.controller.dispatch(WorkspaceAction.NewDocumentPage())
        }
        return this
    }

    private fun createFrame(restored: PersistedWorkspace?): SwingIdeFrame =
        SwingIdeFrame(
            application.controller,
            frontendScope,
            featureRenderers,
            templates,
            restored?.fontSize ?: DEFAULT_FONT_SIZE,
            restored?.documents?.map { it.fontSize ?: restored.fontSize } ?: emptyList(),
            onDeletePersistedState = {
                persistence?.delete()
                suppressAutosave = true
            },
        )

    private fun installUncaughtExceptionHandler() {
        uncaughtExceptionHandler =
            SwingIdeUncaughtExceptionHandler(
                frame = { if (::frame.isInitialized) frame else null },
                fallback = Thread.getDefaultUncaughtExceptionHandler(),
            ).also { handler ->
                previousUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
                Thread.setDefaultUncaughtExceptionHandler(handler)
            }
    }

    private fun installCollectors() {
        val effects =
            SwingIdeEffectHandler(
                controller = application.controller,
                scope = frontendScope,
                parent = { frame },
                onExit = ::close,
            )
        collectors +=
            frontendScope.launch {
                application.controller.state.collectLatest { state -> onEdt { frame.render(state) } }
            }
        collectors +=
            frontendScope.launch {
                application.controller.effects.collectLatest(effects::handle)
            }
    }

    private fun applyWindowBounds(restored: PersistedWorkspace) {
        val width = restored.windowWidth
        val height = restored.windowHeight
        if (width != null && height != null) {
            frame.setSize(width, height)
            val x = restored.windowX
            val y = restored.windowY
            if (x != null && y != null) {
                frame.setLocation(x, y)
            } else {
                frame.setLocationRelativeTo(null)
            }
        }
    }

    fun close() {
        if (!closed.compareAndSet(false, true)) return
        onEdt {
            if (::frame.isInitialized) {
                if (!suppressAutosave) {
                    persistence?.save(
                        capturePersistedWorkspace(
                            application.controller.state.value,
                            frame.currentFontSize,
                            frame.bounds,
                            frame.pageFontSizes,
                        ),
                    )
                }
                frame.dispose()
            }
        }
        uncaughtExceptionHandler?.let { handler ->
            if (Thread.getDefaultUncaughtExceptionHandler() === handler) {
                Thread.setDefaultUncaughtExceptionHandler(previousUncaughtExceptionHandler)
            }
        }
        collectors.forEach(Job::cancel)
        frontendScope.launch {
            try {
                application.close()
            } finally {
                frontendJob.cancel()
            }
        }
    }
}
