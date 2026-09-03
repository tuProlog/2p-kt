package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.extension.GuiExtensionRegistry
import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.EffectId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.ResolutionSessionId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.model.ApplicationMetadata
import it.unibo.tuprolog.ui.gui.model.ApplicationState
import it.unibo.tuprolog.ui.gui.model.ConsoleState
import it.unibo.tuprolog.ui.gui.model.DiagnosticState
import it.unibo.tuprolog.ui.gui.model.DocumentState
import it.unibo.tuprolog.ui.gui.model.GuiState
import it.unibo.tuprolog.ui.gui.model.PageContent
import it.unibo.tuprolog.ui.gui.model.PageFeatureState
import it.unibo.tuprolog.ui.gui.model.PageState
import it.unibo.tuprolog.ui.gui.model.PanelId
import it.unibo.tuprolog.ui.gui.model.ResolutionHistoryEntry
import it.unibo.tuprolog.ui.gui.model.ResolutionState
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import it.unibo.tuprolog.ui.gui.model.SolverSessionLifecycle
import it.unibo.tuprolog.ui.gui.model.SolverSessionState
import it.unibo.tuprolog.ui.gui.model.WorkspaceConfiguration
import it.unibo.tuprolog.ui.gui.model.WorkspaceState
import it.unibo.tuprolog.ui.gui.model.resolve
import it.unibo.tuprolog.ui.gui.solver.ResolutionCursor
import it.unibo.tuprolog.ui.gui.solver.ResolutionRequest
import it.unibo.tuprolog.ui.gui.solver.ResolutionSchedulingPolicy
import it.unibo.tuprolog.ui.gui.solver.ResolutionStep
import it.unibo.tuprolog.ui.gui.solver.SolverProfile
import it.unibo.tuprolog.ui.gui.solver.SolverProfileRegistry
import it.unibo.tuprolog.ui.gui.solver.SolverSession
import it.unibo.tuprolog.ui.gui.solver.SolverSessionCreationRequest
import it.unibo.tuprolog.ui.gui.solver.SolverSignal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Default presentation-independent application controller.
 *
 * All public state is immutable. Live solvers, cursors and jobs are held in [PageRuntime] and keyed by [PageId].
 * Every asynchronous update verifies its [ResolutionSessionId], which prevents cancelled or stale computations from
 * mutating a later resolution or another selected page.
 */
class DefaultGuiController(
    workspaceConfiguration: WorkspaceConfiguration,
    baseProfiles: Iterable<SolverProfile>,
    parentScope: CoroutineScope,
    private val extensions: GuiExtensionRegistry = GuiExtensionRegistry(),
    metadata: ApplicationMetadata = ApplicationMetadata(),
) : GuiController {
    private val mutex = Mutex()
    private val scope =
        CoroutineScope(
            parentScope.coroutineContext +
                SupervisorJob(parentScope.coroutineContext[Job]),
        )
    private val ids = SequentialIds()
    private val runtimes = mutableMapOf<PageId, PageRuntime>()
    private val closeAfterSave = mutableSetOf<PageId>()

    private val profiles = SolverProfileRegistry(baseProfiles.toList() + extensions.solverProfiles)

    private val _state =
        MutableStateFlow(
            GuiState(
                application = ApplicationState(metadata = metadata),
                workspace = WorkspaceState(configuration = workspaceConfiguration),
            ),
        )
    private val _events =
        MutableSharedFlow<GuiEvent>(
            replay = 0,
            extraBufferCapacity = 1024,
        )

    /*
     * Platform effects are commands, not telemetry: losing one can strand the state machine
     * (for example, a pending save-before-close). Keep them in an ordered, lossless channel.
     */
    private val effectChannel = Channel<GuiEffect>(capacity = Channel.UNLIMITED)

    override val state: StateFlow<GuiState> = _state.asStateFlow()
    override val events: SharedFlow<GuiEvent> = _events.asSharedFlow()
    override val effects: Flow<GuiEffect> = effectChannel.receiveAsFlow()

    init {
        profiles.require(workspaceConfiguration.defaultSolverProfileId)
    }

    override suspend fun dispatch(action: GuiAction) {
        when (action) {
            is DocumentAction.ChangeText -> changeDocumentText(action)
            is PageAction.ChangeScratchText -> changeScratchText(action)
            is PageAction.ChangeQuery -> changeQuery(action)
            is PageAction.ChangeStdin -> changeStdin(action)
            is PageAction.ChangeConfiguration -> changeConfiguration(action)
            is PageAction.ChangeSolverProfile -> changeSolverProfile(action)
            is PageAction.ChangeTimeout -> changeTimeout(action)
            is PageAction.Solve -> startResolution(action)
            is PageAction.Next -> continueResolution(action)
            is PageAction.Stop -> stopResolution(action.pageId, action)
            is PageAction.Reset -> resetPage(action)
            is PageAction.ExtensionCommand -> invokeExtension(action)
            else -> reduceSynchronous(action)
        }
    }

    override suspend fun shutdown() {
        val resources =
            mutex.withLock {
                val values = runtimes.values.toList()
                runtimes.clear()
                values
            }
        resources.forEach { runtime ->
            runtime.job?.cancel()
            runCatching { runtime.cursor?.cancel() }
            runCatching { runtime.session?.close() }
        }
        effectChannel.close()
        scope.cancel()
    }

    private suspend fun reduceSynchronous(action: GuiAction) {
        when (action) {
            ApplicationAction.Start ->
                mutex.withLock {
                    if (!_state.value.application.started) {
                        updateState { it.copy(application = it.application.copy(started = true)) }
                        event(GuiEvent.ApplicationStarted)
                    }
                }
            ApplicationAction.RequestExit -> requestExit()
            ApplicationAction.ExitConfirmed -> confirmExit()
            ApplicationAction.ExitCancelled ->
                mutex.withLock {
                    updateState { it.copy(application = it.application.copy(exitRequested = false)) }
                    event(GuiEvent.ApplicationExitCancelled)
                }
            is WorkspaceAction.NewDocumentPage -> createDocumentPage(action)
            is WorkspaceAction.NewPageForDocument -> createPageForDocument(action)
            is WorkspaceAction.NewScratchPage -> createScratchPage(action)
            is WorkspaceAction.SelectPage -> selectPage(action)
            WorkspaceAction.RequestOpenDocument ->
                mutex.withLock { emitEffect(GuiEffect.PickOpenDocument(ids.nextEffect())) }
            is WorkspaceAction.OpenDocumentLoaded -> openLoadedDocument(action)
            is WorkspaceAction.RequestClosePage -> requestClosePage(action)
            is WorkspaceAction.ClosePageDecisionProvided -> closePageDecision(action)
            is DocumentAction.Rename -> renameDocument(action)
            is DocumentAction.RequestSave -> requestSave(action)
            is DocumentAction.SaveDestinationSelected -> saveDestinationSelected(action)
            is DocumentAction.SaveCancelled -> saveCancelled(action)
            is DocumentAction.SaveSucceeded -> saveSucceeded(action)
            is DocumentAction.SaveFailed -> saveFailed(action)
            is DocumentAction.RequestReload -> requestReload(action)
            is DocumentAction.ReloadDecisionProvided -> reloadDecisionProvided(action)
            is DocumentAction.ReloadSucceeded -> reloadSucceeded(action)
            is DocumentAction.ReloadFailed ->
                mutex.withLock {
                    if (_state.value.workspace.document(action.documentId) == null) {
                        reject(action, "Unknown document: ${action.documentId}")
                    } else {
                        event(GuiEvent.ActionRejected(action, action.message))
                    }
                }
            is PageAction.MarkPanelRead -> markPanelRead(action)
            is DocumentAction.ChangeText,
            is PageAction.ChangeScratchText,
            is PageAction.ChangeQuery,
            is PageAction.ChangeStdin,
            is PageAction.ChangeConfiguration,
            is PageAction.ChangeSolverProfile,
            is PageAction.ChangeTimeout,
            is PageAction.Solve,
            is PageAction.Next,
            is PageAction.Stop,
            is PageAction.Reset,
            is PageAction.ExtensionCommand,
            -> error("Action is handled by a specialised path: $action")
        }
    }

    private suspend fun requestExit() {
        mutex.withLock {
            val dirty =
                _state.value.workspace.documents.values
                    .filter { it.isDirty }
                    .map { it.id }
            updateState { it.copy(application = it.application.copy(exitRequested = true)) }
            event(GuiEvent.ApplicationExitRequested)
            if (dirty.isEmpty()) {
                emitEffect(GuiEffect.ExitApplication(ids.nextEffect()))
            } else {
                emitEffect(
                    GuiEffect.ConfirmExitWithDirtyDocuments(
                        id = ids.nextEffect(),
                        dirtyDocuments = dirty,
                    ),
                )
            }
        }
    }

    private suspend fun confirmExit() {
        mutex.withLock {
            updateState { it.copy(application = it.application.copy(exitRequested = true)) }
            emitEffect(GuiEffect.ExitApplication(ids.nextEffect()))
        }
    }

    private suspend fun createDocumentPage(action: WorkspaceAction.NewDocumentPage) {
        mutex.withLock {
            val documentId = ids.nextDocument()
            val pageId = ids.nextPage()
            val name =
                action.suggestedName?.takeIf { it.isNotBlank() }
                    ?: "untitled-${documentId.value.substringAfterLast('-')}.pl"
            val document =
                DocumentState(
                    id = documentId,
                    displayName = name,
                    text = action.initialText,
                    revision = if (action.initialText.isEmpty()) 0 else 1,
                    persistedRevision = 0,
                )
            val page = newPage(pageId, name, PageContent.DocumentReference(documentId))
            updateWorkspace {
                it.copy(
                    documents = it.documents + (documentId to document),
                    pages = it.pages + page,
                    selectedPageId = pageId,
                )
            }
            runtimes[pageId] = PageRuntime()
            event(GuiEvent.DocumentCreated(documentId))
            event(GuiEvent.PageCreated(pageId, documentId))
            event(GuiEvent.PageSelected(pageId))
        }
    }

    private suspend fun createPageForDocument(action: WorkspaceAction.NewPageForDocument) {
        mutex.withLock {
            val document = _state.value.workspace.document(action.documentId)
            if (document == null) {
                reject(action, "Unknown document: ${action.documentId}")
                return@withLock
            }
            val pageId = ids.nextPage()
            val page =
                newPage(
                    pageId,
                    action.suggestedTitle?.takeIf { it.isNotBlank() } ?: document.displayName,
                    PageContent.DocumentReference(document.id),
                )
            updateWorkspace { it.copy(pages = it.pages + page, selectedPageId = pageId) }
            runtimes[pageId] = PageRuntime()
            event(GuiEvent.PageCreated(pageId, document.id))
            event(GuiEvent.PageSelected(pageId))
        }
    }

    private suspend fun createScratchPage(action: WorkspaceAction.NewScratchPage) {
        mutex.withLock {
            val pageId = ids.nextPage()
            val title =
                action.suggestedTitle?.takeIf { it.isNotBlank() } ?: "scratch-${pageId.value.substringAfterLast('-')}"
            val page =
                newPage(
                    pageId,
                    title,
                    PageContent.Scratch(
                        text = action.initialText,
                        revision = if (action.initialText.isEmpty()) 0 else 1,
                    ),
                )
            updateWorkspace { it.copy(pages = it.pages + page, selectedPageId = pageId) }
            runtimes[pageId] = PageRuntime()
            event(GuiEvent.PageCreated(pageId, null))
            event(GuiEvent.PageSelected(pageId))
        }
    }

    private suspend fun selectPage(action: WorkspaceAction.SelectPage) {
        mutex.withLock {
            if (_state.value.workspace.page(action.pageId) == null) {
                reject(action, "Unknown page: ${action.pageId}")
                return@withLock
            }
            updateWorkspace { it.copy(selectedPageId = action.pageId) }
            event(GuiEvent.PageSelected(action.pageId))
        }
    }

    private suspend fun openLoadedDocument(action: WorkspaceAction.OpenDocumentLoaded) {
        mutex.withLock {
            val documentId = ids.nextDocument()
            val pageId = ids.nextPage()
            val document =
                DocumentState(
                    id = documentId,
                    displayName = action.origin.displayName,
                    text = action.text,
                    origin = action.origin,
                    revision = 0,
                    persistedRevision = 0,
                )
            val page = newPage(pageId, document.displayName, PageContent.DocumentReference(documentId))
            updateWorkspace {
                it.copy(
                    documents = it.documents + (documentId to document),
                    pages = it.pages + page,
                    selectedPageId = pageId,
                )
            }
            runtimes[pageId] = PageRuntime()
            event(GuiEvent.DocumentCreated(documentId))
            event(GuiEvent.PageCreated(pageId, documentId))
            event(GuiEvent.PageSelected(pageId))
        }
    }

    private suspend fun requestClosePage(action: WorkspaceAction.RequestClosePage) {
        var shouldClose = false
        mutex.withLock {
            val page = _state.value.workspace.page(action.pageId)
            if (page == null) {
                reject(action, "Unknown page: ${action.pageId}")
                return@withLock
            }
            val documentId = (page.content as? PageContent.DocumentReference)?.documentId
            val document = documentId?.let { _state.value.workspace.document(it) }
            val otherReferences =
                if (documentId == null) {
                    0
                } else {
                    _state.value.workspace.pages.count {
                        it.id != page.id && (it.content as? PageContent.DocumentReference)?.documentId == documentId
                    }
                }
            if (document != null && document.isDirty && otherReferences == 0) {
                emitEffect(
                    GuiEffect.ConfirmCloseDirtyPage(
                        id = ids.nextEffect(),
                        pageId = page.id,
                        documentId = document.id,
                        displayName = document.displayName,
                    ),
                )
            } else {
                shouldClose = true
            }
        }
        if (shouldClose) {
            closePage(action.pageId)
        }
    }

    private suspend fun closePageDecision(action: WorkspaceAction.ClosePageDecisionProvided) {
        when (action.decision) {
            CloseDecision.CANCEL -> Unit
            CloseDecision.DISCARD -> closePage(action.pageId)
            CloseDecision.SAVE -> {
                val documentId =
                    mutex.withLock {
                        val page = _state.value.workspace.page(action.pageId)
                        if (page == null) {
                            reject(action, "Unknown page: ${action.pageId}")
                            null
                        } else {
                            (page.content as? PageContent.DocumentReference)?.documentId?.also {
                                closeAfterSave += action.pageId
                            }
                        }
                    }
                if (documentId != null) {
                    requestSave(DocumentAction.RequestSave(documentId))
                }
            }
        }
    }

    private suspend fun closePage(pageId: PageId) {
        val runtime =
            mutex.withLock {
                val workspace = _state.value.workspace
                val page = workspace.page(pageId) ?: return@withLock null
                val remainingPages = workspace.pages.filterNot { it.id == pageId }
                val documentId = (page.content as? PageContent.DocumentReference)?.documentId
                val documentStillReferenced =
                    documentId != null &&
                        remainingPages.any {
                            (it.content as? PageContent.DocumentReference)?.documentId == documentId
                        }
                val documents =
                    if (documentId != null && !documentStillReferenced) {
                        workspace.documents - documentId
                    } else {
                        workspace.documents
                    }
                val newSelection =
                    when {
                        workspace.selectedPageId != pageId -> workspace.selectedPageId
                        remainingPages.isEmpty() -> null
                        else -> remainingPages.last().id
                    }
                updateWorkspace {
                    it.copy(
                        documents = documents,
                        pages = remainingPages,
                        selectedPageId = newSelection,
                    )
                }
                closeAfterSave -= pageId
                event(GuiEvent.PageClosed(pageId))
                event(GuiEvent.PageSelected(newSelection))
                runtimes.remove(pageId)
            }
        runtime?.job?.cancel()
        runCatching { runtime?.cursor?.cancel() }
        runCatching { runtime?.session?.close() }
    }

    private suspend fun renameDocument(action: DocumentAction.Rename) {
        mutex.withLock {
            val workspace = _state.value.workspace
            if (workspace.document(action.documentId) == null) {
                reject(action, "Unknown document: ${action.documentId}")
                return@withLock
            }
            if (action.displayName.isBlank()) {
                reject(action, "Document name cannot be blank")
                return@withLock
            }
            updateWorkspace { current ->
                val renamed = current.updateDocument(action.documentId) { it.copy(displayName = action.displayName) }
                renamed.copy(
                    pages =
                        renamed.pages.map { page ->
                            if ((page.content as? PageContent.DocumentReference)?.documentId == action.documentId) {
                                page.copy(title = action.displayName)
                            } else {
                                page
                            }
                        },
                )
            }
        }
    }

    private suspend fun requestSave(action: DocumentAction.RequestSave) {
        mutex.withLock {
            val document = _state.value.workspace.document(action.documentId)
            if (document == null) {
                reject(action, "Unknown document: ${action.documentId}")
                return@withLock
            }
            if (document.origin == null || action.forceSaveAs) {
                emitEffect(
                    GuiEffect.PickSaveDestination(
                        id = ids.nextEffect(),
                        documentId = document.id,
                        suggestedName = document.displayName,
                    ),
                )
            } else {
                emitEffect(
                    GuiEffect.WriteDocument(
                        id = ids.nextEffect(),
                        documentId = document.id,
                        origin = document.origin,
                        text = document.text,
                        revision = document.revision,
                    ),
                )
            }
        }
    }

    private suspend fun saveDestinationSelected(action: DocumentAction.SaveDestinationSelected) {
        mutex.withLock {
            val document = _state.value.workspace.document(action.documentId)
            if (document == null) {
                reject(action, "Unknown document: ${action.documentId}")
                return@withLock
            }
            emitEffect(
                GuiEffect.WriteDocument(
                    id = ids.nextEffect(),
                    documentId = document.id,
                    origin = action.origin,
                    text = document.text,
                    revision = document.revision,
                ),
            )
        }
    }

    private suspend fun saveCancelled(action: DocumentAction.SaveCancelled) {
        mutex.withLock {
            if (_state.value.workspace.document(action.documentId) == null) {
                reject(action, "Unknown document: ${action.documentId}")
                return@withLock
            }
            clearPendingCloseForDocument(action.documentId)
            event(GuiEvent.DocumentSaveCancelled(action.documentId))
        }
    }

    private suspend fun saveFailed(action: DocumentAction.SaveFailed) {
        mutex.withLock {
            if (_state.value.workspace.document(action.documentId) == null) {
                reject(action, "Unknown document: ${action.documentId}")
                return@withLock
            }
            clearPendingCloseForDocument(action.documentId)
            event(GuiEvent.DocumentSaveFailed(action.documentId, action.message))
        }
    }

    private suspend fun saveSucceeded(action: DocumentAction.SaveSucceeded) {
        val pagesToClose = mutableListOf<PageId>()
        mutex.withLock {
            val document = _state.value.workspace.document(action.documentId)
            if (document == null) {
                reject(action, "Unknown document: ${action.documentId}")
                return@withLock
            }
            if (action.savedRevision > document.revision) {
                reject(action, "Saved revision ${action.savedRevision} exceeds current revision ${document.revision}")
                return@withLock
            }
            if (action.savedRevision < document.persistedRevision) {
                reject(
                    action,
                    "Ignored stale save completion for revision ${action.savedRevision}; " +
                        "revision ${document.persistedRevision} is already persisted",
                )
                return@withLock
            }
            updateWorkspace { current ->
                val updated =
                    current.updateDocument(action.documentId) {
                        it.copy(
                            origin = action.origin,
                            displayName = action.origin.displayName,
                            persistedRevision = action.savedRevision,
                        )
                    }
                updated.copy(
                    pages =
                        updated.pages.map { page ->
                            if ((page.content as? PageContent.DocumentReference)?.documentId == action.documentId) {
                                page.copy(title = action.origin.displayName)
                            } else {
                                page
                            }
                        },
                )
            }
            val closeCandidates = clearPendingCloseForDocument(action.documentId)
            if (action.savedRevision == document.revision) {
                pagesToClose += closeCandidates
            } else if (closeCandidates.isNotEmpty()) {
                event(
                    GuiEvent.ActionRejected(
                        action,
                        "Document changed while it was being saved; affected pages were kept open",
                    ),
                )
            }
            event(GuiEvent.DocumentSaved(action.documentId, action.savedRevision))
        }
        pagesToClose.forEach { closePage(it) }
    }

    /** Must be called while [mutex] is held. */
    private fun clearPendingCloseForDocument(documentId: DocumentId): Set<PageId> {
        val pending =
            closeAfterSave.filterTo(linkedSetOf()) { pageId ->
                val page = _state.value.workspace.page(pageId)
                (page?.content as? PageContent.DocumentReference)?.documentId == documentId
            }
        closeAfterSave.removeAll(pending)
        return pending
    }

    private suspend fun requestReload(action: DocumentAction.RequestReload) {
        mutex.withLock {
            val document = _state.value.workspace.document(action.documentId)
            if (document == null) {
                reject(action, "Unknown document: ${action.documentId}")
                return@withLock
            }
            val origin = document.origin
            if (origin == null) {
                reject(action, "Untitled document cannot be reloaded")
                return@withLock
            }
            if (document.isDirty) {
                emitEffect(
                    GuiEffect.ConfirmReloadDirtyDocument(
                        id = ids.nextEffect(),
                        documentId = document.id,
                        displayName = document.displayName,
                    ),
                )
            } else {
                emitEffect(
                    GuiEffect.ReadDocument(
                        id = ids.nextEffect(),
                        documentId = document.id,
                        origin = origin,
                    ),
                )
            }
        }
    }

    private suspend fun reloadDecisionProvided(action: DocumentAction.ReloadDecisionProvided) {
        if (action.decision == ReloadDecision.CANCEL) {
            return
        }
        mutex.withLock {
            val document = _state.value.workspace.document(action.documentId)
            if (document == null) {
                reject(action, "Unknown document: ${action.documentId}")
                return@withLock
            }
            val origin = document.origin
            if (origin == null) {
                reject(action, "Untitled document cannot be reloaded")
                return@withLock
            }
            emitEffect(
                GuiEffect.ReadDocument(
                    id = ids.nextEffect(),
                    documentId = document.id,
                    origin = origin,
                ),
            )
        }
    }

    private suspend fun reloadSucceeded(action: DocumentAction.ReloadSucceeded) {
        val cancellations = mutableListOf<RuntimeCancellation>()
        val sessionsToClose = mutableListOf<SolverSession>()
        mutex.withLock {
            val workspace = _state.value.workspace
            val document = workspace.document(action.documentId)
            if (document == null) {
                reject(action, "Unknown document: ${action.documentId}")
                return@withLock
            }
            val nextRevision = document.revision + 1
            var updated =
                workspace.updateDocument(action.documentId) {
                    it.copy(
                        text = action.text,
                        revision = nextRevision,
                        persistedRevision = nextRevision,
                    )
                }
            updated =
                updated.copy(
                    pages =
                        updated.pages.map { page ->
                            if ((page.content as? PageContent.DocumentReference)?.documentId == action.documentId) {
                                cancellations += detachActiveRuntime(page.id)
                                detachSession(page.id)?.let(sessionsToClose::add)
                                invalidatePage(page)
                            } else {
                                page
                            }
                        },
                )
            updateWorkspace { updated }
            event(GuiEvent.DocumentChanged(action.documentId, nextRevision))
        }
        cancelResources(cancellations)
        sessionsToClose.forEach { session -> runCatching { session.close() } }
    }

    private suspend fun changeDocumentText(action: DocumentAction.ChangeText) {
        val cancellations = mutableListOf<RuntimeCancellation>()
        val sessionsToClose = mutableListOf<SolverSession>()
        mutex.withLock {
            val workspace = _state.value.workspace
            val document = workspace.document(action.documentId)
            if (document == null) {
                reject(action, "Unknown document: ${action.documentId}")
                return@withLock
            }
            if (document.text == action.text) {
                return@withLock
            }
            val nextRevision = document.revision + 1
            var updated =
                workspace.updateDocument(action.documentId) {
                    it.copy(text = action.text, revision = nextRevision)
                }
            updated =
                updated.copy(
                    pages =
                        updated.pages.map { page ->
                            if ((page.content as? PageContent.DocumentReference)?.documentId == action.documentId) {
                                cancellations += detachActiveRuntime(page.id)
                                detachSession(page.id)?.let(sessionsToClose::add)
                                invalidatePage(page)
                            } else {
                                page
                            }
                        },
                )
            updateWorkspace { updated }
            event(GuiEvent.DocumentChanged(action.documentId, nextRevision))
        }
        cancelResources(cancellations)
        sessionsToClose.forEach { session -> runCatching { session.close() } }
    }

    private suspend fun changeScratchText(action: PageAction.ChangeScratchText) {
        var cancellation: RuntimeCancellation? = null
        var detachedSession: SolverSession? = null
        mutex.withLock {
            val page = _state.value.workspace.page(action.pageId)
            if (page == null) {
                reject(action, "Unknown page: ${action.pageId}")
                return@withLock
            }
            val scratch = page.content as? PageContent.Scratch
            if (scratch == null) {
                reject(action, "Page ${action.pageId} is not a scratch page")
                return@withLock
            }
            if (scratch.text == action.text) {
                return@withLock
            }
            cancellation = detachActiveRuntime(page.id)
            detachedSession = detachSession(page.id)
            updateWorkspace {
                it.updatePage(page.id) { current ->
                    invalidatePage(
                        current.copy(
                            content =
                                PageContent.Scratch(
                                    text = action.text,
                                    revision = scratch.revision + 1,
                                ),
                        ),
                    )
                }
            }
            event(GuiEvent.SolverSessionInvalidated(page.id))
        }
        cancellation?.let { cancelResources(listOf(it)) }
        runCatching { detachedSession?.close() }
    }

    private suspend fun changeQuery(action: PageAction.ChangeQuery) {
        var cancellation: RuntimeCancellation? = null
        mutex.withLock {
            val page = _state.value.workspace.page(action.pageId)
            if (page == null) {
                reject(action, "Unknown page: ${action.pageId}")
                return@withLock
            }
            if (page.resolution.status == ResolutionStatus.RUNNING) {
                reject(action, "Cannot edit the query while a resolution step is running")
                return@withLock
            }
            if (page.query.text == action.query) {
                return@withLock
            }
            if (page.resolution.canStop) {
                cancellation = detachActiveRuntime(page.id)
                event(GuiEvent.ResolutionCancelled(page.id, page.resolution.id))
            }
            updateWorkspace {
                it.updatePage(page.id) { current ->
                    val terminated =
                        if (current.resolution.canStop) {
                            terminalResolution(current, ResolutionStatus.CANCELLED)
                        } else {
                            current
                        }
                    terminated.copy(
                        query = terminated.query.copy(text = action.query),
                        resolution = ResolutionState(),
                    )
                }
            }
        }
        cancellation?.let { cancelResources(listOf(it)) }
    }

    private suspend fun changeStdin(action: PageAction.ChangeStdin) {
        changePageAndInvalidate(action, "stdin") { page ->
            page.copy(console = page.console.copy(stdin = action.stdin))
        }
    }

    private suspend fun changeSolverProfile(action: PageAction.ChangeSolverProfile) {
        if (profiles.find(action.profileId) == null) {
            mutex.withLock { reject(action, "Unknown solver profile: ${action.profileId}") }
            return
        }
        val page = mutex.withLock { _state.value.workspace.page(action.pageId) }
        if (page == null) {
            mutex.withLock { reject(action, "Unknown page: ${action.pageId}") }
            return
        }
        changeConfiguration(
            PageAction.ChangeConfiguration(
                action.pageId,
                page.configuration.copy(solverProfileOverride = action.profileId),
            ),
        )
    }

    private suspend fun changeTimeout(action: PageAction.ChangeTimeout) {
        if (!action.timeout.isPositive()) {
            mutex.withLock { reject(action, "Timeout must be positive") }
            return
        }
        val page = mutex.withLock { _state.value.workspace.page(action.pageId) }
        if (page == null) {
            mutex.withLock { reject(action, "Unknown page: ${action.pageId}") }
            return
        }
        changeConfiguration(
            PageAction.ChangeConfiguration(
                action.pageId,
                page.configuration.copy(timeoutOverride = action.timeout),
            ),
        )
    }

    private suspend fun changeConfiguration(action: PageAction.ChangeConfiguration) {
        val profileId =
            action.configuration.solverProfileOverride
                ?: _state.value.workspace.configuration.defaultSolverProfileId
        if (profiles.find(profileId) == null) {
            mutex.withLock { reject(action, "Unknown solver profile: $profileId") }
            return
        }
        if (action.configuration.timeoutOverride?.isPositive() == false) {
            mutex.withLock { reject(action, "Timeout must be positive") }
            return
        }
        changePageAndInvalidate(action, "configuration") { page ->
            page.copy(configuration = action.configuration)
        }
    }

    private suspend fun changePageAndInvalidate(
        action: PageAction,
        fieldName: String,
        transform: (PageState) -> PageState,
    ) {
        var cancellation: RuntimeCancellation? = null
        var detachedSession: SolverSession? = null
        mutex.withLock {
            val page = _state.value.workspace.page(action.pageId)
            if (page == null) {
                reject(action, "Unknown page: ${action.pageId}")
                return@withLock
            }
            if (page.resolution.status == ResolutionStatus.RUNNING) {
                reject(action, "Cannot change $fieldName while a resolution step is running")
                return@withLock
            }
            cancellation = detachActiveRuntime(page.id)
            detachedSession = detachSession(page.id)
            updateWorkspace {
                it.updatePage(page.id) { current -> invalidatePage(transform(current)) }
            }
            event(GuiEvent.SolverSessionInvalidated(page.id))
        }
        cancellation?.let { cancelResources(listOf(it)) }
        runCatching { detachedSession?.close() }
    }

    private suspend fun startResolution(action: PageAction.Solve) {
        val jobsToCancel = mutableListOf<RuntimeCancellation>()
        val resolutionId: ResolutionSessionId
        mutex.withLock {
            val workspace = _state.value.workspace
            val page = workspace.page(action.pageId)
            if (page == null) {
                reject(action, "Unknown page: ${action.pageId}")
                return
            }
            val query = page.query.text.trim()
            if (query.isEmpty()) {
                reject(action, "Query cannot be blank")
                return
            }
            if (page.resolution.status == ResolutionStatus.RUNNING ||
                page.resolution.status == ResolutionStatus.AWAITING_CONTINUATION
            ) {
                reject(action, "Page already owns an active resolution")
                return
            }
            if (workspace.configuration.schedulingPolicy == ResolutionSchedulingPolicy.SINGLE_ACTIVE_RESOLUTION) {
                for (other in workspace.pages) {
                    if (other.id != page.id && other.resolution.canStop) {
                        jobsToCancel += detachActiveRuntime(other.id)
                        updateWorkspace { current ->
                            current.updatePage(other.id) { cancelResolutionState(it) }
                        }
                        event(GuiEvent.ResolutionCancelled(other.id, other.resolution.id))
                    }
                }
            }
            resolutionId = ids.nextResolution()
            updateWorkspace {
                it.updatePage(page.id) { current ->
                    current.copy(
                        query = current.query.copy(history = current.query.history.record(query)),
                        resolution =
                            ResolutionState(
                                status = ResolutionStatus.RUNNING,
                                id = resolutionId,
                                query = query,
                            ),
                        diagnostics = current.diagnostics.replace(emptyList()),
                    )
                }
            }
            event(GuiEvent.ResolutionStarted(page.id, resolutionId, query))
        }
        cancelResources(jobsToCancel)
        launchResolutionStep(action.pageId, resolutionId, action.mode, firstStep = true)
    }

    private suspend fun continueResolution(action: PageAction.Next) {
        val resolutionId =
            mutex.withLock {
                val page = _state.value.workspace.page(action.pageId)
                when {
                    page == null -> {
                        reject(action, "Unknown page: ${action.pageId}")
                        null
                    }
                    page.resolution.status != ResolutionStatus.AWAITING_CONTINUATION -> {
                        reject(action, "Page is not awaiting continuation")
                        null
                    }
                    runtimes[action.pageId]?.cursor == null -> {
                        reject(action, "Resolution cursor is missing")
                        null
                    }
                    else -> {
                        updateWorkspace {
                            it.updatePage(action.pageId) { current ->
                                current.copy(resolution = current.resolution.copy(status = ResolutionStatus.RUNNING))
                            }
                        }
                        page.resolution.id
                    }
                }
            }
        if (resolutionId != null) {
            launchResolutionStep(action.pageId, resolutionId, action.mode, firstStep = false)
        }
    }

    private suspend fun launchResolutionStep(
        pageId: PageId,
        resolutionId: ResolutionSessionId,
        mode: ConsumptionMode,
        firstStep: Boolean,
    ) {
        val job =
            scope.launch(start = CoroutineStart.LAZY) {
                executeResolution(pageId, resolutionId, mode, firstStep)
            }
        val accepted =
            mutex.withLock {
                val page = _state.value.workspace.page(pageId)
                if (page?.resolution?.id != resolutionId || page.resolution.status != ResolutionStatus.RUNNING) {
                    false
                } else {
                    runtimes.getOrPut(pageId) { PageRuntime() }.job = job
                    true
                }
            }
        if (accepted) {
            job.start()
        } else {
            job.cancel()
        }
    }

    private suspend fun executeResolution(
        pageId: PageId,
        resolutionId: ResolutionSessionId,
        mode: ConsumptionMode,
        firstStep: Boolean,
    ) {
        val ownJob = currentCoroutineContext()[Job]
        try {
            val cursor =
                if (firstStep) {
                    val prepared = ensureSession(pageId, resolutionId) ?: return
                    val opened = prepared.session.openResolution(prepared.request)
                    val accepted =
                        mutex.withLock {
                            val page = _state.value.workspace.page(pageId)
                            if (page?.resolution?.id != resolutionId ||
                                page.resolution.status != ResolutionStatus.RUNNING
                            ) {
                                false
                            } else {
                                runtimes.getOrPut(pageId) { PageRuntime() }.cursor = opened
                                true
                            }
                        }
                    if (!accepted) {
                        runCatching { opened.cancel() }
                        return
                    }
                    opened
                } else {
                    mutex.withLock { runtimes[pageId]?.cursor } ?: return
                }

            var continueConsuming = true
            while (continueConsuming && currentCoroutineContext().isActive) {
                val step = cursor.next()
                continueConsuming = applyResolutionStep(pageId, resolutionId, step, mode)
            }
        } catch (_: CancellationException) {
            // Cancellation state is committed by the action that invalidated/stopped the runtime.
        } catch (throwable: Throwable) {
            failResolution(
                pageId,
                resolutionId,
                throwable.message ?: throwable::class.simpleName ?: "Resolution failed",
            )
        } finally {
            mutex.withLock {
                val runtime = runtimes[pageId]
                if (runtime != null && runtime.job === ownJob) {
                    runtime.job = null
                }
            }
        }
    }

    private suspend fun ensureSession(
        pageId: PageId,
        resolutionId: ResolutionSessionId,
    ): PreparedResolution? {
        val plan =
            mutex.withLock {
                val workspace = _state.value.workspace
                val page = workspace.page(pageId) ?: return@withLock null
                if (page.resolution.id != resolutionId || page.resolution.status != ResolutionStatus.RUNNING) {
                    return@withLock null
                }
                val source = sourceOf(page, workspace)
                val resolved = workspace.configuration.resolve(page.configuration)
                val profile = profiles.require(resolved.solverProfileId)
                val effective = resolved.copy(options = profile.defaultOptions + resolved.options)
                val key =
                    SessionKey(
                        documentRevision = source.revision,
                        profileId = profile.id,
                        options = effective.options,
                        stdin = page.console.stdin,
                    )
                val runtime = runtimes.getOrPut(page.id) { PageRuntime() }
                val existing = runtime.session
                if (existing != null && runtime.sessionKey == key) {
                    return@withLock SessionPlan.Existing(
                        PreparedResolution(
                            session = existing,
                            request =
                                ResolutionRequest(
                                    pageId = page.id,
                                    query = page.resolution.query.orEmpty(),
                                    timeout = effective.timeout,
                                    options = effective.options,
                                    stdin = page.console.stdin,
                                ),
                        ),
                    )
                }
                val oldSession = runtime.session
                runtime.session = null
                runtime.sessionKey = null
                updateWorkspace {
                    it.updatePage(page.id) { current ->
                        current.copy(
                            solverSession =
                                current.solverSession.copy(
                                    lifecycle = SolverSessionLifecycle.BUILDING,
                                    profileId = profile.id,
                                    loadedDocumentRevision = null,
                                    error = null,
                                ),
                        )
                    }
                }
                event(GuiEvent.SolverSessionBuilding(page.id, profile.id))
                SessionPlan.Create(
                    pageId = page.id,
                    documentId = source.documentId,
                    sourceText = source.text,
                    sourceRevision = source.revision,
                    profile = profile,
                    key = key,
                    options = effective.options,
                    stdin = page.console.stdin,
                    query = page.resolution.query.orEmpty(),
                    timeout = effective.timeout,
                    oldSession = oldSession,
                )
            } ?: return null

        return when (plan) {
            is SessionPlan.Existing -> plan.prepared
            is SessionPlan.Create -> {
                runCatching { plan.oldSession?.close() }
                val created =
                    try {
                        plan.profile.factory.create(
                            SolverSessionCreationRequest(
                                pageId = plan.pageId,
                                documentId = plan.documentId,
                                sourceText = plan.sourceText,
                                documentRevision = plan.sourceRevision,
                                profileId = plan.profile.id,
                                options = plan.options,
                                stdin = plan.stdin,
                            ),
                        )
                    } catch (cancellation: CancellationException) {
                        throw cancellation
                    } catch (throwable: Throwable) {
                        failSessionAndResolution(
                            plan.pageId,
                            resolutionId,
                            throwable.message ?: throwable::class.simpleName ?: "Cannot create solver session",
                        )
                        return null
                    }
                val accepted =
                    mutex.withLock {
                        val workspace = _state.value.workspace
                        val page = workspace.page(plan.pageId)
                        if (page?.resolution?.id != resolutionId ||
                            page.resolution.status != ResolutionStatus.RUNNING
                        ) {
                            false
                        } else {
                            val currentSource = sourceOf(page, workspace)
                            val currentResolved = workspace.configuration.resolve(page.configuration)
                            val currentProfile = profiles.require(currentResolved.solverProfileId)
                            val currentEffective =
                                currentResolved.copy(options = currentProfile.defaultOptions + currentResolved.options)
                            val currentKey =
                                SessionKey(
                                    documentRevision = currentSource.revision,
                                    profileId = currentEffective.solverProfileId,
                                    options = currentEffective.options,
                                    stdin = page.console.stdin,
                                )
                            if (currentKey != plan.key) {
                                false
                            } else {
                                val runtime = runtimes.getOrPut(page.id) { PageRuntime() }
                                runtime.session = created
                                runtime.sessionKey = plan.key
                                updateWorkspace {
                                    it.updatePage(page.id) { current ->
                                        current.copy(
                                            solverSession =
                                                SolverSessionState(
                                                    lifecycle = SolverSessionLifecycle.FRESH,
                                                    sessionId = created.id,
                                                    profileId = plan.profile.id,
                                                    loadedDocumentRevision = plan.sourceRevision,
                                                    capabilities = created.capabilities,
                                                    inspection = created.snapshot,
                                                ),
                                        )
                                    }
                                }
                                event(GuiEvent.SolverSessionReady(page.id, created.id))
                                true
                            }
                        }
                    }
                if (!accepted) {
                    runCatching { created.close() }
                    null
                } else {
                    PreparedResolution(
                        session = created,
                        request =
                            ResolutionRequest(
                                pageId = plan.pageId,
                                query = plan.query,
                                timeout = plan.timeout,
                                options = plan.options,
                                stdin = plan.stdin,
                            ),
                    )
                }
            }
        }
    }

    private suspend fun applyResolutionStep(
        pageId: PageId,
        resolutionId: ResolutionSessionId,
        step: ResolutionStep,
        mode: ConsumptionMode,
    ): Boolean =
        mutex.withLock {
            val workspace = _state.value.workspace
            val page = workspace.page(pageId) ?: return@withLock false
            if (page.resolution.id != resolutionId || page.resolution.status != ResolutionStatus.RUNNING) {
                return@withLock false
            }
            var updated = applySignals(page, step.signals)
            when (step) {
                is ResolutionStep.Yield -> {
                    val nextSolutions = updated.resolution.solutions + step.solution
                    val nextFeatures = updated.features.toMutableMap()
                    for ((featureId, replacement) in step.featureStateReplacements) {
                        val previous = nextFeatures[featureId] ?: PageFeatureState()
                        nextFeatures[featureId] =
                            previous.copy(
                                values = replacement,
                                revision = previous.revision + 1,
                            )
                    }
                    updated =
                        updated.copy(
                            resolution =
                                updated.resolution.copy(
                                    solutions = nextSolutions,
                                    revision = updated.resolution.revision + 1,
                                ),
                            features = nextFeatures,
                        )
                    event(GuiEvent.SolutionProduced(pageId, resolutionId, step.solution))
                    when {
                        !step.hasMorePotentially -> {
                            updated = completeResolution(updated, ResolutionStatus.COMPLETED)
                            runtimes[pageId]?.cursor = null
                            event(GuiEvent.ResolutionCompleted(pageId, resolutionId))
                            updateWorkspace { it.updatePage(pageId) { updated } }
                            false
                        }
                        mode == ConsumptionMode.ONE -> {
                            updated =
                                updated.copy(
                                    resolution =
                                        updated.resolution.copy(
                                            status = ResolutionStatus.AWAITING_CONTINUATION,
                                        ),
                                )
                            event(GuiEvent.ResolutionAwaitingContinuation(pageId, resolutionId))
                            updateWorkspace { it.updatePage(pageId) { updated } }
                            false
                        }
                        else -> {
                            updateWorkspace { it.updatePage(pageId) { updated } }
                            true
                        }
                    }
                }
                is ResolutionStep.End -> {
                    updated = completeResolution(updated, ResolutionStatus.COMPLETED)
                    runtimes[pageId]?.cursor = null
                    updateWorkspace { it.updatePage(pageId) { updated } }
                    event(GuiEvent.ResolutionCompleted(pageId, resolutionId))
                    false
                }
                is ResolutionStep.Failed -> {
                    updated =
                        completeResolution(
                            updated.copy(
                                resolution =
                                    updated.resolution.copy(
                                        error = step.message,
                                        revision = updated.resolution.revision + 1,
                                    ),
                            ),
                            ResolutionStatus.FAILED,
                        )
                    runtimes[pageId]?.cursor = null
                    updateWorkspace { it.updatePage(pageId) { updated } }
                    event(GuiEvent.ResolutionFailed(pageId, resolutionId, step.message))
                    false
                }
            }
        }

    private suspend fun failResolution(
        pageId: PageId,
        resolutionId: ResolutionSessionId,
        message: String,
    ) {
        mutex.withLock {
            val page = _state.value.workspace.page(pageId) ?: return@withLock
            if (page.resolution.id != resolutionId || page.resolution.status != ResolutionStatus.RUNNING) {
                return@withLock
            }
            val failed =
                completeResolution(
                    page.copy(
                        resolution =
                            page.resolution.copy(
                                error = message,
                                revision = page.resolution.revision + 1,
                            ),
                    ),
                    ResolutionStatus.FAILED,
                )
            updateWorkspace { it.updatePage(pageId) { failed } }
            runtimes[pageId]?.cursor = null
            event(GuiEvent.ResolutionFailed(pageId, resolutionId, message))
        }
    }

    private suspend fun failSessionAndResolution(
        pageId: PageId,
        resolutionId: ResolutionSessionId,
        message: String,
    ) {
        mutex.withLock {
            val page = _state.value.workspace.page(pageId) ?: return@withLock
            if (page.resolution.id != resolutionId || page.resolution.status != ResolutionStatus.RUNNING) {
                return@withLock
            }
            val failed =
                completeResolution(
                    page.copy(
                        solverSession =
                            page.solverSession.copy(
                                lifecycle = SolverSessionLifecycle.FAILED,
                                error = message,
                            ),
                        resolution =
                            page.resolution.copy(
                                error = message,
                                revision = page.resolution.revision + 1,
                            ),
                    ),
                    ResolutionStatus.FAILED,
                )
            updateWorkspace { it.updatePage(pageId) { failed } }
            event(GuiEvent.ResolutionFailed(pageId, resolutionId, message))
        }
    }

    private suspend fun stopResolution(
        pageId: PageId,
        action: GuiAction,
    ) {
        val cancellation =
            mutex.withLock {
                val page = _state.value.workspace.page(pageId)
                if (page == null) {
                    reject(action, "Unknown page: $pageId")
                    return@withLock null
                }
                if (!page.resolution.canStop) {
                    reject(action, "Page has no active resolution")
                    return@withLock null
                }
                val detached = detachActiveRuntime(pageId)
                updateWorkspace { it.updatePage(pageId) { current -> cancelResolutionState(current) } }
                event(GuiEvent.ResolutionCancelled(pageId, page.resolution.id))
                detached
            }
        cancellation?.let { cancelResources(listOf(it)) }
    }

    private suspend fun resetPage(action: PageAction.Reset) {
        val cancellation: RuntimeCancellation?
        val session: SolverSession?
        mutex.withLock {
            val page = _state.value.workspace.page(action.pageId)
            if (page == null) {
                reject(action, "Unknown page: ${action.pageId}")
                return
            }
            cancellation = detachActiveRuntime(page.id)
            session = detachSession(page.id)
            updateWorkspace {
                it.updatePage(page.id) { current ->
                    current.copy(
                        solverSession = SolverSessionState(),
                        resolution = ResolutionState(),
                        console = ConsoleState(stdin = current.console.stdin),
                        diagnostics = DiagnosticState(),
                    )
                }
            }
            event(GuiEvent.SolverSessionInvalidated(page.id))
        }
        cancellation?.let { cancelResources(listOf(it)) }
        runCatching { session?.close() }
    }

    private suspend fun markPanelRead(action: PageAction.MarkPanelRead) {
        mutex.withLock {
            val page = _state.value.workspace.page(action.pageId)
            if (page == null) {
                reject(action, "Unknown page: ${action.pageId}")
                return@withLock
            }
            updateWorkspace {
                it.updatePage(page.id) { current ->
                    when (action.panel) {
                        PanelId.SOLUTIONS -> current.copy(resolution = current.resolution.markRead())
                        PanelId.STDOUT ->
                            current.copy(console = current.console.copy(stdout = current.console.stdout.markRead()))
                        PanelId.STDERR ->
                            current.copy(console = current.console.copy(stderr = current.console.stderr.markRead()))
                        PanelId.WARNINGS ->
                            current.copy(console = current.console.copy(warnings = current.console.warnings.markRead()))
                        PanelId.DIAGNOSTICS ->
                            current.copy(diagnostics = current.diagnostics.markRead())
                        else -> current
                    }
                }
            }
        }
    }

    private suspend fun invokeExtension(action: PageAction.ExtensionCommand) {
        val pageSnapshot =
            mutex.withLock {
                _state.value.workspace.page(action.pageId).also { page ->
                    if (page == null) reject(action, "Unknown page: ${action.pageId}")
                }
            } ?: return
        val result = extensions.handle(action, pageSnapshot)
        if (result == null) {
            mutex.withLock { reject(action, "No handler registered for extension ${action.extensionId}") }
            return
        }
        mutex.withLock {
            val page = _state.value.workspace.page(action.pageId)
            if (page == null) {
                reject(action, "Page was closed while extension command was executing: ${action.pageId}")
                return@withLock
            }
            updateWorkspace {
                it.updatePage(page.id) { current ->
                    val merged = current.features.toMutableMap()
                    for ((featureId, update) in result.featureUpdates) {
                        val previous = merged[featureId] ?: PageFeatureState()
                        merged[featureId] =
                            previous.copy(
                                values = previous.values + update,
                                revision = previous.revision + 1,
                            )
                    }
                    current.copy(features = merged)
                }
            }
            result.events.forEach(::event)
            result.effects.forEach(::emitEffect)
        }
    }

    private fun newPage(
        id: PageId,
        title: String,
        content: PageContent,
    ): PageState =
        PageState(
            id = id,
            title = title,
            content = content,
            features = extensions.features.associate { it.id to PageFeatureState() },
        )

    private fun sourceOf(
        page: PageState,
        workspace: WorkspaceState,
    ): PageSource =
        when (val content = page.content) {
            is PageContent.DocumentReference -> {
                val document = workspace.document(content.documentId) ?: error("Missing document ${content.documentId}")
                PageSource(document.id, document.text, document.revision)
            }
            is PageContent.Scratch -> PageSource(null, content.text, content.revision)
        }

    private fun invalidatePage(page: PageState): PageState {
        val lifecycle =
            if (page.solverSession.lifecycle == SolverSessionLifecycle.ABSENT) {
                SolverSessionLifecycle.ABSENT
            } else {
                SolverSessionLifecycle.STALE
            }
        return page.copy(
            solverSession =
                page.solverSession.copy(
                    lifecycle = lifecycle,
                    loadedDocumentRevision = null,
                ),
            resolution =
                if (page.resolution.canStop) {
                    terminalResolution(page, ResolutionStatus.CANCELLED).resolution
                } else {
                    ResolutionState()
                },
        )
    }

    private fun cancelResolutionState(page: PageState): PageState =
        if (page.resolution.canStop) {
            terminalResolution(page, ResolutionStatus.CANCELLED)
        } else {
            page.copy(resolution = ResolutionState())
        }

    private fun terminalResolution(
        page: PageState,
        status: ResolutionStatus,
    ): PageState {
        val current = page.resolution
        val history =
            if (current.query == null) {
                page.history
            } else {
                page.history.record(
                    ResolutionHistoryEntry(
                        query = current.query,
                        solutions = current.solutions,
                        terminalStatus = status,
                        error = current.error,
                    ),
                )
            }
        return page.copy(
            history = history,
            // Keep the terminal resolution identity for traceability. A later Solve replaces it,
            // while stale callbacks are still rejected because terminal states are not RUNNING.
            resolution = current.copy(status = status),
        )
    }

    private fun completeResolution(
        page: PageState,
        status: ResolutionStatus,
    ): PageState = terminalResolution(page, status)

    private fun applySignals(
        page: PageState,
        signals: List<SolverSignal>,
    ): PageState {
        var current = page
        for (signal in signals) {
            current =
                when (signal) {
                    is SolverSignal.Stdout ->
                        current.copy(
                            console = current.console.copy(stdout = current.console.stdout.append(signal.text)),
                        )
                    is SolverSignal.Stderr ->
                        current.copy(
                            console = current.console.copy(stderr = current.console.stderr.append(signal.text)),
                        )
                    is SolverSignal.Warning ->
                        current.copy(
                            console = current.console.copy(warnings = current.console.warnings.append(signal.warning)),
                        )
                    is SolverSignal.Diagnostics ->
                        current.copy(diagnostics = current.diagnostics.replace(signal.diagnostics))
                    is SolverSignal.Inspection ->
                        current.copy(
                            solverSession =
                                current.solverSession.copy(
                                    inspection = signal.snapshot,
                                ),
                        )
                }
        }
        return current
    }

    private fun detachActiveRuntime(pageId: PageId): RuntimeCancellation {
        val runtime = runtimes.getOrPut(pageId) { PageRuntime() }
        val detached = RuntimeCancellation(runtime.job, runtime.cursor)
        runtime.job = null
        runtime.cursor = null
        return detached
    }

    private fun detachSession(pageId: PageId): SolverSession? {
        val runtime = runtimes.getOrPut(pageId) { PageRuntime() }
        val session = runtime.session
        runtime.session = null
        runtime.sessionKey = null
        return session
    }

    private suspend fun cancelResources(cancellations: List<RuntimeCancellation>) {
        for (cancellation in cancellations) {
            cancellation.job?.cancel()
            runCatching { cancellation.cursor?.cancel() }
        }
    }

    private fun updateState(transform: (GuiState) -> GuiState) {
        _state.value = transform(_state.value)
    }

    private fun updateWorkspace(transform: (WorkspaceState) -> WorkspaceState) {
        updateState { it.copy(workspace = transform(it.workspace)) }
    }

    private fun event(value: GuiEvent) {
        check(_events.tryEmit(value)) { "GUI event buffer overflow" }
    }

    private fun emitEffect(value: GuiEffect) {
        effectChannel.trySend(value).getOrThrow()
        event(GuiEvent.EffectRequested(value))
    }

    private fun reject(
        action: GuiAction,
        reason: String,
    ) {
        event(GuiEvent.ActionRejected(action, reason))
    }

    private data class PageSource(
        val documentId: DocumentId?,
        val text: String,
        val revision: Long,
    )

    private data class SessionKey(
        val documentRevision: Long,
        val profileId: SolverProfileId,
        val options: Map<String, String>,
        val stdin: String,
    )

    private class PageRuntime(
        var session: SolverSession? = null,
        var sessionKey: SessionKey? = null,
        var cursor: ResolutionCursor? = null,
        var job: Job? = null,
    )

    private data class RuntimeCancellation(
        val job: Job?,
        val cursor: ResolutionCursor?,
    )

    private data class PreparedResolution(
        val session: SolverSession,
        val request: ResolutionRequest,
    )

    private sealed interface SessionPlan {
        data class Existing(
            val prepared: PreparedResolution,
        ) : SessionPlan

        data class Create(
            val pageId: PageId,
            val documentId: DocumentId?,
            val sourceText: String,
            val sourceRevision: Long,
            val profile: SolverProfile,
            val key: SessionKey,
            val options: Map<String, String>,
            val stdin: String,
            val query: String,
            val timeout: kotlin.time.Duration,
            val oldSession: SolverSession?,
        ) : SessionPlan
    }

    private class SequentialIds {
        private var documents: Long = 0
        private var pages: Long = 0
        private var resolutions: Long = 0
        private var effects: Long = 0

        fun nextDocument(): DocumentId = DocumentId("document-${++documents}")

        fun nextPage(): PageId = PageId("page-${++pages}")

        fun nextResolution(): ResolutionSessionId = ResolutionSessionId("resolution-${++resolutions}")

        fun nextEffect(): EffectId = EffectId("effect-${++effects}")
    }
}
