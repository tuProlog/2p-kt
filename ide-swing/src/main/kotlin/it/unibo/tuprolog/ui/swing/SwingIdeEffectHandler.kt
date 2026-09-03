package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.controller.ApplicationAction
import it.unibo.tuprolog.ui.gui.controller.CloseDecision
import it.unibo.tuprolog.ui.gui.controller.DocumentAction
import it.unibo.tuprolog.ui.gui.controller.GuiController
import it.unibo.tuprolog.ui.gui.controller.GuiEffect
import it.unibo.tuprolog.ui.gui.controller.ReloadDecision
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Component
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

/** Executes platform effects. The shared controller never imports AWT, Swing, java.io, or java.nio. */
class SwingIdeEffectHandler(
    private val controller: GuiController,
    private val scope: CoroutineScope,
    private val parent: () -> Component?,
    private val onExit: () -> Unit,
) {
    fun handle(effect: GuiEffect) {
        when (effect) {
            is GuiEffect.PickOpenDocument -> pickOpenDocument(effect)
            is GuiEffect.PickSaveDestination -> pickSaveDestination(effect)
            is GuiEffect.WriteDocument -> writeDocument(effect)
            is GuiEffect.ReadDocument -> readDocument(effect)
            is GuiEffect.ConfirmCloseDirtyPage -> confirmClose(effect)
            is GuiEffect.ConfirmReloadDirtyDocument -> confirmReload(effect)
            is GuiEffect.ConfirmExitWithDirtyDocuments -> confirmExit(effect)
            is GuiEffect.CopyTextToClipboard -> copyToClipboard(effect.text)
            is GuiEffect.ExitApplication -> onEdt(onExit)
        }
    }

    private fun pickOpenDocument(effect: GuiEffect.PickOpenDocument) {
        onEdt {
            val chooser = fileChooser(effect.acceptedExtensions)
            if (chooser.showOpenDialog(parent()) == JFileChooser.APPROVE_OPTION) {
                val path =
                    chooser.selectedFile
                        .toPath()
                        .toAbsolutePath()
                        .normalize()
                scope.launch {
                    runCatching {
                        withContext(Dispatchers.IO) { Files.readString(path, StandardCharsets.UTF_8) }
                    }.onSuccess { text ->
                        controller.dispatch(
                            WorkspaceAction.OpenDocumentLoaded(
                                origin = path.toOrigin(),
                                text = text,
                            ),
                        )
                    }.onFailure { error ->
                        showError("Cannot open ${path.fileName}", error)
                    }
                }
            }
        }
    }

    private fun pickSaveDestination(effect: GuiEffect.PickSaveDestination) {
        onEdt {
            val chooser = fileChooser(setOf("pl", "2p", "txt"))
            chooser.selectedFile = Paths.get(effect.suggestedName).toFile()
            if (chooser.showSaveDialog(parent()) == JFileChooser.APPROVE_OPTION) {
                val path =
                    chooser.selectedFile
                        .toPath()
                        .toAbsolutePath()
                        .normalize()
                scope.dispatch {
                    controller.dispatch(DocumentAction.SaveDestinationSelected(effect.documentId, path.toOrigin()))
                }
            } else {
                scope.dispatch {
                    controller.dispatch(DocumentAction.SaveCancelled(effect.documentId))
                }
            }
        }
    }

    private fun writeDocument(effect: GuiEffect.WriteDocument) {
        scope.launch {
            val path = effect.origin.toPathOrNull()
            if (path == null) {
                controller.dispatch(
                    DocumentAction.SaveFailed(
                        effect.documentId,
                        "Unsupported Swing document origin provider: ${effect.origin.providerId}",
                    ),
                )
                return@launch
            }
            runCatching {
                withContext(Dispatchers.IO) {
                    path.parent?.let(Files::createDirectories)
                    Files.writeString(path, effect.text, StandardCharsets.UTF_8)
                }
            }.onSuccess {
                controller.dispatch(
                    DocumentAction.SaveSucceeded(
                        documentId = effect.documentId,
                        origin = effect.origin,
                        savedRevision = effect.revision,
                    ),
                )
            }.onFailure { error ->
                controller.dispatch(
                    DocumentAction.SaveFailed(effect.documentId, error.message ?: "Cannot save document"),
                )
                showError("Cannot save ${effect.origin.displayName}", error)
            }
        }
    }

    private fun readDocument(effect: GuiEffect.ReadDocument) {
        scope.launch {
            val path = effect.origin.toPathOrNull()
            if (path == null) {
                controller.dispatch(
                    DocumentAction.ReloadFailed(
                        effect.documentId,
                        "Unsupported Swing document origin provider: ${effect.origin.providerId}",
                    ),
                )
                return@launch
            }
            runCatching {
                withContext(Dispatchers.IO) { Files.readString(path, StandardCharsets.UTF_8) }
            }.onSuccess { text ->
                controller.dispatch(DocumentAction.ReloadSucceeded(effect.documentId, text))
            }.onFailure { error ->
                controller.dispatch(
                    DocumentAction.ReloadFailed(
                        effect.documentId,
                        error.message ?: "Cannot reload document",
                    ),
                )
                showError("Cannot reload ${effect.origin.displayName}", error)
            }
        }
    }

    private fun confirmClose(effect: GuiEffect.ConfirmCloseDirtyPage) {
        onEdt {
            val labels = arrayOf("Save", "Discard", "Cancel")
            val selected =
                JOptionPane.showOptionDialog(
                    parent(),
                    "Save changes to ${effect.displayName}?",
                    "Unsaved changes",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    labels,
                    labels.first(),
                )
            val decision =
                when (selected) {
                    0 -> CloseDecision.SAVE
                    1 -> CloseDecision.DISCARD
                    else -> CloseDecision.CANCEL
                }
            scope.dispatch {
                controller.dispatch(WorkspaceAction.ClosePageDecisionProvided(effect.pageId, decision))
            }
        }
    }

    private fun confirmReload(effect: GuiEffect.ConfirmReloadDirtyDocument) {
        onEdt {
            val selected =
                JOptionPane.showConfirmDialog(
                    parent(),
                    "Discard local changes and reload ${effect.displayName}?",
                    "Reload document",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                )
            val decision =
                if (selected == JOptionPane.OK_OPTION) ReloadDecision.DISCARD_CHANGES else ReloadDecision.CANCEL
            scope.dispatch {
                controller.dispatch(DocumentAction.ReloadDecisionProvided(effect.documentId, decision))
            }
        }
    }

    private fun confirmExit(effect: GuiEffect.ConfirmExitWithDirtyDocuments) {
        onEdt {
            val selected =
                JOptionPane.showConfirmDialog(
                    parent(),
                    "${effect.dirtyDocuments.size} document(s) contain unsaved changes. Discard them and exit?",
                    "Exit 2P-Kt",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                )
            scope.dispatch {
                controller.dispatch(
                    if (selected ==
                        JOptionPane.OK_OPTION
                    ) {
                        ApplicationAction.ExitConfirmed
                    } else {
                        ApplicationAction.ExitCancelled
                    },
                )
            }
        }
    }

    private fun copyToClipboard(text: String) {
        runCatching {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
        }.onFailure { showError("Cannot access the clipboard", it) }
    }

    private fun showError(
        title: String,
        error: Throwable,
    ) {
        onEdt {
            JOptionPane.showMessageDialog(
                parent(),
                error.message ?: error::class.simpleName ?: "Unknown error",
                title,
                JOptionPane.ERROR_MESSAGE,
            )
        }
    }

    private fun fileChooser(extensions: Set<String>): JFileChooser =
        JFileChooser().apply {
            currentDirectory = System.getProperty("user.home")?.let(::File)
            if (extensions.isNotEmpty()) {
                fileFilter = FileNameExtensionFilter("Prolog and text files", *extensions.sorted().toTypedArray())
            }
        }

    private fun Path.toOrigin(): DocumentOrigin =
        DocumentOrigin(
            providerId = JVM_PATH_PROVIDER,
            opaqueReference = toString(),
            displayName = fileName?.toString() ?: toString(),
        )

    private fun DocumentOrigin.toPathOrNull(): Path? =
        if (providerId == JVM_PATH_PROVIDER) Paths.get(opaqueReference) else null

    private companion object {
        const val JVM_PATH_PROVIDER: String = "jvm-path"
    }
}
