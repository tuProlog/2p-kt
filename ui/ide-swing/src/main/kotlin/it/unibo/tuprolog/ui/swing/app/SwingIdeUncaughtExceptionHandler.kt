package it.unibo.tuprolog.ui.swing.app

import it.unibo.tuprolog.ui.swing.onEdt
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.JTextArea

private const val REPORT_AREA_ROWS = 24
private const val REPORT_AREA_COLUMNS = 100

/**
 * Catches any exception that would otherwise silently kill a thread (e.g. an EDT callback), showing it in a
 * dialog with a copyable stack trace instead - falling back to [fallback] (the JVM's previous handler) once the
 * IDE window itself is gone (e.g. during shutdown).
 */
internal class SwingIdeUncaughtExceptionHandler(
    private val frame: () -> SwingIdeFrame?,
    private val fallback: Thread.UncaughtExceptionHandler?,
) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(
        thread: Thread,
        error: Throwable,
    ) {
        onEdt {
            val ide = frame()
            if (ide == null) {
                fallback?.uncaughtException(thread, error)
            } else {
                val details = ide.supportReport(thread, error)
                val area = JTextArea(details, REPORT_AREA_ROWS, REPORT_AREA_COLUMNS).apply { caretPosition = 0 }
                JOptionPane.showMessageDialog(
                    ide,
                    JScrollPane(area),
                    "Unexpected error — please report it",
                    JOptionPane.ERROR_MESSAGE,
                )
            }
        }
    }

    internal companion object {
        fun stackTrace(error: Throwable): String =
            StringWriter().also { error.printStackTrace(PrintWriter(it)) }.toString()
    }
}
