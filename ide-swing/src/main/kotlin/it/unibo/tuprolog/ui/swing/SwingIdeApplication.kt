package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.application.GuiApplication
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.template.TheoryTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.awt.GraphicsEnvironment
import java.util.concurrent.atomic.AtomicBoolean

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

    suspend fun show(createInitialPage: Boolean = true): SwingIdeApplication {
        check(!GraphicsEnvironment.isHeadless()) { "Cannot show the Swing IDE in a headless environment" }
        application.start()
        val restored = persistence?.load()
        onEdt {
            frame =
                SwingIdeFrame(
                    application.controller,
                    frontendScope,
                    featureRenderers,
                    templates,
                    restored?.fontSize ?: 14,
                )
            uncaughtExceptionHandler =
                SwingIdeUncaughtExceptionHandler(
                    frame = { if (::frame.isInitialized) frame else null },
                    fallback = Thread.getDefaultUncaughtExceptionHandler(),
                ).also { handler ->
                    previousUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
                    Thread.setDefaultUncaughtExceptionHandler(handler)
                }
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
            if (restored?.windowWidth != null && restored.windowHeight != null) {
                frame.setSize(restored.windowWidth, restored.windowHeight)
                if (restored.windowX != null && restored.windowY != null) {
                    frame.setLocation(restored.windowX, restored.windowY)
                } else {
                    frame.setLocationRelativeTo(null)
                }
            }
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

    fun close() {
        if (!closed.compareAndSet(false, true)) return
        onEdt {
            if (::frame.isInitialized) {
                persistence?.save(
                    capturePersistedWorkspace(application.controller.state.value, frame.currentFontSize, frame.bounds),
                )
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
