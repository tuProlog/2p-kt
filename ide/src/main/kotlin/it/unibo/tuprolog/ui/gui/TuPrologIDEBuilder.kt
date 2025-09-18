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
 * NOTE ([customTabs]: If a [CustomTab.tab] has a [Tab.id] that is already existing
 * in the IDE, it substitutes the existing [Tab] instead of being added to the tab list.
 */
@Suppress("TooManyFunctions")
data class TuPrologIDEBuilder(
    val stage: Stage,
    var title: String = "tuProlog IDE",
    var icon: Image = TUPROLOG_LOGO,
    var onClose: () -> Boolean = { showExitConfirmationDialog(title) },
    var onAbout: () -> Unit = { showAboutDialog(title, Info.VERSION) },
    var stylesheets: List<String> = listOf(JAVA_KEYWORDS_LIGHT, LIGHT_CODE_AREA),
    var customLibraries: List<Library> = emptyList(),
    var customTabs: List<CustomTab> = emptyList(),
) {
    companion object {
        private const val DEFAULT_ICON_SCALE_RATIO: Double = 0.3

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

        @JvmStatic
        fun showExitConfirmationDialog(what: String): Boolean =
            showConfirmationDialog(
                "Close $what",
                "Confirmation",
                "Do you really want close the $what?",
            )

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

    fun title(title: String) = apply { this.title = title }

    fun icon(icon: Image) = apply { this.icon = icon }

    fun onClose(onClose: () -> Boolean) = apply { this.onClose = onClose }

    fun onAbout(onAbout: () -> Unit) = apply { this.onAbout = onAbout }

    fun stylesheets(stylesheets: Iterable<String>) = apply { this.stylesheets = stylesheets.toList() }

    fun stylesheet(stylesheet: String) = apply { this.stylesheets += stylesheet }

    fun customLibraries(customLibraries: Iterable<Library>) = apply { this.customLibraries = customLibraries.toList() }

    fun customLibrary(customLibrary: Library) = apply { this.customLibraries += customLibrary }

    fun customTabs(customTabs: Iterable<CustomTab>) = apply { this.customTabs = customTabs.toList() }

    fun customTab(customTab: CustomTab) = apply { this.customTabs += customTab }

    fun customTab(
        tab: Tab,
        modelConfigurator: ModelConfigurator,
    ) = customTab(CustomTab(tab, modelConfigurator))

    fun customTab(tab: Tab) =
        customTab(tab) {
            // do nothing
        }

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
