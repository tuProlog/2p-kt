package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.application.GuiApplication
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
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
) {
    private val frontendJob = SupervisorJob(scope.coroutineContext[Job])
    private val frontendScope = CoroutineScope(scope.coroutineContext + frontendJob)
    private val closed = AtomicBoolean(false)
    private lateinit var frame: SwingIdeFrame
    private val collectors = mutableListOf<Job>()

    suspend fun show(createInitialPage: Boolean = true): SwingIdeApplication {
        check(!GraphicsEnvironment.isHeadless()) { "Cannot show the Swing IDE in a headless environment" }
        application.start()
        onEdt {
            frame = SwingIdeFrame(application.controller, frontendScope, featureRenderers)
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
            frame.isVisible = true
        }
        if (createInitialPage &&
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
            if (::frame.isInitialized) frame.dispose()
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
