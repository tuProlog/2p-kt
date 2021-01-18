package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.solve.library.AliasedLibrary
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

data class PrologIdeBuilder(
    val stage: Stage,
    var title: String = "tuProlog IDE",
    var icon: Image = TUPROLOG_LOGO,
    var onClose: () -> Boolean = {
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        alert.title = "Close tuProlog IDE"
        alert.headerText = "Confirmation"
        alert.contentText = "Are you absolutely sure you wanna close the IDE?"
        alert.showAndWait().map {
            it == ButtonType.OK
        }.get()
    },
    var onAbout: () -> Unit = {
        val dialog = Alert(Alert.AlertType.INFORMATION)
        dialog.title = "About"
        dialog.headerText = "tuProlog IDE v${Info.VERSION}"
        dialog.dialogPane.graphic = ImageView(TUPROLOG_LOGO).also {
            it.fitWidth = TUPROLOG_LOGO.width * 0.3
            it.fitHeight = TUPROLOG_LOGO.height * 0.3
        }
        dialog.contentText =
            """
            |Running on:
            |  - 2P-Kt v${Info.VERSION}
            |  - JVM v${System.getProperty("java.version")}
            |  - JavaFX v${System.getProperty("javafx.runtime.version")}
            """.trimMargin()
        dialog.showAndWait()
    },
    var stylesheets: Collection<String> = listOf(JAVA_KEYWORDS_LIGHT, LIGHT_CODE_AREA),
    var customLibraries: Collection<AliasedLibrary> = emptyList(),
    var customTabs: Collection<Pair<Tab, (PrologIDEModel) -> Unit>> = emptyList()
) {

    fun title(title: String) = apply { this.title = title }
    fun icon(icon: Image) = apply { this.icon = icon }
    fun onClose(onClose: () -> Boolean) = apply { this.onClose = onClose }
    fun onAbout(onAbout: () -> Unit) = apply { this.onAbout = onAbout }
    fun stylesheets(stylesheets: Collection<String>) = apply { this.stylesheets = stylesheets }
    fun customLibraries(customLibraries: Collection<AliasedLibrary>) = apply { this.customLibraries = customLibraries }
    fun customTabs(customTabs: Collection<Pair<Tab, (PrologIDEModel) -> Unit>>) =
        apply { this.customTabs = customTabs }

    fun show() {
        val loader = FXMLLoader(javaClass.getResource("PrologIDEView.fxml"))
        val root = loader.load<Parent>()
        stage.scene = Scene(root)
        stage.title = this.title
        stage.icons.add(this.icon)
        stage.onCloseRequest = EventHandler { e -> if (!this.onClose()) e.consume() }
        stage.scene.stylesheets.addAll(this.stylesheets)

        val controller = loader.getController() as PrologIDEController
        controller.setOnAbout(this.onAbout)
        controller.setOnClose { if (this.onClose()) this.stage.close() }
        customTabs.forEach {
            controller.addTab(it.first)
        }

        controller.customizeModel {
            it.customizeSolver {
                customLibraries.forEach { lib -> it.loadLibrary(lib) }
            }
            customTabs.forEach { tab -> tab.second(it) }
        }

        this.stage.show()
    }
}
