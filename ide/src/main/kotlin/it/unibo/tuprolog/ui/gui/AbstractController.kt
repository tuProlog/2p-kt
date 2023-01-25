package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.theory.Theory
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.text.Text
import java.time.Duration

abstract class AbstractController : Initializable {
    protected fun onUiThread(action: () -> Unit) = JavaFxRunner.ui(action)

    protected fun Theory.pretty(): String =
        if (isNonEmpty) {
            clauses.joinToString(".\n", postfix = ".") {
                it.format(TermFormatter.prettyExpressions())
            }
        } else {
            ""
        }

    @Suppress("Since15")
    protected fun Duration.pretty(): String {
        val milliseconds = toMillisPart()
        val seconds = toSecondsPart()
        val minutes = toMinutesPart()
        val hours = toHoursPart()
        val days = toDaysPart() % 365
        val years = toDaysPart() / 365
        return sequenceOf(
            years.pretty("y"),
            days.pretty("d"),
            hours.pretty("h"),
            minutes.pretty("m"),
            seconds.pretty("s"),
            milliseconds.pretty("ms")
        ).filterNotNull().joinToString()
    }

    private fun Long.pretty(unit: String): String? =
        if (this == 0L) null else "$this$unit"

    private fun Int.pretty(unit: String): String? = toLong().pretty(unit)

    protected fun String?.toMonospacedText(): Node {
        if (this == null) return Text()
        return Text(this).also { it.style = "-fx-font-family: monospaced" }
    }

    protected fun Tab.showNotification() {
        if (!isSelected && !text.endsWith("*")) {
            text += "*"
        }
    }

    protected fun Tab.hideNotification() {
        if (text.endsWith("*")) {
            text = text.substringBefore('*')
        }
    }
}