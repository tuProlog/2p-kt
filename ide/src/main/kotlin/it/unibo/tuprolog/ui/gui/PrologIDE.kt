package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.Info.VERSION
import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.WindowEvent
import java.net.URL
import kotlin.system.exitProcess

class PrologIDE : Application() {
    override fun start(stage: Stage) {
        try {
            val loader = FXMLLoader(javaClass.getResource("PrologIDEView.fxml"))
            val root = loader.load<Parent>()
            stage.title = "tuProlog IDE v$VERSION"
            stage.scene = Scene(root)
            stage.icons.add(Image(TUPROLOG_LOGO.openStream()))
            stage.onCloseRequest = EventHandler(this::onCloseRequest)
            stage.scene.stylesheets += JAVA_KEYWORDS_LIGHT.toExternalForm()
            stage.scene.stylesheets += LIGHT_CODE_AREA.toExternalForm()
            stage.show()
        } catch (e: Throwable) {
            e.printStackTrace()
            throw Error(e)
        }
    }

    private fun onCloseRequest(e: WindowEvent) {
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        alert.title = "Close tuProlog IDE"
        alert.headerText = "Confirmation"
        alert.contentText = "Are you absolutely sure you wanna close the IDE?"
        alert.showAndWait().ifPresent {
            if (it != ButtonType.OK) {
                e.consume()
            }
        }
    }

    override fun stop() {
        exitProcess(0)
    }

    companion object {

        val JAVA_KEYWORDS_LIGHT: URL = PrologIDE::class.java.getResource("java-keywords-light.css")

        val JAVA_KEYWORDS_DARK: URL = PrologIDE::class.java.getResource("java-keywords-dark.css")

        val LIGHT_CODE_AREA: URL = PrologIDE::class.java.getResource("light-code-area.css")

        val DARK_CODE_AREA: URL = PrologIDE::class.java.getResource("dark-code-area.css")

        val TUPROLOG_LOGO: URL = PrologIDE::class.java.getResource("2p-logo.png")

        @JvmStatic
        fun main(args: Array<String>) {
            launch(PrologIDE::class.java)
        }
    }
}
