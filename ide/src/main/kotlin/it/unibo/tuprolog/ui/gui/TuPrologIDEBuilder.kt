package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.solve.library.Library
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Tab
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import java.util.Optional

/**
 * Fluent entry point for embedding the tuProlog IDE window into a host JavaFX [javafx.application.Application]:
 * build one
 * (typically as `TuPrologIDEBuilder(stage)`),
 * customize it via the `title`/`icon`/`onClose`/`onAbout`/
 * `stylesheets`/`customLibrar(y|ies)`/`customTab(s)` fluent setters, then call [show] to load the FXML view,
 * wire it to a fresh [TuPrologIDEModel] (via [TuPrologIDEController]), and display [stage].
 *
 * NOTE ([customTabs]): If a [CustomTab.tab] has a [Tab.id] that is already existing
 * in the IDE, it substitutes the existing [Tab] instead of being added to the tab list.
 *
 * Example:
 * ```kotlin
 * class MyIDE : Application() {
 *     override fun start(stage: Stage) {
 *         TuPrologIDEBuilder(stage)
 *             .title("My Prolog IDE")
 *             .customLibrary(MyLibrary)
 *             .show()
 *     }
 * }
 * ```
 */
@Suppress("TooManyFunctions")
data class TuPrologIDEBuilder(
    /** The JavaFX [Stage] the IDE window will be rendered onto by [show]. */
    val stage: Stage,
    /** The window title, and the subject used by the default [onClose]/[onAbout] dialogs. */
    var title: String = "tuProlog IDE",
    /** The window icon, and the image shown in the default "about" dialog (see [showAboutDialog]). */
    var icon: Image = TUPROLOG_LOGO,
    /** Invoked when the user requests to close the window; returning `false` cancels the close.
     * Defaults to a confirmation dialog. */
    var onClose: () -> Boolean = { showExitConfirmationDialog(title) },
    /** Invoked when the user selects the "about" menu entry. Defaults to showing an informational dialog. */
    var onAbout: () -> Unit = { showAboutDialog(title, Info.VERSION) },
    /** CSS stylesheet URLs (see `Resources`) applied to the IDE's [javafx.scene.Scene]. */
    var stylesheets: List<String> = listOf(JAVA_KEYWORDS_LIGHT, LIGHT_CODE_AREA),
    /** Extra [Library] instances loaded into the IDE's solver in addition to the built-in ones
     * (see [TuPrologIDEModel.customizeSolver]). */
    var customLibraries: List<Library> = emptyList(),
    /** Extra tabs (and their model wiring) added to the IDE's side tab pane; see [CustomTab] and the note above. */
    var customTabs: List<CustomTab> = emptyList(),
) {
    companion object {
        private const val DEFAULT_ICON_SCALE_RATIO: Double = 0.3

        /** Shows a modal confirmation [Alert] with the given [title]/[header]/[content]
         * and returns whether the user pressed OK. */
        @JvmStatic
        fun showConfirmationDialog(
            title: String,
            header: String,
            content: String,
        ): Boolean =
            Alert(Alert.AlertType.CONFIRMATION)
                .also {
                    it.title = title
                    it.headerText = header
                    it.contentText = content
                }.showAndWait()
                .map { it == ButtonType.OK }
                .get()

        /** The default [onClose] handler: asks "do you really want to close [what]?" via [showConfirmationDialog]. */
        @JvmStatic
        fun showExitConfirmationDialog(what: String): Boolean =
            showConfirmationDialog(
                "Close $what",
                "Confirmation",
                "Do you really want close the $what?",
            )

        /** The default [onAbout] handler: shows an informational [Alert] naming [what], its [version],
         * and the running JVM/JavaFX versions. */
        @JvmStatic
        fun showAboutDialog(
            what: String,
            version: String = Info.VERSION,
            image: Image = TUPROLOG_LOGO,
            width: Double = TUPROLOG_LOGO.width * DEFAULT_ICON_SCALE_RATIO,
            height: Double = TUPROLOG_LOGO.height * DEFAULT_ICON_SCALE_RATIO,
        ): Optional<ButtonType> =
            Alert(Alert.AlertType.INFORMATION)
                .also {
                    it.title = "About"
                    it.headerText = "$what v$version"
                    it.dialogPane.graphic =
                        ImageView(image).also { img ->
                            img.fitWidth = width
                            img.fitHeight = height
                        }
                    it.contentText =
                        """
                |Running on:
                |  - 2P-Kt v${Info.VERSION}
                |  - JVM v${System.getProperty("java.version")}
                |  - JavaFX v${System.getProperty("javafx.runtime.version")}
                        """.trimMargin()
                }.showAndWait()
    }

    // The fluent setters below each assign the corresponding property (see the class-level KDoc for what it
    // controls) and return `this`, so calls can be chained as in the class-level example.

    fun title(title: String) = apply { this.title = title }

    fun icon(icon: Image) = apply { this.icon = icon }

    fun onClose(onClose: () -> Boolean) = apply { this.onClose = onClose }

    fun onAbout(onAbout: () -> Unit) = apply { this.onAbout = onAbout }

    fun stylesheets(stylesheets: Iterable<String>) = apply { this.stylesheets = stylesheets.toList() }

    /** Appends [stylesheet] to [stylesheets]. */
    fun stylesheet(stylesheet: String) = apply { this.stylesheets += stylesheet }

    fun customLibraries(customLibraries: Iterable<Library>) = apply { this.customLibraries = customLibraries.toList() }

    /** Appends [customLibrary] to [customLibraries]. */
    fun customLibrary(customLibrary: Library) = apply { this.customLibraries += customLibrary }

    fun customTabs(customTabs: Iterable<CustomTab>) = apply { this.customTabs = customTabs.toList() }

    /** Appends [customTab] to [customTabs]. */
    fun customTab(customTab: CustomTab) = apply { this.customTabs += customTab }

    /** Appends a [CustomTab] pairing [tab] with [modelConfigurator] to [customTabs]. */
    fun customTab(
        tab: Tab,
        modelConfigurator: ModelConfigurator,
    ) = customTab(CustomTab(tab, modelConfigurator))

    /** Appends [tab] to [customTabs] with a no-op [ModelConfigurator]. */
    fun customTab(tab: Tab) =
        customTab(tab) {
            // do nothing
        }

    /**
     * Loads the IDE's FXML view onto [stage], installs a fresh [TuPrologIDEController] wired to it, applies
     * [title]/[icon]/[stylesheets]/[onClose]/[onAbout], adds every [customTabs] entry
     * (see [TuPrologIDEController.addTab]),
     * loads every [customLibraries] entry into the controller's [TuPrologIDEModel]
     * (see [TuPrologIDEModel.customizeSolver]),
     * runs each [CustomTab]'s [ModelConfigurator], and finally shows [stage].
     *
     * This is the terminal operation of the builder: call it once all the desired fluent setters have been applied.
     */
    fun show() {
        val loader = FXMLLoader(javaClass.getResource("TuPrologIDEView.fxml"))
        val root = loader.load<Parent>()
        stage.scene = Scene(root)
        stage.title = this.title
        stage.icons.add(this.icon)
        stage.onCloseRequest = EventHandler { e -> if (!this.onClose()) e.consume() }
        stage.scene.stylesheets.addAll(this.stylesheets)

        val controller = loader.getController() as TuPrologIDEController
        controller.setOnAbout(this.onAbout)
        controller.setOnClose { if (this.onClose()) this.stage.close() }
        customTabs.forEach {
            controller.addTab(it.tab)
        }

        controller.customizeModel { model ->
            model.customizeSolver { solver ->
                customLibraries.forEach { solver.loadLibrary(it) }
                solver
            }
            customTabs.forEach { it.modelConfigurator(model) }
        }

        this.stage.show()
    }
}
