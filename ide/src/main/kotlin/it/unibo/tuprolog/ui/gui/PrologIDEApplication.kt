package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.Info.VERSION
import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import javafx.stage.WindowEvent
import kotlin.system.exitProcess

class PrologIDEApplication : Application() {
    override fun start(stage: Stage) {
        try {
            val loader = FXMLLoader(javaClass.getResource("PrologIDEView.fxml"))
            val root = loader.load<Parent>()
            stage.title = "tuProlog IDE"
            stage.scene = Scene(root)
            stage.icons.add(TUPROLOG_LOGO)
            stage.onCloseRequest = EventHandler(this::onCloseRequest)
            stage.scene.stylesheets += JAVA_KEYWORDS_LIGHT
            stage.scene.stylesheets += LIGHT_CODE_AREA
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
        @JvmStatic
        fun main(args: Array<String>) {
            launch(PrologIDEApplication::class.java)
        }
    }
}
