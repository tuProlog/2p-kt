package it.unibo.tuprolog.ui.gui.controller

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.ui.gui.extension.ExtensionActionHandler
import it.unibo.tuprolog.ui.gui.extension.ExtensionCommandResult
import it.unibo.tuprolog.ui.gui.extension.GuiContributions
import it.unibo.tuprolog.ui.gui.extension.GuiExtension
import it.unibo.tuprolog.ui.gui.extension.GuiExtensionRegistry
import it.unibo.tuprolog.ui.gui.identity.CommandId
import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.EffectId
import it.unibo.tuprolog.ui.gui.identity.ExtensionId
import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.model.PageConfiguration
import it.unibo.tuprolog.ui.gui.model.PageContent
import it.unibo.tuprolog.ui.gui.model.PanelId
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import it.unibo.tuprolog.ui.gui.model.WorkspaceConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

private val missingPage = PageId("missing")
private val missingDocument = DocumentId("missing")

/** Actions aimed at a page or document that doesn't exist: each must be rejected, not crash or act on another. */
private val actionsOnMissingTargets: List<Pair<String, GuiAction>> =
    listOf(
        "change-scratch-text" to PageAction.ChangeScratchText(missingPage, "x."),
        "change-query" to PageAction.ChangeQuery(missingPage, "x."),
        "change-stdin" to PageAction.ChangeStdin(missingPage, "x"),
        "change-configuration" to PageAction.ChangeConfiguration(missingPage, PageConfiguration()),
        "change-solver-profile" to PageAction.ChangeSolverProfile(missingPage, testProfileId),
        "change-timeout" to PageAction.ChangeTimeout(missingPage, 1.seconds),
        "solve" to PageAction.Solve(missingPage),
        "next" to PageAction.Next(missingPage),
        "reset" to PageAction.Reset(missingPage),
        "clear-history" to PageAction.ClearHistory(missingPage),
        "restore-history" to PageAction.RestoreHistory(missingPage, listOf("x.")),
        "mark-panel-read" to PageAction.MarkPanelRead(missingPage, PanelId.SOLUTIONS),
        "extension-command" to PageAction.ExtensionCommand(missingPage, ExtensionId("e"), CommandId("c")),
        "select-page" to WorkspaceAction.SelectPage(missingPage),
        "new-page-for-document" to WorkspaceAction.NewPageForDocument(missingDocument),
        "request-close-page" to WorkspaceAction.RequestClosePage(missingPage),
        "rename" to DocumentAction.Rename(missingDocument, "b.pl"),
        "change-text" to DocumentAction.ChangeText(missingDocument, "x."),
        "request-save" to DocumentAction.RequestSave(missingDocument),
        "request-reload" to DocumentAction.RequestReload(missingDocument),
        "reload-decision" to DocumentAction.ReloadDecisionProvided(missingDocument, ReloadDecision.DISCARD_CHANGES),
        "reload-succeeded" to DocumentAction.ReloadSucceeded(missingDocument, "x."),
        "reload-failed" to DocumentAction.ReloadFailed(missingDocument, "boom"),
    )

/** Invalid arguments on an existing untitled document page; [action] builds the action from its ids. */
private class InvalidCase(
    val id: String,
    val reason: String,
    val action: (PageId, DocumentId) -> GuiAction,
)

private val invalidArguments =
    listOf(
        InvalidCase("rename-to-blank", "Document name cannot be blank") { _, d -> DocumentAction.Rename(d, " ") },
        InvalidCase("scratch-text-on-a-document-page", "is not a scratch page") { p, _ ->
            PageAction.ChangeScratchText(p, "x.")
        },
        InvalidCase(
            "negative-timeout",
            "Timeout must not be negative",
        ) { p, _ -> PageAction.ChangeTimeout(p, (-1).seconds) },
        InvalidCase("negative-timeout-in-configuration", "Timeout must not be negative") { p, _ ->
            PageAction.ChangeConfiguration(p, PageConfiguration(timeoutOverride = (-1).seconds))
        },
        InvalidCase("unknown-profile", "Unknown solver profile") { p, _ ->
            PageAction.ChangeSolverProfile(p, SolverProfileId("nope"))
        },
        InvalidCase("unknown-profile-in-configuration", "Unknown solver profile") { p, _ ->
            PageAction.ChangeConfiguration(p, PageConfiguration(solverProfileOverride = SolverProfileId("nope")))
        },
        InvalidCase("reload-an-untitled-document", "Untitled document cannot be reloaded") { _, d ->
            DocumentAction.RequestReload(d)
        },
        InvalidCase("reload-decision-on-an-untitled-document", "Untitled document cannot be reloaded") { _, d ->
            DocumentAction.ReloadDecisionProvided(d, ReloadDecision.DISCARD_CHANGES)
        },
        InvalidCase(
            "reload-failure-is-reported",
            "disk on fire",
        ) { _, d -> DocumentAction.ReloadFailed(d, "disk on fire") },
        InvalidCase("extension-without-a-handler", "No handler registered") { p, _ ->
            PageAction.ExtensionCommand(p, ExtensionId("absent"), CommandId("c"))
        },
    )

/** A controller living in the test's own scope, with what the tests below need to drive it. */
private class Harness(
    val controller: GuiController,
    private val scope: CoroutineScope,
) {
    val selectedPage get() = controller.state.value.workspace.selectedPageId!!

    fun documentOf(page: PageId) =
        (
            controller.state.value.workspace
                .page(page)!!
                .content as PageContent.DocumentReference
        ).documentId

    /** Dispatches [action], subscribed beforehand to what it must produce: events are dropped when nobody listens. */
    suspend fun rejection(action: GuiAction): GuiEvent.ActionRejected =
        expecting(action) { controller.events.first { it is GuiEvent.ActionRejected } as GuiEvent.ActionRejected }

    suspend fun effect(action: GuiAction): GuiEffect = expecting(action) { controller.effects.first() }

    private suspend fun <T> expecting(
        action: GuiAction,
        outcome: suspend () -> T,
    ): T {
        val result = scope.async(start = CoroutineStart.UNDISPATCHED) { withTimeout(5_000) { outcome() } }
        controller.dispatch(action)
        return result.await()
    }
}

private suspend fun CoroutineScope.withController(
    extensions: GuiExtensionRegistry = GuiExtensionRegistry(),
    block: suspend Harness.() -> Unit,
) {
    val controller =
        DefaultGuiController(
            workspaceConfiguration = WorkspaceConfiguration(defaultSolverProfileId = testProfileId),
            baseProfiles = listOf(TestSolverFactory().profile),
            parentScope = this,
            extensions = extensions,
        )
    try {
        Harness(controller, this).block()
    } finally {
        controller.shutdown()
    }
}

class ControllerActionsSpec :
    FunSpec({
        withTests(nameFn = { "rejects/missing-target/${it.first}" }, actionsOnMissingTargets) { (_, action) ->
            withController {
                rejection(action).action shouldBe action
            }
        }

        withTests(nameFn = { "rejects/${it.id}" }, invalidArguments) { case ->
            withController {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p."))
                val page = selectedPage
                val action = case.action(page, documentOf(page))
                val rejection = rejection(action)
                rejection.action shouldBe action
                (case.reason in rejection.reason) shouldBe true
            }
        }

        test("rename/renames-the-document-and-its-pages") {
            withController {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p."))
                val page = selectedPage
                val document = documentOf(page)
                controller.dispatch(WorkspaceAction.NewPageForDocument(document))
                val second = selectedPage
                controller.dispatch(DocumentAction.Rename(document, "b.pl"))
                val workspace = controller.state.value.workspace
                workspace.document(document)!!.displayName shouldBe "b.pl"
                listOf(workspace.page(page)!!.title, workspace.page(second)!!.title) shouldBe listOf("b.pl", "b.pl")
            }
        }

        test("scratch-text/replaces-the-text-and-bumps-the-revision-once") {
            withController {
                controller.dispatch(WorkspaceAction.NewScratchPage("notes", "a."))
                val page = selectedPage

                fun scratch() =
                    controller.state.value.workspace
                        .page(page)!!
                        .content as PageContent.Scratch
                val initial = scratch().revision
                controller.dispatch(PageAction.ChangeScratchText(page, "b."))
                controller.dispatch(PageAction.ChangeScratchText(page, "b."))
                scratch().text shouldBe "b."
                scratch().revision shouldBe initial + 1
            }
        }

        test("reset/clears-the-resolution-but-keeps-stdin") {
            withController {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p(1)."))
                val page = selectedPage
                controller.dispatch(PageAction.ChangeQuery(page, "p(X)."))
                controller.dispatch(PageAction.ChangeStdin(page, "input"))
                controller.dispatch(PageAction.Solve(page, ConsumptionMode.ALL))
                controller.awaitResolution(page, ResolutionStatus.COMPLETED)
                controller.dispatch(PageAction.Reset(page))
                val reset =
                    controller.state.value.workspace
                        .page(page)!!
                reset.resolution.status shouldBe ResolutionStatus.IDLE
                reset.resolution.solutions shouldBe emptyList()
                reset.console.stdin shouldBe "input"
            }
        }

        test("settings/timeout-and-profile-become-page-overrides") {
            withController {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p."))
                val page = selectedPage
                controller.dispatch(PageAction.ChangeTimeout(page, 3.seconds))
                controller.dispatch(PageAction.ChangeSolverProfile(page, testProfileId))
                controller.state.value.workspace
                    .page(page)!!
                    .configuration shouldBe
                    PageConfiguration(solverProfileOverride = testProfileId, timeoutOverride = 3.seconds)
            }
        }

        test("mark-panel-read/clears-unread-solutions") {
            withController {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p(1)."))
                val page = selectedPage
                controller.dispatch(PageAction.ChangeQuery(page, "p(X)."))
                controller.dispatch(PageAction.Solve(page, ConsumptionMode.ALL))
                controller.awaitResolution(page, ResolutionStatus.COMPLETED)
                controller.state.value.workspace
                    .page(page)!!
                    .resolution.hasUnreadChanges shouldBe true
                PanelId.entries.forEach { controller.dispatch(PageAction.MarkPanelRead(page, it)) }
                controller.state.value.workspace
                    .page(page)!!
                    .resolution.hasUnreadChanges shouldBe false
            }
        }

        test("signals/reach-every-console-panel-diagnostics-and-inspection") {
            withController {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p."))
                val page = selectedPage
                controller.dispatch(PageAction.ChangeQuery(page, "signals."))
                controller.dispatch(PageAction.Solve(page, ConsumptionMode.ALL))
                val done = controller.awaitResolution(page, ResolutionStatus.COMPLETED)
                done.console.stdout.text shouldBe "out\nbye\n"
                done.console.stderr.text shouldBe "err\n"
                done.console.warnings.values
                    .map { it.message } shouldBe listOf("careful")
                done.diagnostics.values
                    .map { it.message } shouldBe listOf("hmm")
                done.solverSession.inspection.flags
                    .map { it.name } shouldBe listOf("f")
            }
        }

        test("failure/a-failed-step-fails-the-resolution-with-its-message") {
            withController {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p."))
                val page = selectedPage
                controller.dispatch(PageAction.ChangeQuery(page, "failing."))
                controller.dispatch(PageAction.Solve(page))
                val failed = controller.awaitResolution(page, ResolutionStatus.FAILED)
                (failed.resolution.error!!.contains("it broke")) shouldBe true
                failed.console.stderr.text shouldBe "why\n"
            }
        }

        test("failure/an-exception-from-the-solver-fails-the-resolution") {
            withController {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p."))
                val page = selectedPage
                controller.dispatch(PageAction.ChangeQuery(page, "throwing."))
                controller.dispatch(PageAction.Solve(page))
                val failed = controller.awaitResolution(page, ResolutionStatus.FAILED)
                (failed.resolution.error!!.contains("cursor exploded")) shouldBe true
            }
        }

        test("extension-command/merges-feature-updates-and-forwards-effects") {
            val feature = FeatureId("counter")
            val handler =
                ExtensionActionHandler { context ->
                    ExtensionCommandResult(
                        featureUpdates =
                            mapOf(
                                feature to
                                    mapOf(context.commandId.value to FeatureValue.Text(context.payload.getValue("v"))),
                            ),
                        effects = listOf(GuiEffect.CopyTextToClipboard(EffectId("copy"), "copied")),
                    )
                }
            val extension =
                object : GuiExtension {
                    override val id = ExtensionId("ext")
                    override val contributions = GuiContributions(actionHandler = handler)
                }
            withController(GuiExtensionRegistry(listOf(extension))) {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p."))
                val page = selectedPage
                effect(PageAction.ExtensionCommand(page, extension.id, CommandId("a"), mapOf("v" to "1")))
                    .shouldBeInstanceOf<GuiEffect.CopyTextToClipboard>()
                    .text shouldBe "copied"
                effect(PageAction.ExtensionCommand(page, extension.id, CommandId("b"), mapOf("v" to "2")))
                val state =
                    controller.state.value.workspace
                        .page(page)!!
                        .features
                        .getValue(feature)
                state.values shouldBe mapOf("a" to FeatureValue.Text("1"), "b" to FeatureValue.Text("2"))
                state.revision shouldBe 2
            }
        }

        test("exit/with-nothing-unsaved-exits-at-once") {
            withController {
                effect(ApplicationAction.RequestExit)
                    .shouldBeInstanceOf<GuiEffect.ExitApplication>()
                controller.state.value.application.exitRequested shouldBe true
            }
        }

        test("exit/with-unsaved-documents-asks-and-can-be-cancelled-or-confirmed") {
            withController {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p."))
                val document = documentOf(selectedPage)
                effect(ApplicationAction.RequestExit)
                    .shouldBeInstanceOf<GuiEffect.ConfirmExitWithDirtyDocuments>()
                    .dirtyDocuments shouldBe listOf(document)
                controller.dispatch(ApplicationAction.ExitCancelled)
                controller.state.value.application.exitRequested shouldBe false
                effect(ApplicationAction.ExitConfirmed)
                    .shouldBeInstanceOf<GuiEffect.ExitApplication>()
            }
        }

        test("reload/of-a-clean-document-reads-it-and-replaces-the-text") {
            withController {
                val origin = DocumentOrigin("file", "/a.pl", "a.pl")
                controller.dispatch(WorkspaceAction.OpenDocumentLoaded(origin, "p(1)."))
                val page = selectedPage
                val document = documentOf(page)
                effect(DocumentAction.RequestReload(document))
                    .shouldBeInstanceOf<GuiEffect.ReadDocument>()
                    .origin shouldBe origin
                controller.dispatch(DocumentAction.ReloadSucceeded(document, "p(2)."))
                val reloaded =
                    controller.state.value.workspace
                        .document(document)!!
                reloaded.text shouldBe "p(2)."
                reloaded.isDirty shouldBe false
            }
        }

        test("reload/of-a-dirty-document-asks-first-and-honours-the-decision") {
            withController {
                controller.dispatch(
                    WorkspaceAction.OpenDocumentLoaded(DocumentOrigin("file", "/a.pl", "a.pl"), "p(1)."),
                )
                val document = documentOf(selectedPage)
                controller.dispatch(DocumentAction.ChangeText(document, "p(2)."))
                effect(DocumentAction.RequestReload(document))
                    .shouldBeInstanceOf<GuiEffect.ConfirmReloadDirtyDocument>()
                controller.dispatch(DocumentAction.ReloadDecisionProvided(document, ReloadDecision.CANCEL))
                controller.state.value.workspace
                    .document(document)!!
                    .text shouldBe "p(2)."
                effect(DocumentAction.ReloadDecisionProvided(document, ReloadDecision.DISCARD_CHANGES))
                    .shouldBeInstanceOf<GuiEffect.ReadDocument>()
            }
        }
    })
